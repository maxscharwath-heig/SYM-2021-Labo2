package ch.heigvd.sym_labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Xml
import android.widget.Button
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import kotlinx.serialization.decodeFromString

import ch.heigvd.sym_labo2.model.Phone
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Text
import java.io.InputStream
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilder

import javax.xml.parsers.DocumentBuilderFactory
import android.R.xml
import org.w3c.dom.Node

import java.io.StringReader

import org.xml.sax.InputSource








class SerializeActivity : AppCompatActivity() {


    private lateinit var sendBtn : Button
    private lateinit var dataName : EditText
    private lateinit var dataFirstName : EditText
    private lateinit var dataPhone1 : EditText
    private lateinit var dataPhone2 : EditText
    private lateinit var dataPhone3 : EditText
    private lateinit var result : TextView
    private lateinit var spinner : Spinner

    @Serializable
    data class Data(val name: String, val firstName: String, val phone1: String, val phone2: String, val phone3: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serialize)

        sendBtn = findViewById(R.id.btn_send)
        dataName = findViewById(R.id.input_data_name)
        dataFirstName = findViewById(R.id.input_data_firstName)
        dataPhone1 = findViewById(R.id.input_data_phone1)
        dataPhone2 = findViewById(R.id.input_data_phone2)
        dataPhone3 = findViewById(R.id.input_data_phone3)
        result = findViewById(R.id.result)
        spinner = findViewById(R.id.spinner)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val mcm = SymComManager()
        val stringArray = resources.getStringArray(R.array.data_array)

        sendBtn.setOnClickListener {
            var person = ch.heigvd.sym_labo2.model.Person(dataName.text.toString()
                , dataFirstName.text.toString(),
                mutableListOf(Phone(dataPhone1.text.toString(),
                Phone.Type.home), Phone(dataPhone2.text.toString(),
                Phone.Type.mobile), Phone(dataPhone3.text.toString(),
                Phone.Type.work)
            ))
                when {
                spinner.selectedItem.toString() == stringArray[0] -> {

                    val json = Json.encodeToString(Data(person.name, person.firstName,
                        person.phones[0].number, person.phones[1].number, person.phones[2].number))

                    mcm.setCommunicationListener(object : CommunicationEventListener {
                        override fun handleServerResponse(response: String) {

                            val jsonResult = Json.decodeFromString<Data>(response)

                            var resultPerson = ch.heigvd.sym_labo2.model.Person(jsonResult.name
                                , jsonResult.firstName,
                                mutableListOf(
                                    Phone(
                                        jsonResult.phone1,
                                        Phone.Type.home
                                    ),
                                    Phone(
                                        jsonResult.phone2,
                                        Phone.Type.mobile
                                    ),
                                    Phone(
                                        jsonResult.phone3,
                                        Phone.Type.work,
                                    )
                                )
                            )
                            result.text = resultPerson.toString()
                        }
                    })

                    mcm.sendRequest("http://mobile.iict.ch/api/json", json, "application/json")

                }
                spinner.selectedItem.toString() == stringArray[1] -> {
                    val xmlSerializer = Xml.newSerializer()
                    val writer = StringWriter()
                    xmlSerializer.setOutput(writer)
                    xmlSerializer.startDocument("UTF-8", false)
                    xmlSerializer.docdecl(" directory SYSTEM \"http://mobile.iict.ch/directory.dtd\"")
                    xmlSerializer.startTag("", "directory")
                    person.toXml(xmlSerializer)
                    xmlSerializer.endTag("", "directory")
                    xmlSerializer.endDocument()
                    var toSend = writer.toString()

                    mcm.setCommunicationListener(object : CommunicationEventListener {
                        override fun handleServerResponse(response: String) {
                            val docb = DocumentBuilderFactory.newInstance()
                            val doc = docb.newDocumentBuilder()
                            val input = InputSource()
                            input.setCharacterStream(StringReader(response))
                            val d = doc.parse(input)
                            val nameElements = d.getElementsByTagName("name")
                            val firstNameElements = d.getElementsByTagName("firstname")
                            val phoneElements = d.getElementsByTagName("phone")

                            var resultPerson = ch.heigvd.sym_labo2.model.Person(nameElements.item(0).textContent
                                , firstNameElements.item(0).textContent,
                                mutableListOf(
                                    Phone(
                                        phoneElements.item(0).textContent,
                                        Phone.Type.valueOf(phoneElements.item(0).attributes.item(0).textContent)
                                    ),
                                    Phone(
                                        phoneElements.item(1).textContent,
                                        Phone.Type.valueOf(phoneElements.item(1).attributes.item(0).textContent)
                                    ),
                                    Phone(
                                        phoneElements.item(2).textContent,
                                        Phone.Type.valueOf(phoneElements.item(2).attributes.item(0).textContent)
                                    )
                                )
                            )
                            result.text = resultPerson.toString()
                        }
                    })
                    mcm.sendRequest("http://mobile.iict.ch/api/xml", toSend , "application/xml")
                }
                spinner.selectedItem.toString() == stringArray[2] -> {

                }
            }
        }
    }
}