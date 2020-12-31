package BibleImporter

data class BibleTranslation(
    val abbreviation: String,
    val description: String,
    val about: String,
    val name: String,
    val language: String,
    val books: MutableList<Book>
)

data class Book(
    val abbreviation: String,
    val name: String,
    val order: Number,
    val chapters: MutableList<Chapter>
)

data class Chapter(
    val order: Number,
    val paragraphs: MutableList<Paragraph>
)

data class Paragraph(
    val order: Number,
    val verses: MutableList<Verse>
)

data class Verse(
    val order: Number,
    val content: String
)