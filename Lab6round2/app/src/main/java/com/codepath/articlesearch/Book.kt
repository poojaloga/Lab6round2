import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookResponse(
    @SerialName("results") val results: BooksResults
)

@Serializable
data class BooksResults(
    @SerialName("books") val books: List<Book>
)

@Serializable
data class Book(
    @SerialName("title") val title: String,
    @SerialName("author") val author: String,
    @SerialName("description") val description: String,
    @SerialName("book_image") val coverImageUrl: String
)
