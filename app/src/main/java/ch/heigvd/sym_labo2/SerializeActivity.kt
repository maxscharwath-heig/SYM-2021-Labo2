package ch.heigvd.sym_labo2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.util.Xml
import android.widget.Button
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import kotlinx.serialization.decodeFromString
import android.widget.AdapterView

import android.view.View
import android.widget.AdapterView.OnItemSelectedListener
import org.xmlpull.v1.XmlPullParser
import java.io.StringWriter

import org.xmlpull.v1.XmlSerializer





class SerializeActivity : AppCompatActivity() {


    private lateinit var sendBtn : Button
    private lateinit var editData1 : EditText
    private lateinit var editData2 : EditText
    private lateinit var textResult1 : TextView
    private lateinit var textResult2 : TextView
    private lateinit var spinner : Spinner

    @Serializable
    data class Data(val name: String, val surname: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serialize)

        sendBtn = findViewById(R.id.btn_send)
        editData1 = findViewById(R.id.input_data1)
        editData2 = findViewById(R.id.input_data2)
        textResult1 = findViewById(R.id.textView_result1)
        textResult2 = findViewById(R.id.textView_result2)
        spinner = findViewById(R.id.spinner)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val mcm = SymComManager()
        val stringArray = resources.getStringArray(R.array.data_array)

        sendBtn.setOnClickListener {
            var name = editData1.text.toString()
            var surname = editData2.text.toString()
            Log.d("test", name)
            Log.d("test", surname)
            if (spinner.selectedItem.toString() == stringArray[0]){

                val json = Json.encodeToString(Data(name, surname))

                mcm.setCommunicationListener(object : CommunicationEventListener {
                    override fun handleServerResponse(response: String) {
                        val result = Json.decodeFromString<Data>(response)
                        textResult1.text = result.name
                        textResult2.text = result.surname
                    }
                })

                mcm.sendRequest("http://mobile.iict.ch/api/json", json, "application/json")

            } else if(spinner.selectedItem.toString() == stringArray[1]) {

                val xmlSerializer = Xml.newSerializer()
                val xmlParser = Xml.newPullParser()
                val writer = StringWriter()
                xmlSerializer.setOutput(writer)
                xmlSerializer.startDocument("UTF-8", true)
                xmlSerializer.setFeature(
                    "http://xmlpull.org/v1/doc/features.html#indent-output",
                    true
                )
                xmlSerializer.docdecl("http://mobile.iict.ch/directory.dtd")
                xmlSerializer.startTag("", "directory")
                xmlSerializer.startTag("","person")
                xmlSerializer.startTag("", "name")
                xmlSerializer.text(name)
                xmlSerializer.endTag("", "name")
                xmlSerializer.startTag("", "firstname")
                xmlSerializer.text(surname)
                xmlSerializer.endTag("", "firstname")
                xmlSerializer.endTag("", "person")
                xmlSerializer.endTag("", "directory")
                xmlSerializer.endDocument()

                val toSend = writer.

                mcm.setCommunicationListener(object : CommunicationEventListener {
                    override fun handleServerResponse(response: String) {
                       // textResult1.text = xmlParser.getAttributeValue(null, "name")
                        // textResult2.text = xmlParser.getAttributeValue(null, "surname")
                        textResult2.text = response
                    }
                })

                mcm.sendRequest("http://mobile.iict.ch/api/xml", toSend , "application/xml")

            } else if (spinner.selectedItem.toString() == stringArray[2]){

            }
        }
    }
}