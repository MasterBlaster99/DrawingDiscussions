package com.example.drawingdiscussions.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.drawingdiscussions.model.Drawing
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class DrawingsRepository @Inject constructor() {

    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _drawingsLiveData = MutableLiveData<List<Drawing>>()
    val drawingsLiveData: LiveData<List<Drawing>>
        get() = _drawingsLiveData

    fun getDrawings(): LiveData<List<Drawing>> {
        fireStore.collection("drawings")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Handle the error
                } else {
                    val drawingsList = mutableListOf<Drawing>()
                    for (document in snapshot?.documents ?: emptyList()) {
                        val drawing = document.toObject(Drawing::class.java)
                        drawing?.let { drawingsList.add(it) }
                    }
                    _drawingsLiveData.value = drawingsList
                }
            }
        return drawingsLiveData
    }

    fun uploadDrawing(drawing: Drawing) {
        fireStore.collection("drawings")
            .add(drawing)
            .addOnSuccessListener {
            }
            .addOnFailureListener { exception ->
            }
    }

}