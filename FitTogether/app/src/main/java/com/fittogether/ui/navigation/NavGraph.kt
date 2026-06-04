package com.fittogether.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fittogether.ui.screens.bind.BindScreen
import com.fittogether.ui.screens.calendar.CalendarScreen
import com.fittogether.ui.screens.checkin.CheckInScreen
import com.fittogether.ui.screens.couple.CoupleSpaceScreen
import com.fittogether.ui.screens.home.HomeScreen
import com.fittogether.ui.screens.login.LoginScreen
import com.fittogether.ui.screens.profile.ProfileScreen
import com.fittogether.ui.screens.splash.SplashScreen
import com.fittogether.viewmodel.MainViewModel
import kotlinx.coroutines.launch

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val BIND = "bind"
    const val HOME = "home"
    const val CHECKIN = "checkin/{categoryId}"
    const val CALENDAR = "calendar"
    const val COUPLE = "couple"
    const val PROFILE = "profile"

    fun checkin(categoryId: Int) = "checkin/$categoryId"
}

@Composable
fun FitTogetherNavGraph(
    navController: NavHostController,
    viewModel: MainViewModel = viewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val partner by viewModel.partner.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val todayRecords by viewModel.todayRecords.collectAsState()
    val monthRecords by viewModel.monthRecords.collectAsState()
    val cheerHistory by viewModel.cheerHistory.collectAsState()
    val showConfetti by viewModel.showConfetti.collectAsState()
    val showHeartParticles by viewModel.showHeartParticles.collectAsState()
    val showHighFive by viewModel.showHighFive.collectAsState()
    val showFireworks by viewModel.showFireworks.collectAsState()

    val scope = rememberCoroutineScope()

    val startDestination = Routes.SPLASH

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLogin = { phone ->
                    viewModel.login(phone)
                    navController.navigate(Routes.BIND) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onRegister = { phone, nickname ->
                    viewModel.register(phone, nickname)
                    navController.navigate(Routes.BIND) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.BIND) {
            BindScreen(
                onBind = { code ->
                    viewModel.bindPartner(code)
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.BIND) { inclusive = true }
                    }
                },
                onSkip = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.BIND) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                user = currentUser,
                partner = partner,
                categories = categories,
                todayRecords = todayRecords,
                showConfetti = showConfetti,
                showHeartParticles = showHeartParticles,
                streakDays = 0, // Will be computed
                weekProgress = 0f, // Will be computed
                onCategoryClick = { category ->
                    navController.navigate(Routes.checkin(category.id))
                },
                onDismissConfetti = { viewModel.dismissConfetti() },
                onDismissHeartParticles = { viewModel.dismissHeartParticles() },
                onCheerClick = { type -> viewModel.sendCheer(type) },
                onNavigateToCalendar = { navController.navigate(Routes.CALENDAR) },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
                onNavigateToCouple = { navController.navigate(Routes.COUPLE) }
            )
        }

        composable(
            route = Routes.CHECKIN,
            arguments = listOf(navArgument("categoryId") { type = NavType.IntType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: 0
            val category = categories.find { it.id == categoryId }

            if (category != null) {
                CheckInScreen(
                    category = category,
                    onCheckIn = { value, unit, note, mood, photoUrl ->
                        viewModel.doCheckIn(categoryId, value, unit, note, mood, photoUrl)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Routes.CALENDAR) {
            CalendarScreen(
                monthRecords = monthRecords,
                categories = categories,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.COUPLE) {
            CoupleSpaceScreen(
                user = currentUser,
                partner = partner,
                cheerHistory = cheerHistory,
                showHearts = showHeartParticles,
                showHighFive = showHighFive,
                showFireworks = showFireworks,
                onCheer = { type -> viewModel.sendCheer(type) },
                onPoke = { viewModel.sendCheer("poke") },
                onDismissHearts = { viewModel.dismissHeartParticles() },
                onDismissHighFive = { viewModel.dismissHighFive() },
                onDismissFireworks = { viewModel.dismissFireworks() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                user = currentUser,
                categories = categories,
                totalDays = 0,
                weekCount = 0,
                onUpdateProfile = { nickname, avatar, signature, goal ->
                    viewModel.updateProfile(nickname, avatar, signature, goal)
                },
                onAddCategory = { name, icon, color ->
                    viewModel.addCustomCategory(name, icon, color)
                },
                onDeleteCategory = { category ->
                    viewModel.deleteCustomCategory(category)
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

