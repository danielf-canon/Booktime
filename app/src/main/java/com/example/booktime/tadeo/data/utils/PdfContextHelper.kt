package com.example.booktime.tadeo.data.utils

object PdfContextHelper {

    fun getRelevantContext(
        fullText: String,
        question: String
    ): String {

        if (fullText.isBlank()) {
            return ""
        }

        val paragraphs =
            fullText.split("\n")

        val keywords =
            question.lowercase()
                .split(" ")
                .filter { it.length > 3 }

        val relevantParagraphs =
            paragraphs.filter { paragraph ->

                keywords.any { keyword ->
                    paragraph.lowercase().contains(keyword)
                }
            }

        return relevantParagraphs
            .take(8)
            .joinToString("\n")
    }
}