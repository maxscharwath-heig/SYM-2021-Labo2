package ch.heigvd.sym_labo2

import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class SymComManager(private var communicationEventListener: CommunicationEventListener? = null) {

    fun setCommunicationListener(communicationEventListener: CommunicationEventListener){
        this.communicationEventListener = communicationEventListener
    }

    fun sendRequest(url: String, request: String) {
        val data = request.toByteArray(StandardCharsets.UTF_8)

        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "POST";
        connection.setRequestProperty("Content-Type","text/plain")
        connection.setRequestProperty("Content-Length",data.size.toString())

        val outputStream = DataOutputStream(connection.outputStream)
        outputStream.write(data)
        outputStream.flush()
        val responseReader = connection.inputStream.bufferedReader()
        val response = responseReader.readText();
        communicationEventListener?.handleServerResponse(response)
    }

}