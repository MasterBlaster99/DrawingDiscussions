package com.example.drawingdiscussions

import android.app.Application
import com.example.drawingdiscussions.repository.DrawingsRepository
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {
//    lateinit var repository: DrawingsRepository
    override fun onCreate() {
        super.onCreate()
//        repository = DrawingsRepository()
    }
}