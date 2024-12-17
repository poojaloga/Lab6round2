package com.codepath.articlesearch

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookDao {
    @Query("SELECT * FROM books")
    fun getAllBooks(): List<BookEntity>

    @Insert
    fun insertBooks(books: List<BookEntity>)

    @Query("DELETE FROM books")
    fun deleteAllBooks()
}
