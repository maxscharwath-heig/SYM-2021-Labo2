package ch.heigvd.sym_labo2

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.net.ConnectivityManager
import android.os.StrictMode
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter


class DifferActivity : AppCompatActivity() {
    private lateinit var sendButton: Button
    private lateinit var requestContentTextView: TextView
    private lateinit var requestResultTextView: TextView

    companion object {
        private const val REQUEST_FILE = "requests.txt"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_differ)

        sendButton = findViewById(R.id.send_button)
        requestContentTextView = findViewById(R.id.request_content_text)
        requestResultTextView = findViewById(R.id.request_result_text)

        sendButton.setOnClickListener {

            val content = requestContentTextView.text.toString()

            if (isNetworkAvailable(this)) {
                // Internet is available, sending request

                val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
                StrictMode.setThreadPolicy(policy)

                val mcm = SymComManager()
                mcm.setCommunicationListener(object : CommunicationEventListener {
                    override fun handleServerResponse(response: String) {
                        requestResultTextView.setText(response)
                    }
                })
                mcm.sendRequest("http://mobile.iict.ch/api/txt", content)
            }
            else {
                writeRequestToFile(this, content)

                Log.d("File content:", retrieveStoredRequests(this))
            }
        }
    }

    // TODO: test this
    private fun isNetworkAvailable (context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities != null
    }

    private fun writeRequestToFile (context: Context, content: String) : File  {
        val path = context.getFilesDir()
        val file = File(path, REQUEST_FILE)

        file.appendText(content + "\n")
        return file
    }

    private fun retrieveStoredRequests (context: Context) : String {
        val path = context.getFilesDir()
        val file = File(path, REQUEST_FILE)

        return FileInputStream(file).bufferedReader().use { it.readText() }
    }
}