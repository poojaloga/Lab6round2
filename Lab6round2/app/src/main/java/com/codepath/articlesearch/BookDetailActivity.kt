package com.codepath.articlesearch

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.codepath.articlesearch.databinding.ActivityBookDetailBinding

class BookDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Extract the passed book data from the intent
        val title = intent.getStringExtra("title") ?: "No Title"
        val author = intent.getStringExtra("author") ?: "No Author"
        val description = intent.getStringExtra("description") ?: "No Description"
        val coverImageUrl = intent.getStringExtra("coverImageUrl") ?: ""

        // Populate the UI with book data
        binding.bookTitle.text = title
        binding.bookAuthor.text = author
        binding.bookDescription.text = description

        // Load the image using Glide
        Glide.with(this).load(coverImageUrl).into(binding.bookImage)
    }
}
