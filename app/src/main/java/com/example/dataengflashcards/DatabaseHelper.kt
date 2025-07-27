package com.example.dataengflashcards

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "DataEngFlashcards.db"
        private const val DATABASE_VERSION = 2

        private const val TABLE_QUESTIONS = "questions"
        private const val COLUMN_ID = "id"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_DIFFICULTY = "difficulty"
        private const val COLUMN_TERM = "term"
        private const val COLUMN_DEFINITION = "definition"
        private const val COLUMN_EXAMPLE = "example"
        private const val COLUMN_IS_LEARNING = "is_learning"
        private const val COLUMN_IS_REVIEWING = "is_reviewing"
        private const val COLUMN_IS_MASTERED = "is_mastered"
        private const val COLUMN_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE $TABLE_QUESTIONS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CATEGORY TEXT NOT NULL,
                $COLUMN_DIFFICULTY TEXT NOT NULL,
                $COLUMN_TERM TEXT NOT NULL,
                $COLUMN_DEFINITION TEXT NOT NULL,
                $COLUMN_EXAMPLE TEXT NOT NULL,
                $COLUMN_IS_LEARNING INTEGER DEFAULT 1,
                $COLUMN_IS_REVIEWING INTEGER DEFAULT 0,
                $COLUMN_IS_MASTERED INTEGER DEFAULT 0,
                $COLUMN_CREATED_AT INTEGER NOT NULL
            )
        """.trimIndent()

        db?.execSQL(createTable)
        insertSampleData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_QUESTIONS")
        onCreate(db)
    }

    private fun insertSampleData(db: SQLiteDatabase?) {
        val sampleQuestions = listOf(
            DataEngQuestion(0, "Common Quiz I", "Basic", "ETL", "Extract, Transform, Load - a data integration process", "An ETL pipeline extracts data from a source database, transforms it by cleaning and formatting, then loads it into a data warehouse for analysis."),
            DataEngQuestion(0, "Common Quiz I", "Basic", "Schema", "The structure or blueprint that defines how data is organized", "A database schema defines table structures, including column names, data types, and relationships between tables."),
            DataEngQuestion(0, "Common Quiz I", "Basic", "Pipeline", "A series of data processing steps connected in sequence", "A data pipeline might involve extracting data from APIs, cleaning it, transforming formats, and loading into a database automatically."),
            DataEngQuestion(0, "Common Quiz I", "Basic", "Batch Processing", "Processing data in large chunks at scheduled intervals", "A company runs batch processing every night to analyze the day's sales data and generate reports."),
            DataEngQuestion(0, "Common Quiz I", "Basic", "Streaming", "Processing data in real-time as it arrives", "Kafka streaming processes live user clicks on a website to update recommendation engines instantly."),

            // Common Quiz II
            DataEngQuestion(0, "Common Quiz II", "Intermediate", "Partitioning", "Dividing large datasets into smaller, manageable pieces", "A database table is partitioned by date, so queries for recent data only scan relevant partitions, improving performance."),
            DataEngQuestion(0, "Common Quiz II", "Intermediate", "Sharding", "Distributing data across multiple database instances", "A social media platform shards user data by geographic region, storing European users on servers in Europe."),
            DataEngQuestion(0, "Common Quiz II", "Intermediate", "Replication", "Creating copies of data across multiple locations", "Database replication ensures that if the primary server fails, a replica can take over without data loss."),
            DataEngQuestion(0, "Common Quiz II", "Intermediate", "Normalization", "Organizing data to reduce redundancy and improve integrity", "Normalizing a customer database splits address information into a separate table to avoid repeating addresses for multiple orders."),
            DataEngQuestion(0, "Common Quiz II", "Intermediate", "Denormalization", "Combining normalized data for better query performance", "A reporting database denormalizes customer and order data into a single table to speed up analytics queries."),

            // Common Quiz III
            DataEngQuestion(0, "Common Quiz III", "Advanced", "Idempotency", "Property where repeated operations produce the same result", "An idempotent API call for creating a user will not create duplicates if called multiple times with the same data."),
            DataEngQuestion(0, "Common Quiz III", "Advanced", "Eventual Consistency", "Data consistency achieved over time in distributed systems", "In a distributed database, a write might not be immediately visible on all nodes, but eventually all nodes will have consistent data."),
            DataEngQuestion(0, "Common Quiz III", "Advanced", "CAP Theorem", "Principle stating systems can guarantee only 2 of: Consistency, Availability, Partition tolerance", "During a network partition, a database must choose between remaining available (potentially serving stale data) or maintaining consistency (becoming unavailable)."),
            DataEngQuestion(0, "Common Quiz III", "Advanced", "ACID Properties", "Atomicity, Consistency, Isolation, Durability - database transaction properties", "A bank transfer transaction must be atomic (all or nothing), consistent (valid states), isolated (no interference), and durable (permanent once committed)."),
            DataEngQuestion(0, "Common Quiz III", "Advanced", "Data Lineage", "The flow and transformation of data from source to destination", "Data lineage tracking shows that a customer analytics report derives from CRM data, transformed through three ETL steps, and loaded into a data warehouse."),

            // Fill in the Blank I
            DataEngQuestion(0, "Common Quiz - Fill in the Blank I", "Basic", "Data Lake", "A storage repository that holds raw data in its native format", "Companies store unstructured data like logs, images, and documents in a _____ before processing them for analytics."),
            DataEngQuestion(0, "Common Quiz - Fill in the Blank I", "Basic", "Data Warehouse", "A central repository of integrated data from multiple sources", "Business intelligence tools query the _____ to generate executive dashboards and reports."),
            DataEngQuestion(0, "Common Quiz - Fill in the Blank I", "Basic", "OLTP", "Online Transaction Processing - systems that handle day-to-day transactions", "An e-commerce website uses _____ systems to process customer orders and payments in real-time."),
            DataEngQuestion(0, "Common Quiz - Fill in the Blank I", "Basic", "OLAP", "Online Analytical Processing - systems optimized for complex queries", "Data analysts use _____ cubes to perform multidimensional analysis of sales data across time, geography, and products."),

            // Common Quiz IV
            DataEngQuestion(0, "Common Quiz IV", "Advanced", "Lambda Architecture", "Data processing architecture with batch and stream processing layers", "Netflix uses lambda architecture to provide both real-time recommendations (speed layer) and comprehensive analytics (batch layer)."),
            DataEngQuestion(0, "Common Quiz IV", "Advanced", "Kappa Architecture", "Stream-first architecture where all data is treated as a stream", "LinkedIn's data platform uses kappa architecture, processing all data as streams and maintaining different views for batch and real-time needs."),
            DataEngQuestion(0, "Common Quiz IV", "Advanced", "Data Mesh", "Decentralized data architecture with domain-owned data products", "Large organizations implement data mesh to let each business domain manage their own data as products with clear APIs."),
            DataEngQuestion(0, "Common Quiz IV", "Advanced", "Change Data Capture", "Method of tracking changes in source systems", "CDC tools monitor database transaction logs to capture every insert, update, and delete for real-time data synchronization.")
        )

        sampleQuestions.forEach { question ->
            val values = ContentValues().apply {
                put(COLUMN_CATEGORY, question.category)
                put(COLUMN_DIFFICULTY, question.difficulty)
                put(COLUMN_TERM, question.term)
                put(COLUMN_DEFINITION, question.definition)
                put(COLUMN_EXAMPLE, question.example)
                put(COLUMN_IS_LEARNING, if (question.isLearning) 1 else 0)
                put(COLUMN_IS_REVIEWING, if (question.isReviewing) 1 else 0)
                put(COLUMN_IS_MASTERED, if (question.isMastered) 1 else 0)
                put(COLUMN_CREATED_AT, question.createdAt)
            }
            db?.insert(TABLE_QUESTIONS, null, values)
        }
    }

    fun getAllCategories(): List<CategoryProgress> {
        val categories = mutableListOf<CategoryProgress>()
        val db = readableDatabase

        val query = """
            SELECT 
                $COLUMN_CATEGORY,
                COUNT(*) as total,
                SUM(CASE WHEN $COLUMN_IS_MASTERED = 1 THEN 1 ELSE 0 END) as mastered,
                SUM(CASE WHEN $COLUMN_IS_REVIEWING = 1 THEN 1 ELSE 0 END) as reviewing,
                SUM(CASE WHEN $COLUMN_IS_LEARNING = 1 THEN 1 ELSE 0 END) as learning
            FROM $TABLE_QUESTIONS 
            GROUP BY $COLUMN_CATEGORY
            ORDER BY $COLUMN_CATEGORY
        """.trimIndent()

        val cursor = db.rawQuery(query, null)

        cursor.use {
            while (it.moveToNext()) {
                val categoryName = it.getString(0)
                val total = it.getInt(1)
                val mastered = it.getInt(2)
                val reviewing = it.getInt(3)
                val learning = it.getInt(4)

                categories.add(CategoryProgress(categoryName, total, mastered, reviewing, learning))
            }
        }

        return categories
    }

    fun getQuestionsByCategory(category: String): List<DataEngQuestion> {
        val questions = mutableListOf<DataEngQuestion>()
        val db = readableDatabase

        val cursor = db.query(
            TABLE_QUESTIONS,
            null,
            "$COLUMN_CATEGORY = ? AND $COLUMN_IS_LEARNING = 1",
            arrayOf(category),
            null, null, null
        )

        cursor.use {
            while (it.moveToNext()) {
                questions.add(
                    DataEngQuestion(
                        id = it.getInt(0),
                        category = it.getString(1),
                        difficulty = it.getString(2),
                        term = it.getString(3),
                        definition = it.getString(4),
                        example = it.getString(5),
                        isLearning = it.getInt(6) == 1,
                        isReviewing = it.getInt(7) == 1,
                        isMastered = it.getInt(8) == 1,
                        createdAt = it.getLong(9)
                    )
                )
            }
        }

        return questions
    }

    fun updateQuestionStatus(questionId: Int, knew: Boolean) {
        val db = writableDatabase
        val values = ContentValues()

        if (knew) {
            values.put(COLUMN_IS_LEARNING, 0)
            values.put(COLUMN_IS_REVIEWING, 0)
            values.put(COLUMN_IS_MASTERED, 1)
        } else {
            values.put(COLUMN_IS_LEARNING, 0)
            values.put(COLUMN_IS_REVIEWING, 1)
            values.put(COLUMN_IS_MASTERED, 0)
        }

        db.update(TABLE_QUESTIONS, values, "$COLUMN_ID = ?", arrayOf(questionId.toString()))
    }

    fun resetCategoryProgress(category: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IS_LEARNING, 1)
            put(COLUMN_IS_REVIEWING, 0)
            put(COLUMN_IS_MASTERED, 0)
        }

        db.update(TABLE_QUESTIONS, values, "$COLUMN_CATEGORY = ?", arrayOf(category))
    }

    fun getAllQuestionsByCategory(category: String): List<DataEngQuestion> {
        val questions = mutableListOf<DataEngQuestion>()
        val db = readableDatabase

        val cursor = db.query(
            TABLE_QUESTIONS,
            null,
            "$COLUMN_CATEGORY = ?",
            arrayOf(category),
            null, null, null
        )

        cursor.use {
            while (it.moveToNext()) {
                questions.add(
                    DataEngQuestion(
                        id = it.getInt(0),
                        category = it.getString(1),
                        difficulty = it.getString(2),
                        term = it.getString(3),
                        definition = it.getString(4),
                        example = it.getString(5),
                        isLearning = it.getInt(6) == 1,
                        isReviewing = it.getInt(7) == 1,
                        isMastered = it.getInt(8) == 1,
                        createdAt = it.getLong(9)
                    )
                )
            }
        }

        return questions
    }
}