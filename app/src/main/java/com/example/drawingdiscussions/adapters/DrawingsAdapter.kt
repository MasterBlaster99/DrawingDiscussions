package com.example.drawingdiscussions.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.drawingdiscussions.R
import com.example.drawingdiscussions.databinding.ItemDrawingListBinding
import com.example.drawingdiscussions.model.Drawing
import com.example.drawingdiscussions.utils.Utils


class DrawingsAdapter(var mContext: Context, private val onClickListener: OnClickListener) :
    ListAdapter<Drawing, DrawingsAdapter.ViewHolder>(
        DiffCallback()
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDrawingListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = getItem(holder.adapterPosition)
        holder.binding.apply {
            Glide.with(mContext).load(item.imageUrl).placeholder(R.drawable.blue_brush).into(
                ivDrawingImage
            )
            tvDrawingTitle.text = item.title
            tvTime.text = Utils.getTimeAgo(item.timeStamp!!.toLong())
            tvMarkers.text = item.markersList!!.size.toString()
            root.setOnClickListener {
                onClickListener.onDrawingClick(item)
            }
        }
    }

    inner class ViewHolder(val binding: ItemDrawingListBinding) :
        RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<Drawing>() {
        override fun areItemsTheSame(oldItem: Drawing, newItem: Drawing) = oldItem == newItem
        override fun areContentsTheSame(oldItem: Drawing, newItem: Drawing) = oldItem == newItem
    }

    interface OnClickListener {
        fun onDrawingClick(drawing: Drawing)
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

}