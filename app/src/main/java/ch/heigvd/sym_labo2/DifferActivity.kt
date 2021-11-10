package ch.heigvd.sym_labo2

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.net.ConnectivityManager
import android.os.StrictMode
import android.widget.Toast
import androidx.work.*
import ch.heigvd.sym_labo2.DifferRequestWorker.Companion.KEY_INPUT
import ch.heigvd.sym_labo2.DifferRequestWorker.Companion.KEY_RESULT


class DifferActivity : AppCompatActivity() {
    private lateinit var sendButton: Button
    private lateinit var requestContentTextView: TextView
    private lateinit var requestResultTextView: TextView

    private var pendingRequests = mutableListOf<String>()

    companion object {
        val CONSTRAINTS = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_differ)

        sendButton = findViewById(R.id.send_button)
        requestContentTextView = findViewById(R.id.request_content_text)
        requestResultTextView = findViewById(R.id.request_result_text)

        val mcm = SymComManager()
        mcm.setCommunicationListener(object : CommunicationEventListener {
            override fun handleServerResponse(response: String) {
                requestResultTextView.text = response
            }
        })

        sendButton.setOnClickListener {
            val content = requestContentTextView.text.toString()

            if (content.isBlank()) {
                Toast.makeText(applicationContext, getString(R.string.blank_input), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isNetworkAvailable(applicationContext)) {
                // Internet is available, sending request directly
                mcm.sendRequest("http://mobile.iict.ch/api/txt", content, "text/plain")

            } else {
                Toast.makeText(applicationContext, getString(R.string.no_network_warn), Toast.LENGTH_SHORT).show()

                pendingRequests.add(content)
                val data = Data.Builder().putStringArray(KEY_INPUT, pendingRequests.toTypedArray())

                val request = OneTimeWorkRequestBuilder<DifferRequestWorker>()
                    .setConstraints(CONSTRAINTS)
                    .setInputData(data.build())
                    .build()

                WorkManager.getInstance(this).enqueue(request)
                WorkManager.getInstance(this).getWorkInfoByIdLiveData(request.id)
                    .observe(this, { workInfo ->
                        if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                            requestResultTextView.text = workInfo.outputData.getString(KEY_RESULT).toString()
                            pendingRequests.clear()
                            Toast.makeText(applicationContext, getString(R.string.cached_req_done), Toast.LENGTH_SHORT).show()
                        }
                    })
            }
            return@setOnClickListener
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities != null
    }
}