package ch.heigvd.sym_labo2

import android.content.Context
import android.os.StrictMode
import androidx.work.Worker
import androidx.work.WorkerParameters

class DifferRequestWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    companion object {
        const val REQ_KEY = "requests"
    }

    override fun doWork(): Result {

        val retrievedRequests = inputData.getStringArray(REQ_KEY)

        if (retrievedRequests != null) {
            for (req in retrievedRequests) {
                sendRequest(req)
            }
        }

        // For now, just doing the first line
        //val res =
        // sendRequest("coucou")


        // TODO: Find a way to detect if there are errors in sending
        // return Result.retry()

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