package com.example.drawingdiscussions.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawingdiscussions.model.Drawing
import com.example.drawingdiscussions.repository.DrawingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrawingsViewModel @Inject constructor( private val drawingsRepository: DrawingsRepository) : ViewModel() {

    val drawingsLiveData: LiveData<List<Drawing>>
        get() = drawingsRepository.drawingsLiveData

    fun getDrawings() {
        viewModelScope.launch(Dispatchers.IO) {
            drawingsRepository.getDrawings()
        }
    }

    fun uploadDrawing(drawing: Drawing) {
        viewModelScope.launch(Dispatchers.IO) {
            drawingsRepository.uploadDrawing(drawing)
        }
    }

}