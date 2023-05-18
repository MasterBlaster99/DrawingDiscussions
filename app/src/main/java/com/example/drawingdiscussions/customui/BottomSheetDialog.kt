package com.example.drawingdiscussions.customui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.drawingdiscussions.databinding.MarkerBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetDialog(val isEditable: Boolean, val title: String?="",val des: String?="") : BottomSheetDialogFragment() {

    var onInfoAddedListener: OnInfoAddedListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val binding = MarkerBottomSheetBinding.inflate(inflater)

        if(!isEditable) {
            binding.buttonVerify.visibility = View.GONE
            binding.etDescription.isEnabled = false
            binding.etTitle.isEnabled = false
            binding.etTitle.setText(title)
            binding.etDescription.setText(des)
        }

        binding.buttonVerify.setOnClickListener{
            onInfoAddedListener!!.onMarkerAdded(binding.etTitle.text.toString().trim(),binding.etDescription.text.toString().trim())
        }

        return binding.root
    }
    interface OnInfoAddedListener {
        fun onMarkerAdded(title:String, des:String)
    }
    fun setOnMarkerAddedListener(listener: OnInfoAddedListener) {
        onInfoAddedListener = listener
    }
}