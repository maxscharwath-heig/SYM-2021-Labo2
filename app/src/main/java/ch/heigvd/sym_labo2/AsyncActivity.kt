package ch.heigvd.sym_labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode

class AsyncActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_async)
    }

    override fun onStart() {
        super.onStart()
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val comManager = SymComManager();
        comManager.sendRequest("http://mobile.iict.ch/api/txt","Yolololol")
    }
}