package com.example.drawingdiscussions.fragments

import android.graphics.PointF
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.drawingdiscussions.R
import com.example.drawingdiscussions.customui.BottomSheetDialog
import com.example.drawingdiscussions.databinding.FragmentDrawingViewBinding
import com.example.drawingdiscussions.customui.ZoomClass
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FragmentDrawingView : Fragment() {

    private lateinit var binding: FragmentDrawingViewBinding
    private lateinit var markersList:ArrayList<HashMap<String,String>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDrawingViewBinding.inflate(inflater)

        //Initializing Args
        val drawing = navArgs<FragmentDrawingViewArgs>().value.drawing
        markersList = drawing.getSerializable("markersList") as ArrayList<HashMap<String, String>>

        // UI Setup
        Glide.with(requireActivity()).load(drawing.getString("imgUrl"))
            .placeholder(R.drawable.blue_brush).into(binding.ivCustom)
        binding.toolbarDrawingView.title.text = drawing.getString("title")

        // Adding all the markers at their position
        for (i in markersList){
            PointF(i["x"]!!.toFloat(), i["y"]!!.toFloat())
            binding.ivCustom.addMarkerPoint(PointF(i["x"]!!.toFloat(), i["y"]!!.toFloat()))
        }

        setUpListeners()

        return binding.root
    }

    private fun setUpListeners(){
        binding.ivCustom.setOnMarkerClickListener(object : ZoomClass.OnMarkerClickListener{
            override fun onMarkerClicked(i: Int) {
                val title = markersList[i]["title"]
                val des = markersList[i]["des"]
                val bottomSheet = BottomSheetDialog(false,title,des)
                bottomSheet.show(activity!!.supportFragmentManager, "ModalBottomSheet")
                binding.ivCustom.invalidate()
            }
        })
    }

}