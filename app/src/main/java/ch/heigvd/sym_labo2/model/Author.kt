package ch.heigvd.sym_labo2.model

data class Author(val id: String?, val name: String?, val books: ArrayList<Book>?) {
    override fun toString(): String {
        return name ?: "null"
    }
}