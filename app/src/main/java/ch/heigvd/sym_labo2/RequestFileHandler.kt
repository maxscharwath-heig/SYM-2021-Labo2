package ch.heigvd.sym_labo2

import android.content.Context
import java.io.File
import java.io.FileInputStream

class RequestFileHandler {
    companion object {
        const val REQUEST_FILE = "requests.txt"

        fun retrieveStoredRequests(context: Context): List<String> {
            // TODO: To array
            val path = context.filesDir
            val file = File(path, REQUEST_FILE)

            return FileInputStream(file).bufferedReader().use { it.readText() }.split('\n')
        }

        fun writeRequestToFile(context: Context, content: String): File {
            val path = context.filesDir
            val file = File(path, REQUEST_FILE)

            file.appendText(content + "\n")
            return file
        }
    }


}