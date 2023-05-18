package com.example.drawingdiscussions.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.drawingdiscussions.MyApp
import com.example.drawingdiscussions.R
import com.example.drawingdiscussions.adapters.DrawingsAdapter
import com.example.drawingdiscussions.databinding.FragmentHomeBinding
import com.example.drawingdiscussions.model.Drawing
import com.example.drawingdiscussions.viewmodel.DrawingViewModelFactory
import com.example.drawingdiscussions.viewmodel.DrawingsViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), DrawingsAdapter.OnClickListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: DrawingsViewModel
    private lateinit var adapter: DrawingsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)

        setUpViews()
        setUpViewModel()
        setUpObservers()
        setUpListeners()

        return binding.root
    }

    override fun onDrawingClick(drawing: Drawing) {
        val bundle = Bundle()
        bundle.putString("imgUrl", drawing.imageUrl)
        bundle.putString("title", drawing.title)
        bundle.putString("timestamp", drawing.timeStamp)
        bundle.putSerializable("markersList", drawing.markersList)
        findNavController().navigate(
            HomeFragmentDirections
                .actionHomeFragmentToFragmentDrawingView(bundle)
        )
    }

    private fun setUpViews() {
        val verticalDecoration = DividerItemDecoration(
            requireContext(),
            DividerItemDecoration.VERTICAL
        )
        val verticalDivider =
            ContextCompat.getDrawable(requireContext(), R.drawable.divider)
        verticalDecoration.setDrawable(verticalDivider!!)
        binding.rvDrawings.addItemDecoration(verticalDecoration)
        adapter = DrawingsAdapter(requireContext(), this)
        binding.rvDrawings.adapter = adapter
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(requireActivity()).get(DrawingsViewModel::class.java)
//        viewModel = ViewModelProvider(
//            this,
//            DrawingViewModelFactory((requireActivity().application as MyApp).repository)
//        )[DrawingsViewModel::class.java]
        viewModel.getDrawings()
    }

    private fun setUpObservers() {
        viewModel.drawingsLiveData.observe(requireActivity()) {
            if (it != null)
                adapter.submitList(it as ArrayList<Drawing>)
        }
    }

    private fun setUpListeners() {
        binding.addDrawing.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddImageFragment())
        }
    }
}