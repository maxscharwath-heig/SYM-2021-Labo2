package ch.heigvd.sym_labo2.model

import ch.heigvd.sym_labo2.protobuf.DirectoryOuterClass
import org.xml.sax.InputSource
import org.xmlpull.v1.XmlSerializer
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Class that represents a person
 * @param name Name of the person
 * @param firstName First name of the person
 * @param phones list of phones that the Person has
 *
 * @author Nicolas Crausaz
 * @author Teo Ferrari
 * @author Maxime Scharwath
 */
data class Person(val name: String, val firstName: String, val phones: List<Phone>) {

    override fun toString(): String {
        var s: String = "Name: " + name +
                "\nFirstname: " + firstName +
                "\nPhones : \n"

        for (phone in phones) {
            s += phone.toString()
            s += "\n"
        }
        return s
    }

    /**
     * Serializes the person to XML using the XmlSerializer
     * @param xmlSerializer Serializer to use
     */
    fun toXml(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "person")
        xmlSerializer.startTag("", "name")
        xmlSerializer.text(name)
        xmlSerializer.endTag("", "name")
        xmlSerializer.startTag("", "firstname")
        xmlSerializer.text(firstName)
        xmlSerializer.endTag("", "firstname")
        for (phone in phones) {
            phone.toXml(xmlSerializer)
        }
        xmlSerializer.endTag("", "person")
    }

    /**
     * Serializes the phone to ProtoBuf, returns a ProtoBuf serialized phone
     * @return a ProtoBuf serialized phone
     */
    fun toProtobuf(): DirectoryOuterClass.Person? {

        val protoPersonBuilder = DirectoryOuterClass.Person.newBuilder()
            .setName(name)
            .setFirstname(firstName)

        val protoPhone1 = phones[0].toProtobuf()

        val protoPhone2 = phones[1].toProtobuf()

        val protoPhone3 = phones[2].toProtobuf()

        protoPersonBuilder.addPhone(protoPhone1)
        protoPersonBuilder.addPhone(protoPhone2)
        protoPersonBuilder.addPhone(protoPhone3)
        return protoPersonBuilder.build()
    }

    companion object {

        /**
         * Returns a Person by parsing a xml string
         * @param xml A serialized Person as an XML string
         * @return Parsed Person
         */
        fun fromXml(xml: String): Person {
            val docBuilder = DocumentBuilderFactory.newInstance()
            val doc = docBuilder.newDocumentBuilder()
            val input = InputSource()
            input.characterStream = StringReader(xml)
            val d = doc.parse(input)
            val nameElements = d.getElementsByTagName("name")
            val firstNameElements = d.getElementsByTagName("firstname")
            val phoneElements = d.getElementsByTagName("phone")

            return Person(
                nameElements.item(0).textContent, firstNameElements.item(0).textContent,
                mutableListOf(
                    Phone(
                        phoneElements.item(0).textContent,
                        Phone.Type.valueOf(phoneElements.item(0).
                        attributes.item(0).textContent.uppercase())
                    ),
                    Phone(
                        phoneElements.item(1).textContent,
                        Phone.Type.valueOf(phoneElements.item(1).
                        attributes.item(0).textContent.uppercase())
                    ),
                    Phone(
                        phoneElements.item(2).textContent,
                        Phone.Type.valueOf(phoneElements.item(2).
                        attributes.item(0).textContent.uppercase())
                    )
                )
            )
        }

        /**
         * Returns a Person by parsing a ProtoBuf ByteArray
         * @param protoBuf A serialized Person as an ProtoBuf ByteArray
         * @return Parsed Person
         */
        fun fromProtobuf(protoBuf: ByteArray): Person {
            val resultProtoDirectory = DirectoryOuterClass.Directory.parseFrom(protoBuf)
            val resultProtoPerson = resultProtoDirectory.resultsList[0]
            return Person(
                resultProtoPerson.name, resultProtoPerson.firstname,
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
                    )
                )
            )
        }
    }
}