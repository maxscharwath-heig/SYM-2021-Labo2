package ch.heigvd.sym_labo2

import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class SymComManager(private var communicationEventListener: CommunicationEventListener? = null) {
    private val handler = Handler(Looper.getMainLooper())

    fun setCommunicationListener(communicationEventListener: CommunicationEventListener) {
        this.communicationEventListener = communicationEventListener
    }

    fun sendRequest(url: String, request: String, contentType: String) {
        val thread = Thread {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            val data = request.toByteArray(StandardCharsets.UTF_8)

            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "POST";
            connection.setRequestProperty("Content-Type", contentType)
            connection.setRequestProperty("Content-Length", data.size.toString())

            val outputStream = DataOutputStream(connection.outputStream)
            outputStream.write(data)
            outputStream.flush()
            val responseReader = connection.inputStream.bufferedReader()
            val response = responseReader.readText()
            //I use handler to edit something in the main thead because you cant edit ui from another thread
            handler.post(Runnable {
                communicationEventListener?.handleServerResponse(response)
            })
        }
        thread.start()
    }
}