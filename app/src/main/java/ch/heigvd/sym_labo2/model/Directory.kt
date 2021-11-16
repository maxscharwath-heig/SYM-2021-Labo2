package ch.heigvd.sym_labo2.model

import org.xmlpull.v1.XmlSerializer

class Directory (private val persons : MutableList<Person>) {

    fun addPerson(person : Person){
        persons.add(person)
    }

    fun toXml(xmlSerializer: XmlSerializer){
        xmlSerializer.startTag("", "directory")
        for (person in persons){
            person.toXml(xmlSerializer)
        }
        xmlSerializer.endTag("", "directory")
    }
}