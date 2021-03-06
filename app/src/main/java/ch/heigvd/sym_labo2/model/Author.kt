package ch.heigvd.sym_labo2.model

/**
 * Class that represents an Author
 *
 * @remarks All fields can be null because this class is using by GraphQL
 * @author Nicolas Crausaz
 * @author Teo Ferrari
 * @author Maxime Scharwath
 */
data class Author(val id: String?, val name: String?, val books: ArrayList<Book>?) {
    override fun toString(): String {
        return name ?: "null"
    }
}