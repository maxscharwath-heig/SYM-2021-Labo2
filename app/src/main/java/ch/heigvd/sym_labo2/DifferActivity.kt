package ch.heigvd.sym_labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.work.*
import ch.heigvd.sym_labo2.DifferRequestWorker.Companion.KEY_INPUT
import ch.heigvd.sym_labo2.DifferRequestWorker.Companion.KEY_RESULT
import ch.heigvd.sym_labo2.com.CommunicationEventListener
import ch.heigvd.sym_labo2.com.SymComManager

/**
 * Activity demonstrating a differed request using WorkManager
 * @author Nicolas Crausaz
 * @author Teo Ferrari
 * @author Maxime Scharwath
 */
class DifferActivity : AppCompatActivity() {
    private lateinit var sendButton: Button
    private lateinit var requestContentTextView: TextView
    private lateinit var requestResultTextView: TextView

    private var pendingRequests = mutableListOf<String>()

    companion object {
        private val CONSTRAINTS =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_differ)

        sendButton = findViewById(R.id.send_button)
        requestContentTextView = findViewById(R.id.request_content_text)
        requestResultTextView = findViewById(R.id.request_result_text)

        // Defining request's return behaviour
        val mcm = SymComManager()
        mcm.setCommunicationListener(object : CommunicationEventListener {
            override fun handleServerResponse(response: ByteArray) {
                requestResultTextView.text = response.decodeToString()
            }
        })

        sendButton.setOnClickListener {
            val content = requestContentTextView.text.toString()

            // Content validation
            if (content.isBlank()) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.blank_input),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

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
                        requestResultTextView.text =
                            workInfo.outputData.getString(KEY_RESULT).toString()
                        pendingRequests.clear()
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.cached_req_done),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

            return@setOnClickListener
        }
    }
}