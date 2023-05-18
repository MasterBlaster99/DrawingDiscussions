package com.example.drawingdiscussions.fragments

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.PointF
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.drawingdiscussions.customui.BottomSheetDialog
import com.example.drawingdiscussions.databinding.FragmentAddImageBinding
import com.example.drawingdiscussions.model.Drawing
import com.example.drawingdiscussions.customui.ZoomClass
import com.example.drawingdiscussions.viewmodel.DrawingsViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@AndroidEntryPoint
class AddImageFragment : Fragment() {
    private lateinit var binding: FragmentAddImageBinding
    private var isImagedAdded = false
    private lateinit var pointersList: ArrayList<HashMap<String, String>>
    private lateinit var viewModel: DrawingsViewModel
    private lateinit var uri: String
    private lateinit var imagePickContract: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        imagePickContract =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                val iv = binding.ivCustom
                if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                    binding.ivCustom.setImageURI(it.data!!.data)
                    binding.tvNoImage.visibility = View.GONE
                    isImagedAdded = true
                    iv.isImageAdded = true
                    uri = it.data!!.data.toString()
                } else {
                    isImagedAdded = false
                    iv.isImageAdded = false
                }
            }
        binding = FragmentAddImageBinding.inflate(inflater)
        pointersList = ArrayList()
        binding.toolbarAddImage.title.text="New Drawing"

        setUpViewModel()
        setUpListeners()

        return binding.root
    }

    private fun uploadImage(uri: Uri): String {
        val path2 = UUID.randomUUID().toString()
        val path = "drawings/$path2"
        FirebaseStorage.getInstance()
        val storageReference = FirebaseStorage.getInstance().reference
        val ref: StorageReference = storageReference
            .child(
                path
            )
        ref.putFile(uri)
        return "https://firebasestorage.googleapis.com/v0/b/drawingdiscussions.appspot.com/o/drawings%2F$path2?alt=media&token=f1998695-87e9-40fb-888d-e3dc44177c0d"
    }

    private fun setUpListeners() {
        binding.btnSaveDrawing.setOnClickListener {
            if (isImagedAdded) {
                if (binding.etTitle.text.toString().trim().isEmpty())
                    viewModel.uploadDrawing(
                        (Drawing(
                            "Untitled",
                            uploadImage(Uri.parse(uri)),
                            System.currentTimeMillis().toString(),
                            pointersList
                        ))
                    )
                else
                    viewModel.uploadDrawing(
                        (Drawing(
                            binding.etTitle.text.toString().trim(),
                            uploadImage(Uri.parse(uri)),
                            System.currentTimeMillis().toString(),
                            pointersList
                        ))
                    )
                findNavController().popBackStack()
            }
        }

        binding.ivCustom.setOnMarkerAddedListener(object : ZoomClass.OnMarkerAddedListener {
            override fun onMarkerAdded(pointF: PointF) {
                val bottomSheet = BottomSheetDialog(true)
                bottomSheet.show(activity!!.supportFragmentManager, "ModalBottomSheet")
                bottomSheet.setOnMarkerAddedListener(object :
                    BottomSheetDialog.OnInfoAddedListener {
                    override fun onMarkerAdded(title: String, des: String) {
                        val map = HashMap<String, String>()
                        map["x"] = pointF.x.toString()
                        map["y"] = pointF.y.toString()
                        map["title"] = title
                        map["des"] = des
                        pointersList.add(map)
                        bottomSheet.dismiss()
                        Log.d(TAG, "onMarkerAdded: $pointersList")
                    }

                })

            }
        })

        binding.addImage.setOnClickListener {
            imagePickContract.launch(
                Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
            )
        }
        binding.ivCustom.setOnClickListener {
            if (!isImagedAdded) {
                imagePickContract.launch(
                    Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                )
            }
        }
    }

    private fun setUpViewModel(){
        viewModel = ViewModelProvider(requireActivity()).get(DrawingsViewModel::class.java)
//        viewModel = ViewModelProvider(
//            this,
//            DrawingViewModelFactory((requireActivity().application as MyApp).repository)
//        )[DrawingsViewModel::class.java]
    }
}