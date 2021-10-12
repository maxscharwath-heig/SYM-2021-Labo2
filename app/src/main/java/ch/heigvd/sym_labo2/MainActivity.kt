package ch.heigvd.sym_labo2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    private lateinit var asyncBtn : Button
    private lateinit var differBtn : Button
    private lateinit var serializeBtn : Button
    private lateinit var compressBtn : Button
    private lateinit var graphqlBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        asyncBtn = findViewById(R.id.async_btn)
        differBtn = findViewById(R.id.differ_btn)
        serializeBtn = findViewById(R.id.serialize_btn)
        compressBtn = findViewById(R.id.compress_btn)
        graphqlBtn = findViewById(R.id.graphql_btn)


        asyncBtn.setOnClickListener {
            val intent = Intent(this, AsyncActivity::class.java)
            startActivity(intent)
        }

        differBtn.setOnClickListener {
            val intent = Intent(this, DifferActivity::class.java)
            startActivity(intent)
        }

        serializeBtn.setOnClickListener {
            val intent = Intent(this, SerializeActivity::class.java)
            startActivity(intent)
        }

        compressBtn.setOnClickListener {
            val intent = Intent(this, CompressActivity::class.java)
            startActivity(intent)
        }

        graphqlBtn.setOnClickListener {
            val intent = Intent(this, GraphqlActivity::class.java)
            startActivity(intent)
        }
    }
}