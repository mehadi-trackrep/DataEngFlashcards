package com.example.dataengflashcards

data class DataEngQuestion(
    val id: Int = 0,
    val category: String,
    val difficulty: String, // "Basic", "Intermediate", "Advanced"
    val term: String,
    val definition: String,
    val example: String,
    val isLearning: Boolean = true,
    val isReviewing: Boolean = false,
    val isMastered: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class CategoryProgress(
    val categoryName: String,
    val totalWords: Int,
    val masteredWords: Int,
    val reviewingWords: Int,
    val learningWords: Int
) {
    val masteredPercentage: Float
        get() = if (totalWords > 0) (masteredWords.toFloat() / totalWords.toFloat()) * 100 else 0f

    val reviewingPercentage: Float
        get() = if (totalWords > 0) (reviewingWords.toFloat() / totalWords.toFloat()) * 100 else 0f

    val learningPercentage: Float
        get() = if (totalWords > 0) (learningWords.toFloat() / totalWords.toFloat()) * 100 else 0f
}