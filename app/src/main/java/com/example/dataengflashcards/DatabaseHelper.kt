package com.example.dataengflashcards

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "DataEngFlashcards.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_QUESTIONS = "questions"
        private const val COLUMN_ID = "id"
        private const val COLUMN_FIREBASE_ID = "firebase_id"
        private const val COLUMN_QUESTION = "question"
        private const val COLUMN_ANSWER = "answer"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_DIFFICULTY = "difficulty"
        private const val COLUMN_CORRECT_COUNT = "correct_count"
        private const val COLUMN_INCORRECT_COUNT = "incorrect_count"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_QUESTIONS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_FIREBASE_ID TEXT,
                $COLUMN_QUESTION TEXT NOT NULL,
                $COLUMN_ANSWER TEXT NOT NULL,
                $COLUMN_CATEGORY TEXT NOT NULL,
                $COLUMN_DIFFICULTY TEXT NOT NULL,
                $COLUMN_CORRECT_COUNT INTEGER DEFAULT 0,
                $COLUMN_INCORRECT_COUNT INTEGER DEFAULT 0
            )
        """.trimIndent()

        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_QUESTIONS")
        onCreate(db)
    }

    fun saveQuestionsFromFirebase(questions: List<DataEngQuestion>) {
        val db = writableDatabase

        // Clear existing data
        db.delete(TABLE_QUESTIONS, null, null)

        // Insert new data
        questions.forEach { question ->
            val values = ContentValues().apply {
                put(COLUMN_FIREBASE_ID, question.firebaseId)
                put(COLUMN_QUESTION, question.question)
                put(COLUMN_ANSWER, question.answer)
                put(COLUMN_CATEGORY, question.category)
                put(COLUMN_DIFFICULTY, question.difficulty)
                put(COLUMN_CORRECT_COUNT, question.correctCount)
                put(COLUMN_INCORRECT_COUNT, question.incorrectCount)
            }

            val id = db.insert(TABLE_QUESTIONS, null, values)
            question.id = id.toInt()
        }

        db.close()
    }

    fun getAllQuestions(): List<DataEngQuestion> {
        val questions = mutableListOf<DataEngQuestion>()
        val db = readableDatabase
        val cursor = db.query(TABLE_QUESTIONS, null, null, null, null, null, null)

        cursor.use {
            while (it.moveToNext()) {
                val question = DataEngQuestion(
                    id = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    firebaseId = it.getString(it.getColumnIndexOrThrow(COLUMN_FIREBASE_ID)),
                    question = it.getString(it.getColumnIndexOrThrow(COLUMN_QUESTION)),
                    answer = it.getString(it.getColumnIndexOrThrow(COLUMN_ANSWER)),
                    category = it.getString(it.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                    difficulty = it.getString(it.getColumnIndexOrThrow(COLUMN_DIFFICULTY)),
                    correctCount = it.getInt(it.getColumnIndexOrThrow(COLUMN_CORRECT_COUNT)),
                    incorrectCount = it.getInt(it.getColumnIndexOrThrow(COLUMN_INCORRECT_COUNT))
                )
                questions.add(question)
            }
        }

        db.close()
        return questions
    }

    fun updateQuestionStats(questionId: Int, isCorrect: Boolean) {
        val db = writableDatabase
        val column = if (isCorrect) COLUMN_CORRECT_COUNT else COLUMN_INCORRECT_COUNT

        db.execSQL("UPDATE $TABLE_QUESTIONS SET $column = $column + 1 WHERE $COLUMN_ID = ?", arrayOf(questionId.toString()))
        db.close()
    }
}
