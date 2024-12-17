package com.codepath.articlesearch

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.Headers
import org.json.JSONException

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val popularArticles = mutableListOf<DisplayArticle>()
    private lateinit var articlesAdapter: ArticleAdapter
    private lateinit var swipeContainer: SwipeRefreshLayout

    companion object {
        private const val TAG = "HomeFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize SwipeRefreshLayout
        swipeContainer = view.findViewById(R.id.swipeContainer)
        swipeContainer.setOnRefreshListener {
            fetchPopularArticles()
        }

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.popularArticlesRecyclerView)
        articlesAdapter = ArticleAdapter(requireContext(), popularArticles)
        recyclerView.adapter = articlesAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Fetch popular articles initially
        fetchPopularArticles()

        return view
    }

    private fun fetchPopularArticles() {
        val client = AsyncHttpClient()
        val url =
            "https://api.nytimes.com/svc/mostpopular/v2/viewed/1.json?api-key=${BuildConfig.API_KEY}"

        client.get(url, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                // Log the raw API response for debugging
                Log.d(TAG, "API Response: ${json.jsonObject}")
                println("API Response: ${json.jsonObject}")
                try {
                    val parsedJson = Json {
                        ignoreUnknownKeys = true
                    }.decodeFromString<PopularArticlesResponse>(json.jsonObject.toString())

                    val displayArticles = parsedJson.results.map { article ->
                        DisplayArticle(
                            headline = article.title ?: "No Title",
                            abstract = article.abstract ?: "No Description",
                            byline = article.byline ?: "Unknown Author",
                            mediaImageUrl = article.mediaImageUrl
                        )
                    }

                    // Update the RecyclerView
                    popularArticles.clear()
                    popularArticles.addAll(displayArticles)
                    articlesAdapter.notifyDataSetChanged()

                    // Stop refreshing animation
                    swipeContainer.isRefreshing = false
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing response: ${e.message}")
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Error parsing articles", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                // Log the failure response and throwable for debugging
                Log.e(TAG, "API Fetch Failed: ${response ?: "No response"}")
                Log.e(TAG, "Error: ${throwable?.message}")
                throwable?.printStackTrace()
                Toast.makeText(requireContext(), "Failed to fetch articles", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}
