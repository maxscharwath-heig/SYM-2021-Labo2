package ch.heigvd.sym_labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.TextView

class AsyncActivity : AppCompatActivity() {

    private lateinit var resultTextView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_async)
        resultTextView = findViewById(R.id.async_result)
    }

    override fun onStart() {
        super.onStart()
        val mcm = SymComManager();
        mcm.setCommunicationListener(object : CommunicationEventListener {
            override fun handleServerResponse(response: String) {
                resultTextView.setText(response);
                println(response)
            }
        })
        mcm.sendRequest("http://mobile.iict.ch/api/txt","Yolololol", "text/plain")


    }
}