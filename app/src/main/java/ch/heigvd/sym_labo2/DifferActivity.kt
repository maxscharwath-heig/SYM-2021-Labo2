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
import ch.heigvd.sym_labo2.DifferRequestWorker.Companion.REQ_KEY


class DifferActivity : AppCompatActivity() {
    private lateinit var sendButton: Button
    private lateinit var requestContentTextView: TextView
    private lateinit var requestResultTextView: TextView
    private lateinit var noNetworkWarningTextView: TextView

    private var pendingRequests = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_differ)

        sendButton = findViewById(R.id.send_button)
        requestContentTextView = findViewById(R.id.request_content_text)
        requestResultTextView = findViewById(R.id.request_result_text)
        noNetworkWarningTextView = findViewById(R.id.no_network_warning)

        sendButton.setOnClickListener {
            // TODO: Try to send, if error, use workmanager

            val content = requestContentTextView.text.toString()

            if (isNetworkAvailable(applicationContext)) {
                // Internet is available, sending request
                noNetworkWarningTextView.text = ""
                sendRequest(content)

            } else {
                noNetworkWarningTextView.text = getString(R.string.no_network_warn)
                pendingRequests.add(content)

                val constraints =
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                val data = Data.Builder().putStringArray(REQ_KEY, pendingRequests.toTypedArray())

                val request = OneTimeWorkRequestBuilder<DifferRequestWorker>()
                    .setConstraints(constraints)
                    .setInputData(data.build())
                    .build()

                WorkManager.getInstance(this).enqueue(request)
                WorkManager.getInstance(this).getWorkInfoByIdLiveData(request.id)
                    .observe(this, { workInfo ->
                        if (workInfo != null && workInfo.state.isFinished) {
                            Log.d("Result", workInfo.outputData.getString(REQ_KEY).toString())
                            requestResultTextView.text = workInfo.outputData.getString(REQ_KEY).toString()
                            pendingRequests.clear()
                        }
                    })
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