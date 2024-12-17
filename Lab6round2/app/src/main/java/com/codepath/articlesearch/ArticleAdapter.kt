package com.codepath.articlesearch

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codepath.articlesearch.DisplayArticle

const val ARTICLE_EXTRA = "ARTICLE_EXTRA"
private const val TAG = "ArticleAdapter"

class ArticleAdapter(
    private val context: Context,
    private val articles: List<DisplayArticle>,
    private val isHomeScreen: Boolean = false // Add a flag to differentiate layouts
) : RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Use different layouts based on the isHomeScreen flag
        val layoutId = if (isHomeScreen) R.layout.item_home else R.layout.item_article
        val view = LayoutInflater.from(context).inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]
        holder.bind(article)
    }

    override fun getItemCount() = articles.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val mediaImageView: ImageView? = itemView.findViewById(R.id.mediaImage)
        private val titleTextView: TextView? = itemView.findViewById(R.id.articleTitle)
        private val abstractTextView: TextView? = itemView.findViewById(R.id.articleAbstract)
        private val bylineTextView: TextView? = itemView.findViewById(R.id.articleByline)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(article: DisplayArticle) {
            titleTextView?.text = article.headline
            abstractTextView?.text = article.abstract
            bylineTextView?.text = article.byline

            // Load the image with placeholder and error handling
            mediaImageView?.let {
                Glide.with(context)
                    .load(article.mediaImageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(it)
            }
        }

        override fun onClick(v: View?) {
            val article = articles[adapterPosition]
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(ARTICLE_EXTRA, article)
            context.startActivity(intent)
        }
    }
}


