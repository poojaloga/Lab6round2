package com.codepath.articlesearch

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codepath.articlesearch.databinding.ItemBookBinding

class BooksAdapter(private val context: Context, private val books: List<DisplayBook>) :
    RecyclerView.Adapter<BooksAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemBookBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(book: DisplayBook) {
            binding.bookTitle.text = book.title
            binding.bookAuthor.text = book.author
            binding.bookDescription.text = book.description
            Glide.with(context).load(book.coverImageUrl).into(binding.bookImage)

            // Set a click listener to open the detail activity
            binding.root.setOnClickListener {
                val intent = Intent(context, BookDetailActivity::class.java)
                intent.putExtra("title", book.title)
                intent.putExtra("author", book.author)
                intent.putExtra("description", book.description)
                intent.putExtra("coverImageUrl", book.coverImageUrl)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount() = books.size
}

