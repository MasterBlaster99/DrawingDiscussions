package com.example.drawingdiscussions.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.drawingdiscussions.repository.DrawingsRepository

class DrawingViewModelFactory(private val genresRepository: DrawingsRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DrawingsViewModel(genresRepository) as T
    }
}