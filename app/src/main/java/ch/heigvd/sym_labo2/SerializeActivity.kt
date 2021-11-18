package ch.heigvd.sym_labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Xml
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import ch.heigvd.sym_labo2.com.CommunicationEventListener
import ch.heigvd.sym_labo2.com.SymComManager
import ch.heigvd.sym_labo2.model.Directory
import ch.heigvd.sym_labo2.model.Person
import ch.heigvd.sym_labo2.model.Phone
import java.io.StringWriter
import com.google.gson.Gson

/**
 * Activity demonstrating serialization of a Person with JSON, XML and ProtoBuf
 * @author Nicolas Crausaz
 * @author Teo Ferrari
 * @author Maxime Scharwath
 */
class SerializeActivity : AppCompatActivity() {

    //elements for the view
    private lateinit var sendBtn: Button
    private lateinit var dataName: EditText
    private lateinit var dataFirstName: EditText
    private lateinit var dataPhone1: EditText
    private lateinit var dataPhone2: EditText
    private lateinit var dataPhone3: EditText
    private lateinit var result: TextView
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serialize)

        //link with the view
        sendBtn = findViewById(R.id.btn_send)
        dataName = findViewById(R.id.input_data_name)
        dataFirstName = findViewById(R.id.input_data_firstName)
        dataPhone1 = findViewById(R.id.input_data_phone1)
        dataPhone2 = findViewById(R.id.input_data_phone2)
        dataPhone3 = findViewById(R.id.input_data_phone3)
        result = findViewById(R.id.result)
        spinner = findViewById(R.id.spinner)

        //TODO wtf is this
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val mcm = SymComManager()
        val stringArray = resources.getStringArray(R.array.data_array)

        //when the send button is clicked
        sendBtn.setOnClickListener {

            //new Person from user input ready to be serialized
            val person = Person(
                dataName.text.toString(),
                dataFirstName.text.toString(),
                mutableListOf(
                    Phone(dataPhone1.text.toString(), Phone.Type.home),
                    Phone(dataPhone2.text.toString(), Phone.Type.mobile),
                    Phone(dataPhone3.text.toString(), Phone.Type.work)
                )
            )

            //Directory containing the person for xml and protobuf
            val directory = Directory(mutableListOf(person))

            when {
                //JSON
                spinner.selectedItem.toString() == stringArray[0] -> {

                    //simple json serialization
                    val json = Gson().toJson(person)

                    mcm.setCommunicationListener(object : CommunicationEventListener {
                        override fun handleServerResponse(response: ByteArray) {
                            //simple json parsing
                            val resultPerson =
                                Gson().fromJson(response.decodeToString(), Person::class.java)
                            result.text = resultPerson.toString()
                        }
                    })

                    mcm.sendRequest(
                        SymComManager.URL_JSON,
                        json.toByteArray(), SymComManager.CONTENT_TYPE_JSON
                    )
                }

                //XML
                spinner.selectedItem.toString() == stringArray[1] -> {
                    //Serialization of Xml using classes functions. In this case we create a
                    // xmlSerializer with the basic needed info and then pass it to the classes
                    // that will parse themselves in the xmlSerializer
                    val xmlSerializer = Xml.newSerializer()
                    val writer = StringWriter()
                    xmlSerializer.setOutput(writer)
                    xmlSerializer.startDocument("UTF-8", false) //TODO consts ?
                    xmlSerializer.docdecl(" directory SYSTEM \"http://mobile.iict.ch/directory.dtd\"")
                    directory.toXml(xmlSerializer)
                    xmlSerializer.endDocument()
                    var toSend = writer.toString()

                    mcm.setCommunicationListener(object : CommunicationEventListener {
                        override fun handleServerResponse(response: ByteArray) {
                            //Parsing of Xml using classes function
                            result.text = Person.fromXml(response.decodeToString()).toString()
                        }
                    })
                    mcm.sendRequest(
                        SymComManager.URL_XML,
                        toSend.toByteArray(),
                        SymComManager.CONTENT_TYPE_XML
                    )
                }

                //ProtoBuf
                spinner.selectedItem.toString() == stringArray[2] -> {

                    //Serialization of Xml using classes functions
                    val toSend = directory.toProtobuf().toByteArray()

                    mcm.setCommunicationListener(object : CommunicationEventListener {
                        override fun handleServerResponse(response: ByteArray) {
                            ////Parsing of Xml using classes functions
                            result.text = Person.fromProtobuf(response).toString()
                        }
                    })
                    mcm.sendRequest(
                        SymComManager.URL_PROTOBUF,
                        toSend,
                        SymComManager.CONTENT_TYPE_PROTOBUF
                    )
                }
            }
        }
    }
}