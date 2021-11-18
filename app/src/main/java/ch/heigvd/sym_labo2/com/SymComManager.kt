package ch.heigvd.sym_labo2.com

import android.os.Handler
import android.os.Looper
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream

class SymComManager(private var communicationEventListener: CommunicationEventListener? = null) {

    companion object {
        const val REQ_METHOD = "POST"

        const val URL_TEXT: String = "http://mobile.iict.ch/api/txt"
        const val URL_JSON: String = "http://mobile.iict.ch/api/json"
        const val URL_XML: String = "http://mobile.iict.ch/api/xml"
        const val URL_PROTOBUF: String = "http://mobile.iict.ch/api/protobuf"
        const val URL_GRAPHQL: String = "http://mobile.iict.ch/graphql"

        const val CONTENT_TYPE_TEXT: String = "text/plain"
        const val CONTENT_TYPE_JSON: String = "application/json"
        const val CONTENT_TYPE_XML: String = "application/xml"
        const val CONTENT_TYPE_PROTOBUF: String = "application/protobuf"
    }

    private val handler = Handler(Looper.getMainLooper())

    fun setCommunicationListener(communicationEventListener: CommunicationEventListener) {
        this.communicationEventListener = communicationEventListener
    }

    fun sendRequest(
        url: String,
        request: ByteArray,
        contentType: String,
        compress: Boolean = false
    ) {
        val thread = Thread {

            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = REQ_METHOD;
            connection.setRequestProperty("Content-Type", contentType)

            val outputStream: OutputStream = if (compress) {
                connection.setRequestProperty("X-Network", "CSD")
                connection.setRequestProperty("X-Content-Encoding", "deflate")
                DeflaterOutputStream(
                    connection.outputStream,
                    Deflater(Deflater.DEFAULT_COMPRESSION, true)
                )
            } else {
                DataOutputStream(connection.outputStream)
            }

            outputStream.write(request)
            outputStream.flush()
            outputStream.close()

            // Handle response
            val inputStream: InputStream = if (compress) {
                InflaterInputStream(connection.inputStream, Inflater(true))
            } else {
                DataInputStream(connection.inputStream)
            }

            val response = inputStream.readBytes()
            inputStream.close()

            handler.post {
                communicationEventListener?.handleServerResponse(response)
            }
        }
        thread.start()
    }
}