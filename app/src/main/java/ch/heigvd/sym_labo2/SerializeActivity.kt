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
import ch.heigvd.sym_labo2.model.Directory
import ch.heigvd.sym_labo2.model.Person
import ch.heigvd.sym_labo2.model.Phone
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import ch.heigvd.sym_labo2.protobuf.DirectoryOuterClass
import com.google.gson.Gson
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
            val person = Person(
                dataName.text.toString(),
                dataFirstName.text.toString(),
                mutableListOf(
                    Phone(dataPhone1.text.toString(), Phone.Type.home),
                    Phone(dataPhone2.text.toString(),Phone.Type.mobile),
                    Phone(dataPhone3.text.toString(),Phone.Type.work)))

            val directory = Directory(mutableListOf(person))

            when {
                //Json
                spinner.selectedItem.toString() == stringArray[0] -> {

                    val json = Gson().toJson(person)

                    mcm.setCommunicationListener(object : CommunicationEventListener {
                        override fun handleServerResponse(response: ByteArray) {

                            val jsonResult = Gson().fromJson(response.decodeToString(), Person::class.java)

                            var resultPerson = Person(
                                jsonResult.name,
                                jsonResult.firstName,
                                mutableListOf(
                                    Phone(
                                        jsonResult.phones[0].number,
                                        jsonResult.phones[0].type
                                    ),
                                    Phone(
                                        jsonResult.phones[1].number,
                                        jsonResult.phones[1].type
                                    ),
                                    Phone(
                                        jsonResult.phones[2].number,
                                        jsonResult.phones[2].type
                                    )
                                )
                            )
                            result.text = resultPerson.toString()
                        }
                    })

                    mcm.sendRequest("http://mobile.iict.ch/api/json", json.toByteArray(), "application/json")

                }
                //XML
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
                        override fun handleServerResponse(response: ByteArray) {
                            val docb = DocumentBuilderFactory.newInstance()
                            val doc = docb.newDocumentBuilder()
                            val input = InputSource()
                            input.setCharacterStream(StringReader(response.decodeToString()))
                            val d = doc.parse(input)
                            val nameElements = d.getElementsByTagName("name")
                            val firstNameElements = d.getElementsByTagName("firstname")
                            val phoneElements = d.getElementsByTagName("phone")

                            var resultPerson = Person(nameElements.item(0).textContent
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
                    mcm.sendRequest("http://mobile.iict.ch/api/xml", toSend.toByteArray() , "application/xml")
                }
                //ProtoBuf
                spinner.selectedItem.toString() == stringArray[2] -> {

                    val protoDirectoryBuilder = DirectoryOuterClass.Directory.newBuilder()

                    var protoPersonBuilder = DirectoryOuterClass.Person.newBuilder()
                        .setName(person.name)
                        .setFirstname(person.firstName)

                    val protoPhone1 = DirectoryOuterClass.Phone.newBuilder()
                        .setType(DirectoryOuterClass.Phone.Type.HOME)
                        .setNumber(person.phones[0].number).build()

                    val protoPhone2 = DirectoryOuterClass.Phone.newBuilder()
                        .setType(DirectoryOuterClass.Phone.Type.MOBILE)
                        .setNumber(person.phones[1].number).build()

                    val protoPhone3 = DirectoryOuterClass.Phone.newBuilder()
                        .setType(DirectoryOuterClass.Phone.Type.WORK)
                        .setNumber(person.phones[2].number).build()


                    protoPersonBuilder.addPhone(protoPhone1)
                    protoPersonBuilder.addPhone(protoPhone2)
                    protoPersonBuilder.addPhone(protoPhone3)

                    protoDirectoryBuilder.addResults(protoPersonBuilder.build())

                    val toSend = protoDirectoryBuilder.build().toByteArray()



                    mcm.setCommunicationListener(object : CommunicationEventListener {
                        override fun handleServerResponse(response: ByteArray) {
                            val resultProtoDirectory = DirectoryOuterClass.Directory.parseFrom(response)
                            val resultProtoPerson = resultProtoDirectory.resultsList[0]
                            var resultPerson = ch.heigvd.sym_labo2.model.Person(resultProtoPerson.name, resultProtoPerson.firstname,
                                mutableListOf(
                                    Phone(
                                        resultProtoPerson.phoneList[0].number,
                                        Phone.Type.valueOf(resultProtoPerson.phoneList[0].type.name.lowercase())
                                    ),
                                    Phone(
                                        resultProtoPerson.phoneList[1].number,
                                        Phone.Type.valueOf(resultProtoPerson.phoneList[1].type.name.lowercase())
                                    ),
                                    Phone(
                                        resultProtoPerson.phoneList[2].number,
                                        Phone.Type.valueOf(resultProtoPerson.phoneList[2].type.name.lowercase())
                                    ),
                                )
                            )
                            result.text = resultPerson.toString()
                        }
                    })
                    mcm.sendRequest("http://mobile.iict.ch/api/protobuf", toSend , "application/protobuf")

                }
            }
        }
    }
}