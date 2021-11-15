package ch.heigvd.sym_labo2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Button
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import android.widget.EditText
import android.widget.TextView
import kotlinx.serialization.decodeFromString

import ch.heigvd.sym_labo2.model.Phone
import java.io.StringWriter

import javax.xml.parsers.DocumentBuilderFactory
import ch.heigvd.sym_labo2.protobuf.DirectoryOuterClass

import java.io.StringReader

import org.xml.sax.InputSource








class SerializeActivity : AppCompatActivity() {


    private lateinit var sendBtn : Button
    private lateinit var editData1 : EditText
    private lateinit var editData2 : EditText
    private lateinit var textResult1 : TextView
    private lateinit var textResult2 : TextView

    @Serializable
    data class Data(val name: String, val surname: String)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_serialize)

        sendBtn = findViewById(R.id.btn_send)
        editData1 = findViewById(R.id.input_data1)
        editData2 = findViewById(R.id.input_data2)
        textResult1 = findViewById(R.id.textView_result)
        textResult2 = findViewById(R.id.textView_result2)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val mcm = SymComManager();



        sendBtn.setOnClickListener {
            var name = editData1.text.toString()
            var surname = editData2.text.toString()
            Log.d("test",name)
            Log.d("test",surname)
            val json = Json.encodeToString(Data(name, surname))

            mcm.setCommunicationListener(object : CommunicationEventListener {
                override fun handleServerResponse(response: String) {
                    val result = Json.decodeFromString<Data>(response)
                    textResult1.text = result.name
                    textResult2.text = result.surname
                }
            })
            mcm.sendRequest("http://mobile.iict.ch/api/json", json, "application/json")



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
                        override fun handleServerResponse(response: String) {
                            val resultProtoDirectory = DirectoryOuterClass.Directory.parseFrom(response.encodeToByteArray())

                            val resultProtoPerson = resultProtoDirectory.resultsList[0]

                            var resultPerson = ch.heigvd.sym_labo2.model.Person(resultProtoPerson.name, resultProtoPerson.firstname,
                                mutableListOf(
                                    Phone(
                                        resultProtoPerson.phoneList[0].number,
                                        Phone.Type.valueOf(resultProtoPerson.phoneList[0].type.name)
                                    ),
                                    Phone(
                                        resultProtoPerson.phoneList[1].number,
                                        Phone.Type.valueOf(resultProtoPerson.phoneList[1].type.name)
                                    ),
                                    Phone(
                                        resultProtoPerson.phoneList[2].number,
                                        Phone.Type.valueOf(resultProtoPerson.phoneList[2].type.name)
                                    ),
                                )
                            )
                            result.text = resultPerson.toString()
                        }
                    })
                    mcm.sendRequest("http://mobile.iict.ch/api/protobuf", toSend.toString() , "application/protobuf")

                }
            }
        }
    }
}