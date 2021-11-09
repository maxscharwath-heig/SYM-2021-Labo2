package ch.heigvd.sym_labo2.model

class Phone(
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
}
