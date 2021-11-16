package ch.heigvd.sym_labo2.model

import kotlinx.serialization.Serializable
import org.xmlpull.v1.XmlSerializer


data class Phone(
    val number: String,
    val type: Type) {
    enum class Type {
        home,
        mobile,
        work
    }

    override fun toString(): String {
        return type.name + " : " + number
    }

    fun toXml(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "phone")
        xmlSerializer.attribute("", "type", type.name)
        xmlSerializer.text(number)
        xmlSerializer.endTag("", "phone")
    }
}
