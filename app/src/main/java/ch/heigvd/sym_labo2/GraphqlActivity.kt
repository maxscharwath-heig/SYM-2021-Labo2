package ch.heigvd.sym_labo2

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class GraphqlActivity : AppCompatActivity() {
    private lateinit var authorSpinner: Spinner
    private lateinit var bookListView: ListView
    private val mcm: SymComManager = SymComManager()

    data class Query(val query: String);

    data class Book(
        val id: String,
        val title: String,
        val isbn13: String,
        val languageCode: String,
        val numPages: Int,
        val publicationDate: String,
        val publisher: String,
        val textReviewsCount: Int,
        val averageRating: Float
    ) {
        override fun toString(): String {
            return title
        }
    }

    data class Author(val id: String, val name: String, val books: ArrayList<Book>) {
        override fun toString(): String {
            return name
        }
    };
    data class AuthorsResponse(
        val data: Data,
    ) {
        data class Data(
            val authors: ArrayList<Author>
        )
    }
    data class AuthorResponse(
        val data: Data,
    ) {
        data class Data(
            val author: Author
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graphql)
        authorSpinner = findViewById(R.id.spinner_author)
        bookListView = findViewById(R.id.list_book)
        val authorAdapter = ArrayAdapter<Author>(
            this@GraphqlActivity, android.R.layout.simple_list_item_1
        );
        authorSpinner.adapter = authorAdapter;
        authorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val author = authorSpinner.getItemAtPosition(position) as Author;
                getAuthorBooks(author.id);
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        mcm.setCommunicationListener(object : CommunicationEventListener {
            override fun handleServerResponse(response: ByteArray) {
                val result = Gson().fromJson(response.decodeToString(), AuthorsResponse::class.java)
                authorAdapter.clear();
                authorAdapter.addAll(result.data.authors);
                Toast.makeText(
                    applicationContext,
                    "Found ${result.data.authors.size} authors",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        val json = Gson().toJson(Query("{authors:findAllAuthors{id, name, books{id}}}"))
        mcm.sendRequest("http://mobile.iict.ch/graphql", json.toByteArray(), "application/json")
    }

    private fun getAuthorBooks(authorId:String){
        mcm.setCommunicationListener(object : CommunicationEventListener {
            override fun handleServerResponse(response: ByteArray) {
                val result = Gson().fromJson(response.decodeToString(), AuthorResponse::class.java)
                Toast.makeText(
                    applicationContext,
                    "Found ${result.data.author.books.size} books",
                    Toast.LENGTH_SHORT
                ).show()
                val adapter: ArrayAdapter<Book> = ArrayAdapter(
                    this@GraphqlActivity,
                    android.R.layout.simple_list_item_1,
                    result.data.author.books
                )
                bookListView.adapter = adapter;
                bookListView.setOnItemClickListener { parent, view, position, id ->
                    val book = bookListView.getItemAtPosition(position) as Book;
                }

            }
        })
        val json = Gson().toJson(Query("{author:findAuthorById(id: $authorId){books{id, title}}}"))

        mcm.sendRequest("http://mobile.iict.ch/graphql",json.toByteArray(), "application/json")
    }
}