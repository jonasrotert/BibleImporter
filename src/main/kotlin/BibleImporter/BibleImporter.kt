package BibleImporter

import com.google.gson.GsonBuilder
import java.io.FileInputStream
import java.io.FileOutputStream

val bibleTranslation: BibleTranslation =
        BibleTranslation("ELB1871", "ELB1871", "ELB1871", "ELB1871", "DE", mutableListOf<Book>())

fun getBookFromIndex(index: String): Number {
    val regex = Regex("(\\d+)|(\\d+)|(\\d+)")
    return regex.findAll(index).toList().get(0).value.toInt()
}

fun getChapterFromIndex(index: String): Number {
    val regex = Regex("(\\d+)|(\\d+)|(\\d+)")
    return regex.findAll(index).toList().get(1).value.toInt()
}

fun getVerseFromIndex(index: String): Number {
    val regex = Regex("(\\d+)|(\\d+)|(\\d+)")
    return regex.findAll(index).toList().get(2).value.toInt()
}

fun import(indexfilename: String, bibletextfilename: String) {
    val indexList: MutableList<String> = mutableListOf<String>()
    val bibletextList: MutableList<String> = mutableListOf<String>()

    FileInputStream(bibletextfilename).bufferedReader(charset("windows-1252")).forEachLine { bibletextList.add(it) }
    FileInputStream(indexfilename).bufferedReader(charset("windows-1252")).forEachLine { indexList.add(it) }

    var index = 0

    indexList.forEach {
        val book: Number = getBookFromIndex(it)
        val chapter: Number = getChapterFromIndex(it)
        val verse: Number = getVerseFromIndex(it)
        val verseText: String = bibletextList[index]

        insertVerseIfNotExists(book, chapter, verse, verseText)

        index++
    }
    val gson = GsonBuilder().setPrettyPrinting().create()

    FileOutputStream("output.json").bufferedWriter(charset(charsetName = "utf-8")).write(gson.toJson(bibleTranslation))
    println("Stop")
}

fun insertVerseIfNotExists(bookOrder: Number, chapterOrder: Number, verseOrder: Number, verseText: String) {
    bibleTranslation.books.filter { it.order == bookOrder }.apply {
        val book =
                if (this.isEmpty()) {
                    val temp_book = Book("abbreviation", "name", bookOrder, mutableListOf<Chapter>())
                    bibleTranslation.books.add(temp_book)
                    temp_book
                } else this.first()

        book.chapters.filter { it.order == chapterOrder }.apply {
            val chapter = if (this.isEmpty()) {
                val temp_chapter = Chapter(chapterOrder, mutableListOf<Paragraph>())
                book.chapters.add(temp_chapter)
                temp_chapter
            } else this.first()

            val paragraph =
                    if (chapter.paragraphs.isEmpty()) {
                        val temp_par = Paragraph(0, mutableListOf<Verse>())
                        chapter.paragraphs.add(temp_par)
                        temp_par
                    } else chapter.paragraphs.last()

            val regex = Regex("(\\s?<CM>)")

            val editedVerseText = if (regex.containsMatchIn(verseText)) {
                chapter.paragraphs.add(Paragraph(paragraph.order.toInt().inc(), mutableListOf<Verse>()))
                regex.replace(input = verseText, replacement = "")
            } else verseText

            paragraph.verses.add(Verse(verseOrder, editedVerseText))
        }
    }
}

fun main(args: Array<String>) {

    val indexfilename: String = args[0];
    val bibletextfilename: String = args[1];

    import(indexfilename, bibletextfilename)
}