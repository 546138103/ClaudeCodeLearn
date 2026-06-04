package com.fittogether.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fittogether.data.local.AppDatabase
import com.fittogether.data.model.*
import com.fittogether.data.repository.FitRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    val repository = FitRepository(db)

    private val _currentUserId = MutableStateFlow("user_self")

    // ── Auth state ──
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // ── Current user ──
    val currentUser: StateFlow<User?> = _currentUserId
        .flatMapLatest { uid -> repository.observeUser(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // ── Partner ──
    val partner: StateFlow<User?> = currentUser
        .flatMapLatest { user ->
            user?.partnerId?.let { repository.observeUser(it) } ?: flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // ── Categories ──
    val categories: StateFlow<List<CheckInCategory>> = _currentUserId
        .flatMapLatest { uid -> repository.observeCategories(uid) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ── Today's records ──
    private val todayDate = MutableStateFlow(today())
    val todayRecords: StateFlow<List<CheckInRecord>> = combine(_currentUserId, todayDate) { uid, date ->
        uid to date
    }.flatMapLatest { (uid, date) -> repository.observeTodayRecords(uid, date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ── Month records (calendar) ──
    private val currentMonth = MutableStateFlow(todayMonth())
    val monthRecords: StateFlow<List<CheckInRecord>> = combine(_currentUserId, currentMonth) { uid, month ->
        uid to month
    }.flatMapLatest { (uid, month) -> repository.observeMonthRecords(uid, month) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ── Cheer history ──
    val cheerHistory: StateFlow<List<CheerRecord>> = combine(
        _currentUserId,
        currentUser.filterNotNull()
    ) { uid, user ->
        user.partnerId?.let { pid -> listOf(uid, pid) } ?: listOf(uid, uid)
    }.flatMapLatest { (uid1, uid2) ->
        repository.observeCheerHistory(uid1, uid2)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ── UI state ──
    private val _showConfetti = MutableStateFlow(false)
    val showConfetti: StateFlow<Boolean> = _showConfetti.asStateFlow()

    private val _showHeartParticles = MutableStateFlow(false)
    val showHeartParticles: StateFlow<Boolean> = _showHeartParticles.asStateFlow()

    private val _showHighFive = MutableStateFlow(false)
    val showHighFive: StateFlow<Boolean> = _showHighFive.asStateFlow()

    private val _showFireworks = MutableStateFlow(false)
    val showFireworks: StateFlow<Boolean> = _showFireworks.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensurePresetCategories()
        }
    }

    // ── Auth actions ──
    fun login(phone: String) {
        viewModelScope.launch {
            val existing = repository.getUser("user_self")
            if (existing == null) {
                val user = User(
                    uid = "user_self",
                    phone = phone,
                    nickname = "健身达人"
                )
                repository.insertUser(user)
            }
            _isLoggedIn.value = true
        }
    }

    fun register(phone: String, nickname: String) {
        viewModelScope.launch {
            val user = User(
                uid = "user_self",
                phone = phone,
                nickname = nickname
            )
            repository.insertUser(user)
            _isLoggedIn.value = true
        }
    }

    // ── Check-in actions ──
    fun doCheckIn(categoryId: Int, value: Float, unit: String, note: String, mood: String, photoUrl: String = "") {
        viewModelScope.launch {
            val record = CheckInRecord(
                userId = _currentUserId.value,
                categoryId = categoryId,
                date = today(),
                value = value,
                unit = unit,
                note = note,
                mood = mood,
                photoUrl = photoUrl
            )
            repository.insertRecord(record)
            _showConfetti.value = true
        }
    }

    fun dismissConfetti() { _showConfetti.value = false }

    // ── Partner actions ──
    fun bindPartner(inviteCode: String) {
        viewModelScope.launch {
            // In a real app, look up partner by invite code via backend
            // For demo, bind directly
            val partnerUser = User(
                uid = "user_partner",
                phone = "",
                nickname = "Ta",
                partnerId = _currentUserId.value
            )
            repository.insertUser(partnerUser)
            repository.bindPartner(_currentUserId.value, "user_partner")

            // Also update current user
            val user = repository.getUser(_currentUserId.value)
            if (user != null) {
                repository.updateUser(user.copy(partnerId = "user_partner"))
            }
        }
    }

    fun sendCheer(type: String, emoji: String = "") {
        viewModelScope.launch {
            val partnerId = currentUser.value?.partnerId ?: return@launch
            val cheer = CheerRecord(
                fromUserId = _currentUserId.value,
                toUserId = partnerId,
                type = type,
                emoji = emoji
            )
            repository.insertCheer(cheer)
            _showHeartParticles.value = true
        }
    }

    fun dismissHeartParticles() { _showHeartParticles.value = false }

    fun triggerHighFive() { _showHighFive.value = true }
    fun dismissHighFive() { _showHighFive.value = false }

    fun triggerFireworks() { _showFireworks.value = true }
    fun dismissFireworks() { _showFireworks.value = false }

    // ── Category management ──
    fun addCustomCategory(name: String, icon: String, color: Long) {
        viewModelScope.launch {
            val cat = CheckInCategory(
                name = name, icon = icon, color = color,
                isCustom = true, userId = _currentUserId.value
            )
            repository.insertCategory(cat)
        }
    }

    fun deleteCustomCategory(category: CheckInCategory) {
        viewModelScope.launch {
            if (category.isCustom) {
                repository.deleteCategory(category)
            }
        }
    }

    // ── Profile ──
    fun updateProfile(nickname: String, avatar: String, signature: String, weeklyGoal: Int) {
        viewModelScope.launch {
            repository.updateProfile(_currentUserId.value, nickname, avatar, signature, weeklyGoal)
        }
    }

    // ── Stats helpers ──
    suspend fun getWeekCheckInCount(): Int = repository.getWeekCheckInCount(_currentUserId.value)

    suspend fun getTotalCheckInDays(): Int = repository.getTotalCheckInDays(_currentUserId.value)

    companion object {
        fun today(): String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        fun todayMonth(): String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"))
    }
}
