package ch.heigvd.sym_labo2.model

import kotlinx.serialization.Serializable
import org.xmlpull.v1.XmlSerializer

data class Person(val name:String, val firstName: String, val phones: MutableList<Phone>) {

    override fun toString(): String {

        var s: String = "Name: " + name +
                "\nFirstname: " + firstName +
                "\nPhones : \n"

        for (phone in phones) {
            s += phone
            s += "\n"
        }
        return s
    }

    fun toXml(xmlSerializer: XmlSerializer){
        xmlSerializer.startTag("","person")
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



}