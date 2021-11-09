package ch.heigvd.sym_labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.google.gson.Gson
import android.widget.ArrayAdapter

class GraphqlActivity : AppCompatActivity() {
    private lateinit var sendBtn : Button
    private lateinit var textResult : TextView

    data class Query(val query: String);

    data class Book(val id:String, val title:String, val isbn13:String, val languageCode:String, val numPages:Int, val publicationDate:String, val publisher:String, val textReviewsCount:Int, val averageRating:Float){
        override fun toString(): String {
            return title
        }
    }

    data class Author(val id:String, val name:String, val books:ArrayList<Book>){
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
        sendBtn = findViewById(R.id.btn_send)
        textResult = findViewById(R.id.textView_result)

        val mcm = SymComManager();
        mcm.setCommunicationListener(object : CommunicationEventListener {
            override fun handleServerResponse(response: String) {
                val result = Gson().fromJson(response, AuthorsResponse::class.java)
                val list = findViewById<ListView>(R.id.list_item)
                val adapter: ArrayAdapter<Author> = ArrayAdapter(this@GraphqlActivity, android.R.layout.simple_list_item_1, result.data.authors)
                list.adapter = adapter;
                list.setOnItemClickListener { parent, view, position, id ->
                    val author = list.getItemAtPosition(position) as Author;
                    textResult.text = author.name;
                    getAuthorBooks(author.id);
                }
            }
        })



        sendBtn.setOnClickListener{
            val json = Gson().toJson(Query("{authors:findAllAuthors{id, name, books{id}}}"))
            textResult.text=json
            mcm.sendRequest("http://mobile.iict.ch/graphql",json, "application/json")
        }
    }

    private fun getAuthorBooks(authorId:String){
        val mcm = SymComManager();
        mcm.setCommunicationListener(object : CommunicationEventListener {
            override fun handleServerResponse(response: String) {
                val result = Gson().fromJson(response, AuthorResponse::class.java)
                val list = findViewById<ListView>(R.id.list_item)
                val adapter: ArrayAdapter<Book> = ArrayAdapter(this@GraphqlActivity, android.R.layout.simple_list_item_1, result.data.author.books)
                list.adapter = adapter;
                list.setOnItemClickListener { parent, view, position, id ->
                    val book = list.getItemAtPosition(position) as Book;
                    textResult.text = book.title;
                }

            }
        })
        val json = Gson().toJson(Query("{author:findAuthorById(id: $authorId){books{id, title}}}"))

        mcm.sendRequest("http://mobile.iict.ch/graphql",json, "application/json")
    }
}