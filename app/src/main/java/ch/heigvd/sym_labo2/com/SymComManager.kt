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

/**
 * Class that manages requests sending to server's endpoints
 */
class SymComManager(private var communicationEventListener: CommunicationEventListener? = null) {

    companion object {
        private const val REQ_METHOD = "POST"
        private const val ENDPOINT = "http://mobile.iict.ch/api"

        const val URL_TEXT: String = "${ENDPOINT}/txt"
        const val URL_JSON: String = "${ENDPOINT}/json"
        const val URL_XML: String = "${ENDPOINT}/xml"
        const val URL_PROTOBUF: String = "${ENDPOINT}/protobuf"
        const val URL_GRAPHQL: String = "${ENDPOINT}/graphql"

        const val CONTENT_TYPE_TEXT: String = "text/plain"
        const val CONTENT_TYPE_JSON: String = "application/json"
        const val CONTENT_TYPE_XML: String = "application/xml"
        const val CONTENT_TYPE_PROTOBUF: String = "application/protobuf"
    }

    private val handler = Handler(Looper.getMainLooper())

    /**
     * Set response behaviour
     */
    fun setCommunicationListener(communicationEventListener: CommunicationEventListener) {
        this.communicationEventListener = communicationEventListener
    }

    /**
     * Send request
     *
     * @param url Endpoint to reach
     * @param request Request content
     * @param contentType Type of content
     * @param compress Enable deflate compression
     */
    fun sendRequest(
        url: String,
        request: ByteArray,
        contentType: String,
        compress: Boolean = false
    ) {
        val thread = Thread {

            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = REQ_METHOD
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