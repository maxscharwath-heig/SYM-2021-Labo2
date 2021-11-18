package ch.heigvd.sym_labo2.model

import ch.heigvd.sym_labo2.protobuf.DirectoryOuterClass
import com.google.gson.annotations.SerializedName
import org.xmlpull.v1.XmlSerializer

/**
 * Class that represents a phone
 * @param number number of the phone
 * @param type type of phone between home, mobile and work
 * @author Nicolas Crausaz
 * @author Teo Ferrari
 * @author Maxime Scharwath
 */
class Phone( //TODO voir pour private
    val number: String,
    val type: Type
) {
    enum class Type {
        //@SerializedName necessary for enum parsing from gson, in this case it sends
        // the value and gets either the value or one of the alternates
        @SerializedName("home", alternate = ["home MODIFIED"])
        home, //TODO majuscule ?

        @SerializedName("mobile", alternate = ["mobile MODIFIED"])
        mobile,

        @SerializedName("work", alternate = ["work MODIFIED"])
        work;
    }


    override fun toString(): String {
        return type.name + " : " + number
    }

    /**
     * Serializes the phone to XML using the XmlSerializer
     * @param xmlSerializer Serializer to use
     */
    fun toXml(xmlSerializer: XmlSerializer) {
        xmlSerializer.startTag("", "phone")
        xmlSerializer.attribute("", "type", type.name)
        xmlSerializer.text(number)
        xmlSerializer.endTag("", "phone")
    }

    /**
     * Serializes the phone to ProtoBuf, returns a ProtoBuf serialized phone
     * @return a ProtoBuf serialized phone
     */
    fun toProtobuf(): DirectoryOuterClass.Phone? {
        return DirectoryOuterClass.Phone.newBuilder()
            .setType(DirectoryOuterClass.Phone.Type.valueOf(type.name.uppercase()))
            .setNumber(number).build()
    }
}
