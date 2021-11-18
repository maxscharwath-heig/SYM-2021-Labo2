package ch.heigvd.sym_labo2

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import ch.heigvd.sym_labo2.com.CommunicationEventListener
import ch.heigvd.sym_labo2.com.SymComManager
import ch.heigvd.sym_labo2.model.Author
import ch.heigvd.sym_labo2.model.Book
import com.google.gson.Gson

class GraphqlActivity : AppCompatActivity() {
    private lateinit var authorSpinner: Spinner
    private lateinit var bookListView: ListView
    private val mcm: SymComManager = SymComManager()

    data class Query(val query: String)

    data class AuthorsResponse(val data: Data) {
        data class Data(val authors: ArrayList<Author>)
    }

    data class AuthorResponse(val data: Data) {
        data class Data(val author: Author)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graphql)
        authorSpinner = findViewById(R.id.spinner_author)
        bookListView = findViewById(R.id.list_book)
        val authorAdapter = ArrayAdapter<Author>(
            this@GraphqlActivity, android.R.layout.simple_list_item_1
        )
        authorSpinner.adapter = authorAdapter
        authorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val author = authorSpinner.getItemAtPosition(position) as Author
                if (author.id != null) getAuthorBooks(author.id)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        mcm.setCommunicationListener(object : CommunicationEventListener {
            override fun handleServerResponse(response: ByteArray) {
                val result = Gson().fromJson(response.decodeToString(), AuthorsResponse::class.java)
                authorAdapter.clear()
                authorAdapter.addAll(result.data.authors)

                Toast.makeText(
                    applicationContext,
                    "Found ${result.data.authors.size} authors",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        sendQuery("{authors:findAllAuthors{id, name}}")
    }

    private fun getAuthorBooks(authorId: String) {
        mcm.setCommunicationListener(object : CommunicationEventListener {
            override fun handleServerResponse(response: ByteArray) {
                val result = Gson().fromJson(response.decodeToString(), AuthorResponse::class.java)
                val books = result.data.author.books ?: ArrayList()

                Toast.makeText(
                    applicationContext,
                    "Found ${books.size} books",
                    Toast.LENGTH_SHORT
                ).show()

                val adapter: ArrayAdapter<Book> = ArrayAdapter(
                    this@GraphqlActivity,
                    android.R.layout.simple_list_item_1,
                    books
                )
                bookListView.adapter = adapter
            }
        })

        sendQuery("{author:findAuthorById(id: $authorId){books{title}}}")
    }

    private fun sendQuery(queryString: String) {
        val json = Gson().toJson(Query(queryString))
        mcm.sendRequest(
            SymComManager.URL_GRAPHQL,
            json.toByteArray(),
            SymComManager.CONTENT_TYPE_JSON
        )
    }
}