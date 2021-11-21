package ch.heigvd.sym_labo2.model

/**
 * Class that represents an Book
 * @remarks All fields can be null because this class is using by GraphQL
 * @author Nicolas Crausaz
 * @author Teo Ferrari
 * @author Maxime Scharwath
 */
data class Book(
    val id: String?,
    val title: String?,
    val isbn13: String?,
    val languageCode: String?,
    val numPages: Int?,
    val publicationDate: String?,
    val publisher: String?,
    val textReviewsCount: Int?,
    val averageRating: Float?
) {
    override fun toString(): String {
        return title ?: "null"
    }
}