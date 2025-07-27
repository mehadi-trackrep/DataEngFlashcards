package com.example.dataengflashcards

data class DataEngQuestion(
    var id: Int = 0,
    var firebaseId: String? = null,
    val question: String = "",
    val answer: String = "",
    val category: String = "",
    val difficulty: String = "",
    var correctCount: Int = 0,
    var incorrectCount: Int = 0
) {
    // No-argument constructor for Firebase
    constructor() : this(0, null, "", "", "", "", 0, 0)
}