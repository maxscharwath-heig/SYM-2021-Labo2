package ch.heigvd.sym_labo2.model

import ch.heigvd.sym_labo2.protobuf.DirectoryOuterClass
import org.xmlpull.v1.XmlSerializer

/**
 * Class that represents a directory that contains Persons
 * @param persons List of persons in the directory
 *
 * @author Nicolas Crausaz
 * @author Teo Ferrari
 * @author Maxime Scharwath
 */
data class Directory(val persons: MutableList<Person>) {

    /**
     * Serializes the directory to XML using the XmlSerializer
     * @param xmlSerializer Serializer to use
     */
    fun toXml(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "directory")
        for (person in persons) {
            person.toXml(xmlSerializer)
        }
        xmlSerializer.endTag("", "directory")
    }

    /**
     * Serializes the directory to ProtoBuf, returns a ProtoBuf serialized directory
     * @return a ProtoBuf serialized directory
     */
    fun toProtobuf(): DirectoryOuterClass.Directory {
        val protoDirectoryBuilder = DirectoryOuterClass.Directory.newBuilder()

        protoDirectoryBuilder.addResults(persons[0].toProtobuf())

        return protoDirectoryBuilder.build()
    }
}