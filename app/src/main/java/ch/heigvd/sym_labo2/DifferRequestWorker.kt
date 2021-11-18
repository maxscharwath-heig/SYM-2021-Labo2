package ch.heigvd.sym_labo2

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import ch.heigvd.sym_labo2.com.SymComManager

class DifferRequestWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {
    companion object { //TODO C'est sensé être un companion ça ? ça peut pas juste être un const ?
        const val KEY_INPUT = "requests"
        const val KEY_RESULT = "result"
    }

    override fun doWork(): Result {
        val retrievedRequests = inputData.getStringArray(KEY_INPUT)

        if (retrievedRequests != null) {
            for (req in retrievedRequests) {
                return try {
                    val result = sendRequest(req)
                    val output: Data = workDataOf(KEY_RESULT to result)
                    Result.success(output)

                } catch (e: Exception) {
                    Result.retry()
                }
            }
        }
        return Result.success()
    }

    private fun sendRequest(request: String): String {
        val data = request.toByteArray(StandardCharsets.UTF_8)

        val connection = URL(SymComManager.URL_TEXT).openConnection() as HttpURLConnection
        connection.requestMethod = "POST";
        connection.setRequestProperty("Content-Type", SymComManager.CONTENT_TYPE_TEXT)
        connection.setRequestProperty("Content-Length", data.size.toString())

        val outputStream = DataOutputStream(connection.outputStream)
        outputStream.write(data)
        outputStream.flush()

        val responseReader = connection.inputStream.bufferedReader()
        return responseReader.readText();
    }
}