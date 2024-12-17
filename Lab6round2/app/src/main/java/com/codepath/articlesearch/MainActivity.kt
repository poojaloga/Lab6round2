package com.codepath.articlesearch

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.codepath.articlesearch.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), NetworkStatusListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var networkReceiver: NetworkReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout and set content view
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up BottomNavigationView to switch between fragments
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    supportActionBar?.title = "Home"
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment()) // Load HomeFragment
                        .commit()
                    true
                }
                R.id.nav_books -> {
                    supportActionBar?.title = "Books"
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, BooksFragment()) // Load BooksFragment
                        .commit()
                    true
                }
                R.id.nav_articles -> {
                    supportActionBar?.title = "Articles"
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ArticlesFragment()) // Load ArticlesFragment
                        .commit()
                    true
                }
                else -> false
            }
        }

        // Set default fragment on first launch
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ArticlesFragment()) // Default to ArticlesFragment
                .commit()
            supportActionBar?.title = "Home"
        }

        // Register network receiver (if applicable)
        networkReceiver = NetworkReceiver()
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, intentFilter)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save any data you want to persist
        outState.putString("key", "value") // Example
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Restore the data
        val value = savedInstanceState.getString("key") // Example
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the network receiver
        unregisterReceiver(networkReceiver)
    }

    // Implementing NetworkStatusListener interface
    override fun onNetworkConnected() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        when (currentFragment) {
            is ArticlesFragment -> currentFragment.fetchArticles()
            is BooksFragment -> currentFragment.fetchBooks() // Implement fetchBooks in BooksFragment
            else -> {
                Toast.makeText(this, "Network connected, but no data fetched", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onNetworkDisconnected() {
        Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
    }
}
