package com.example.vxsound.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vxsound.adapter.AdminFeedbackAdapter.AdminFeedbackViewHolder
import com.example.vxsound.databinding.ItemFeedbackBinding
import com.example.vxsound.model.Feedback

class AdminFeedbackAdapter(private val mListFeedback: List<Feedback>?) :
    RecyclerView.Adapter<AdminFeedbackViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminFeedbackViewHolder {
        val itemFeedbackBinding = ItemFeedbackBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return AdminFeedbackViewHolder(itemFeedbackBinding)
    }

    override fun onBindViewHolder(holder: AdminFeedbackViewHolder, position: Int) {
        val feedback = mListFeedback!![position]
        holder.mItemFeedbackBinding.tvEmail.text = feedback.email
        holder.mItemFeedbackBinding.tvFeedback.text = feedback.comment
    }

    override fun getItemCount(): Int {
        return mListFeedback?.size ?: 0
    }

    class AdminFeedbackViewHolder(val mItemFeedbackBinding: ItemFeedbackBinding) :
        RecyclerView.ViewHolder(mItemFeedbackBinding.root)
}