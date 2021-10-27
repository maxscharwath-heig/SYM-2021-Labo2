package ch.heigvd.sym_labo2

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.net.ConnectivityManager
import android.os.StrictMode
import android.util.Log
import androidx.work.*


class DifferActivity : AppCompatActivity() {
    private lateinit var sendButton: Button
    private lateinit var requestContentTextView: TextView
    private lateinit var requestResultTextView: TextView
    private lateinit var noNetworkWarningTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_differ)

        sendButton = findViewById(R.id.send_button)
        requestContentTextView = findViewById(R.id.request_content_text)
        requestResultTextView = findViewById(R.id.request_result_text)
        noNetworkWarningTextView = findViewById(R.id.no_network_warning)

        sendButton.setOnClickListener {

            val content = requestContentTextView.text.toString()

            // La condition est inversée juste pour tester le WorkManager
            if (!isNetworkAvailable(applicationContext)) {
                // Internet is available, sending request
                sendRequest(content)

            } else {
                noNetworkWarningTextView.text = getString(R.string.no_network_warn)
                RequestFileHandler.writeRequestToFile(this, content)
                val request: WorkRequest = OneTimeWorkRequestBuilder<DifferRequestWorker>().build()
                WorkManager.getInstance(this).enqueue(request)

                Log.d("state: ",
                    WorkManager.getInstance(this).getWorkInfoById(request.id).get().state.toString()
                )
            }
        }
    }

    private fun sendRequest(content: String) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val mcm = SymComManager()
        mcm.setCommunicationListener(object : CommunicationEventListener {
            override fun handleServerResponse(response: String) {
                requestResultTextView.text = response
            }
        })
        mcm.sendRequest("http://mobile.iict.ch/api/txt", content, "text/plain")
    }

    // TODO: test this
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities != null
    }
}