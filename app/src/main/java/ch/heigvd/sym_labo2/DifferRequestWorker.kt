package ch.heigvd.sym_labo2

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.StrictMode
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.lang.Compiler.command




class DifferRequestWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    companion object {
        const val REQ_KEY = "requests"
    }

    override fun doWork(): Result {

        val retrievedRequests = inputData.getStringArray(REQ_KEY)

        if (retrievedRequests != null) {
            for (req in retrievedRequests) {
                try {
                    Log.d("Trying to send", req)

                    var result = ""
                    val mcm = SymComManager()
                    mcm.setCommunicationListener(object : CommunicationEventListener {
                        override fun handleServerResponse(response: String) {
                            Log.d("Reponse id ", response)

                            result = response
                        }
                    })



                    mcm.sendRequest("http://mobile.iict.ch/api/txt", req, "text/plain")

                    Log.d("HEEEEEEEEEEEEEEEEE", "EEEEEEEEEE")
                    Log.d("sent", result)

                    return Result.success(workDataOf("result" to result))

                } catch (e : Exception) {
                    Log.d("failed", e.toString())
                    return Result.retry()
                }
            }
        }
        return Result.success()
    }

    private fun sendRequest(content: String): String {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        var result = ""

        val mcm = SymComManager()
        mcm.setCommunicationListener(object : CommunicationEventListener {
            override fun handleServerResponse(response: String) {
                result = response
            }
        })

        mcm.sendRequest("http://mobile.iict.ch/api/txt", content, "text/plain")
        return result
    }
}