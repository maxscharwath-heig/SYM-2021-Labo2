package ch.heigvd.sym_labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class CompressActivity : AppCompatActivity() {

    private lateinit var sendButton: Button
    private lateinit var requestContentTextView: TextView
    private lateinit var requestResultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compress)

        sendButton = findViewById(R.id.send_button)
        requestContentTextView = findViewById(R.id.request_content_text)
        requestResultTextView = findViewById(R.id.request_result_text)

        val mcm = SymComManager()
        mcm.setCommunicationListener(object : CommunicationEventListener {
            override fun handleServerResponse(response: ByteArray) {
                requestResultTextView.text = response.decodeToString()
            }
        })

        sendButton.setOnClickListener {
            val content = requestContentTextView.text.toString()

            if (content.isBlank()) {
                Toast.makeText(applicationContext, getString(R.string.blank_input), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mcm.sendRequest("http://mobile.iict.ch/api/txt", content.toByteArray(), "text/plain", true)
            return@setOnClickListener
        }
    }
}