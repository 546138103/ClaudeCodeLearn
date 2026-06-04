package com.fittogether

import android.app.Application

class FitTogetherApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Firebase initialization would go here in production
        // FirebaseApp.initializeApp(this)
    }
}
