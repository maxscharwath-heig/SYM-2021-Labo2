package ch.heigvd.sym_labo2.model

import android.util.Xml
import org.xmlpull.v1.XmlSerializer
import java.io.StringWriter

class Person(val name:String, val firstName: String, val phones: List<Phone>) {

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
            xmlSerializer.startTag("","phone")
            xmlSerializer.attribute("", "type", phone.type.name)
            xmlSerializer.text(phone.number)
            xmlSerializer.endTag("","phone")
        }
        xmlSerializer.endTag("", "person")
    }

}