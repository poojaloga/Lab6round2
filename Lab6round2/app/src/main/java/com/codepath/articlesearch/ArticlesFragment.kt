package com.codepath.articlesearch

import android.os.Bundle
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

class ArticlesFragment : Fragment(), NetworkStatusListener {

    private lateinit var recyclerView: RecyclerView
    private val articles = mutableListOf<DisplayArticle>()
    private lateinit var articlesAdapter: ArticleAdapter
    private lateinit var swipeContainer: SwipeRefreshLayout // Define swipeContainer at the class level

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_articles, container, false)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.articlesRecyclerView)
        articlesAdapter = ArticleAdapter(requireContext(), articles)
        recyclerView.adapter = articlesAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize SwipeRefreshLayout
        swipeContainer = view.findViewById(R.id.swipeContainer) // Assign to the class-level property

        swipeContainer.setOnRefreshListener {
            fetchArticles() // Refresh articles when swiped down
        }

        swipeContainer.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

        // Fetch articles initially
        fetchArticles()

        return view
    }

    fun fetchArticles() {
        val client = AsyncHttpClient()
        val url = "https://api.nytimes.com/svc/search/v2/articlesearch.json?api-key=${BuildConfig.API_KEY}"

        client.get(url, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                try {
                    val parsedJson = Json {
                        ignoreUnknownKeys = true
                    }.decodeFromString<SearchNewsResponse>(json.jsonObject.toString())

                    val displayArticles = parsedJson.response?.docs?.map { article ->
                        DisplayArticle(
                            headline = article.headline?.main ?: "No Title",
                            abstract = article.abstract ?: "No Abstract",
                            byline = article.byline?.original ?: "No Byline",
                            mediaImageUrl = article.mediaImageUrl ?: ""
                        )
                    } ?: emptyList()

                    articles.clear()
                    articles.addAll(displayArticles)
                    articlesAdapter.notifyDataSetChanged()

                    // Stop the loading icon
                    swipeContainer.isRefreshing = false
                } catch (e: JSONException) {
                    e.printStackTrace()
                    swipeContainer.isRefreshing = false // Stop even on failure
                }
            }

            override fun onFailure(statusCode: Int, headers: Headers?, response: String?, throwable: Throwable?) {
                throwable?.printStackTrace()
                swipeContainer.isRefreshing = false // Stop even on failure
            }
        })
    }

    override fun onNetworkConnected() {
        // Called when network is connected
        fetchArticles()
    }

    override fun onNetworkDisconnected() {
        // Called when network is disconnected
        Toast.makeText(context, "Network disconnected. Please check your connection.", Toast.LENGTH_SHORT).show()
    }
}

