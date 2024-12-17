package com.codepath.articlesearch

import BookResponse
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

class BooksFragment : Fragment(), NetworkStatusListener {

    private lateinit var recyclerView: RecyclerView
    private val books = mutableListOf<DisplayBook>()
    private lateinit var booksAdapter: BooksAdapter
    private lateinit var swipeContainer: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_books, container, false)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.booksRecyclerView)
        booksAdapter = BooksAdapter(requireContext(), books)
        recyclerView.adapter = booksAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize SwipeRefreshLayout
        swipeContainer = view.findViewById(R.id.swipeContainer)
        swipeContainer.setOnRefreshListener {
            fetchBooks()
        }
        swipeContainer.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

        // Fetch books initially
        fetchBooks()

        return view
    }

    fun fetchBooks() {
        val client = AsyncHttpClient()
        val url = "https://api.nytimes.com/svc/books/v3/lists/current/hardcover-fiction.json?api-key=${BuildConfig.API_KEY}"

        client.get(url, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                try {
                    val parsedJson = Json {
                        ignoreUnknownKeys = true
                    }.decodeFromString<BookResponse>(json.jsonObject.toString())

                    val displayBooks = parsedJson.results.books.map { book ->
                        DisplayBook(
                            title = book.title,
                            description = book.description,
                            author = book.author,
                            coverImageUrl = book.coverImageUrl
                        )
                    }

                    books.clear()
                    books.addAll(displayBooks)
                    booksAdapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Error parsing book data", Toast.LENGTH_SHORT).show()
                } finally {
                    swipeContainer.isRefreshing = false
                }
            }

            override fun onFailure(statusCode: Int, headers: Headers?, response: String?, throwable: Throwable?) {
                throwable?.printStackTrace()
                swipeContainer.isRefreshing = false
                Toast.makeText(requireContext(), "Failed to load books", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onNetworkConnected() {
        fetchBooks()
    }

    override fun onNetworkDisconnected() {
        Toast.makeText(context, "Network disconnected. Please check your connection.", Toast.LENGTH_SHORT).show()
    }
}
