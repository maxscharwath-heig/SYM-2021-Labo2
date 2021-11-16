package ch.heigvd.sym_labo2

import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.util.Log
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream

class SymComManager(private var communicationEventListener: CommunicationEventListener? = null) {

    companion object {
        val CHARSET = StandardCharsets.UTF_8
    }

    private val handler = Handler(Looper.getMainLooper())

    fun setCommunicationListener(communicationEventListener: CommunicationEventListener) {
        this.communicationEventListener = communicationEventListener
    }

    fun sendRequest(url: String, request: ByteArray, contentType: String, compress: Boolean = false) {
        val thread = Thread {

            // TODO: Gérer les potentielles excption (sur les streams)
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "POST";
            connection.setRequestProperty("Content-Type", contentType)

            val outputStream : OutputStream

            if (compress) {
                connection.setRequestProperty("X-Network", "CSD")
                connection.setRequestProperty("X-Content-Encoding", "deflate")
                outputStream = DeflaterOutputStream(connection.outputStream, Deflater(Deflater.DEFAULT_COMPRESSION, true))
            }
            else {
                outputStream = DataOutputStream(connection.outputStream)
            }

            outputStream.write(request)
            outputStream.flush()
            outputStream.close()

            // Handle response
            val inputStream : InputStream

            if (compress) {
                inputStream = InflaterInputStream(connection.inputStream, Inflater(true))

            } else {
                inputStream = DataInputStream(connection.inputStream)
            }


            val response = inputStream.readBytes()
            inputStream.close()

            //I use handler to edit something in the main thead because you cant edit ui from another thread
            handler.post(Runnable {
                communicationEventListener?.handleServerResponse(response)
            })
        }
        thread.start()
    }
}