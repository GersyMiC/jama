package com.example.jama_fv.ui.client

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jama_fv.R
import com.example.jama_fv.data.model.Review

class ReviewAdapter(private val reviews: MutableList<Review>) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userNameTextView: TextView = itemView.findViewById(R.id.tv_review_user_name)
        val ratingBar: RatingBar = itemView.findViewById(R.id.rating_bar_display)
        val commentTextView: TextView = itemView.findViewById(R.id.tv_review_comment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.userNameTextView.text = review.userName
        holder.ratingBar.rating = review.rating.toFloat()
        holder.commentTextView.text = review.comment
    }

    override fun getItemCount(): Int = reviews.size

    fun updateReviews(newReviews: List<Review>) {
        reviews.clear()
        reviews.addAll(newReviews)
        notifyDataSetChanged() // Notifica al RecyclerView que los datos han cambiado
    }
}