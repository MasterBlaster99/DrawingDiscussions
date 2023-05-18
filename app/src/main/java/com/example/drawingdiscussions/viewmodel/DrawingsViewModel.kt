package com.example.drawingdiscussions.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.drawingdiscussions.model.Drawing
import com.example.drawingdiscussions.repository.DrawingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DrawingsViewModel @Inject constructor( private val drawingsRepository: DrawingsRepository) : ViewModel() {

    val drawingsLiveData: LiveData<List<Drawing>>
        get() = drawingsRepository.drawingsLiveData

    fun getDrawings() {
        drawingsRepository.getDrawings()
    }

    fun uploadDrawing(drawing: Drawing) {
        drawingsRepository.uploadDrawing(drawing)
    }

}