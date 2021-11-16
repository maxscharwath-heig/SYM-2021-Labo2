package ch.heigvd.sym_labo2

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class DifferRequestWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    companion object {
        const val KEY_INPUT  = "requests"
        const val KEY_RESULT = "result"
    }

    override fun doWork(): Result {
        val retrievedRequests = inputData.getStringArray(KEY_INPUT)

        if (retrievedRequests != null) {
            for (req in retrievedRequests) {
                try {
                    Log.d("Trying to send", req)
                    val result = sendRequest(req)
                    val output: Data = workDataOf(KEY_RESULT to result)
                    return Result.success(output)

                } catch (e : Exception) {
                    Log.d("failed", e.toString())
                    return Result.retry()
                }
            }
        }
        return Result.success()
    }

    private fun sendRequest(request: String): String {
        val data = request.toByteArray(StandardCharsets.UTF_8)

        val connection = URL("http://mobile.iict.ch/api/txt").openConnection() as HttpURLConnection
        connection.requestMethod = "POST";
        connection.setRequestProperty("Content-Type", "text/plain")
        connection.setRequestProperty("Content-Length", data.size.toString())

        val outputStream = DataOutputStream(connection.outputStream)
        outputStream.write(data)
        outputStream.flush()
        val responseReader = connection.inputStream.bufferedReader()
        return responseReader.readText();
    }
}