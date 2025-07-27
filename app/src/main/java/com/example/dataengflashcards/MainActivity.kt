package com.example.dataengflashcards

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var firebaseDatabase: DatabaseReference

    private lateinit var questionText: TextView
    private lateinit var answerText: TextView
    private lateinit var showAnswerBtn: Button
    private lateinit var nextBtn: Button
    private lateinit var correctBtn: Button
    private lateinit var incorrectBtn: Button
    private lateinit var progressText: TextView
    private lateinit var loadingProgressBar: ProgressBar

    private var currentQuestion: DataEngQuestion? = null
    private var questionsList = mutableListOf<DataEngQuestion>()
    private var remainingQuestions = mutableListOf<DataEngQuestion>()
    private var isAnswerVisible = false
    private var currentIndex = 0
    private var correctAnswers = 0
    private var totalQuestions = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initFirebase()
        dbHelper = DatabaseHelper(this)

        loadQuestionsFromFirebase()
    }

    private fun initViews() {
        questionText = findViewById(R.id.questionText)
        answerText = findViewById(R.id.answerText)
        showAnswerBtn = findViewById(R.id.showAnswerBtn)
        nextBtn = findViewById(R.id.nextBtn)
        correctBtn = findViewById(R.id.correctBtn)
        incorrectBtn = findViewById(R.id.incorrectBtn)
        progressText = findViewById(R.id.progressText)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)

        showAnswerBtn.setOnClickListener { showAnswer() }
        nextBtn.setOnClickListener { loadNextQuestion() }
        correctBtn.setOnClickListener { markAnswer(true) }
        incorrectBtn.setOnClickListener { markAnswer(false) }
    }

    private fun initFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("data_engineering_questions")
    }

    private fun loadQuestionsFromFirebase() {
        showLoading(true)

        firebaseDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                questionsList.clear()

                for (snapshot in dataSnapshot.children) {
                    val question = snapshot.getValue(DataEngQuestion::class.java)
                    question?.let {
                        it.firebaseId = snapshot.key
                        questionsList.add(it)
                    }
                }

                showLoading(false)

                if (questionsList.isEmpty()) {
                    createSampleDataInFirebase()
                } else {
                    dbHelper.saveQuestionsFromFirebase(questionsList)
                    initializeSession()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                showLoading(false)
                Toast.makeText(this@MainActivity, "Failed to load questions: ${databaseError.message}", Toast.LENGTH_LONG).show()

                // Fallback to local database
                questionsList = dbHelper.getAllQuestions().toMutableList()
                if (questionsList.isNotEmpty()) {
                    initializeSession()
                }
            }
        })
    }

    private fun createSampleDataInFirebase() {
        val sampleQuestions = getSampleQuestions()

        sampleQuestions.forEach { question ->
            val key = firebaseDatabase.push().key
            key?.let {
                firebaseDatabase.child(it).setValue(question)
            }
        }

        questionsList = sampleQuestions.toMutableList()
        dbHelper.saveQuestionsFromFirebase(questionsList)
        initializeSession()

        Toast.makeText(this, "Sample questions created in Firebase", Toast.LENGTH_SHORT).show()
    }

    private fun getSampleQuestions(): List<DataEngQuestion> {
        return listOf(
            DataEngQuestion(
                question = "What is ETL?",
                answer = "Extract, Transform, Load - a process of extracting data from various sources, transforming it into a suitable format, and loading it into a data warehouse or database.",
                category = "Fundamentals",
                difficulty = "Beginner"
            ),
            DataEngQuestion(
                question = "What is a Data Pipeline?",
                answer = "A series of data processing steps where data is ingested from various sources, processed through transformations, and delivered to a destination system for analysis or storage.",
                category = "Architecture",
                difficulty = "Beginner"
            ),
            DataEngQuestion(
                question = "What is ACID in databases?",
                answer = "Atomicity, Consistency, Isolation, Durability - properties that guarantee database transactions are processed reliably even in the event of errors, power failures, etc.",
                category = "Databases",
                difficulty = "Intermediate"
            ),
            DataEngQuestion(
                question = "What is Apache Spark?",
                answer = "A unified analytics engine for large-scale data processing that provides high-level APIs and supports SQL queries, streaming data, and machine learning.",
                category = "Big Data",
                difficulty = "Intermediate"
            ),
            DataEngQuestion(
                question = "What is Data Partitioning?",
                answer = "The process of dividing a large dataset into smaller, more manageable pieces based on certain criteria (like date ranges or hash values) to improve query performance.",
                category = "Optimization",
                difficulty = "Intermediate"
            ),
            DataEngQuestion(
                question = "What is Apache Kafka?",
                answer = "A distributed streaming platform used for building real-time data pipelines and streaming applications. It can handle high-throughput, fault-tolerant data streams.",
                category = "Streaming",
                difficulty = "Intermediate"
            ),
            DataEngQuestion(
                question = "What is a Data Warehouse?",
                answer = "A centralized repository that stores integrated data from multiple sources, optimized for analytical queries and reporting rather than transactional processing.",
                category = "Architecture",
                difficulty = "Beginner"
            ),
            DataEngQuestion(
                question = "What is Data Modeling?",
                answer = "The process of creating a conceptual representation of data structures and their relationships to support business processes and analytical requirements.",
                category = "Design",
                difficulty = "Intermediate"
            ),
            DataEngQuestion(
                question = "What is Apache Airflow?",
                answer = "An open-source workflow orchestration platform used to programmatically author, schedule, and monitor data pipelines as Directed Acyclic Graphs (DAGs).",
                category = "Orchestration",
                difficulty = "Advanced"
            ),
            DataEngQuestion(
                question = "What is Column-oriented storage?",
                answer = "A database storage method where data is stored by columns rather than rows, optimizing for analytical queries that typically access specific columns across many rows.",
                category = "Storage",
                difficulty = "Advanced"
            ),
            DataEngQuestion(
                question = "What is Data Lineage?",
                answer = "The tracking of data's journey from its origin through various transformations to its final destination, providing visibility into data flow and dependencies.",
                category = "Governance",
                difficulty = "Intermediate"
            ),
            DataEngQuestion(
                question = "What is Apache Hive?",
                answer = "A data warehouse software that facilitates reading, writing, and managing large datasets in distributed storage using SQL-like queries (HiveQL).",
                category = "Big Data",
                difficulty = "Intermediate"
            ),
            DataEngQuestion(
                question = "What is Change Data Capture (CDC)?",
                answer = "A technique used to identify and track changes made to data in a database, enabling real-time data integration and synchronization between systems.",
                category = "Integration",
                difficulty = "Advanced"
            ),
            DataEngQuestion(
                question = "What is Data Schema?",
                answer = "A blueprint that defines the structure, organization, and constraints of data in a database, including tables, fields, relationships, and data types.",
                category = "Design",
                difficulty = "Beginner"
            ),
            DataEngQuestion(
                question = "What is Apache Hadoop?",
                answer = "An open-source framework for distributed storage and processing of large datasets across clusters of computers using simple programming models.",
                category = "Big Data",
                difficulty = "Intermediate"
            ),
            DataEngQuestion(
                question = "What is a Data Lake?",
                answer = "A centralized repository that allows you to store all your structured and unstructured data at any scale, without having to first structure the data.",
                category = "Architecture",
                difficulty = "Intermediate"
            ),
            DataEngQuestion(
                question = "What is Apache Flink?",
                answer = "A stream processing framework for distributed, high-performing, always-available, and accurate data streaming applications.",
                category = "Streaming",
                difficulty = "Advanced"
            ),
            DataEngQuestion(
                question = "What is Data Mesh?",
                answer = "A decentralized data architecture paradigm that treats data as a product, with domain-oriented ownership and federated governance.",
                category = "Architecture",
                difficulty = "Advanced"
            )
        )
    }

    private fun initializeSession() {
        remainingQuestions = questionsList.toMutableList()
        totalQuestions = questionsList.size
        currentIndex = 0
        correctAnswers = 0
        loadNextQuestion()
    }

    private fun loadNextQuestion() {
        if (remainingQuestions.isEmpty()) {
            showCompletionDialog()
            return
        }

        val randomIndex = Random.nextInt(remainingQuestions.size)
        currentQuestion = remainingQuestions[randomIndex]
        remainingQuestions.removeAt(randomIndex)
        currentIndex++

        questionText.text = currentQuestion?.question
        answerText.text = currentQuestion?.answer
        answerText.visibility = View.GONE
        isAnswerVisible = false

        showAnswerBtn.visibility = View.VISIBLE
        correctBtn.visibility = View.GONE
        incorrectBtn.visibility = View.GONE
        nextBtn.visibility = View.GONE

        updateProgress()
    }

    private fun showCompletionDialog() {
        val percentage = if (totalQuestions > 0) (correctAnswers * 100) / totalQuestions else 0

        val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
        dialogBuilder.setTitle("🎉 Session Complete!")
        dialogBuilder.setMessage(
            "Great job! Here are your results:\n\n" +
                    "📊 Questions Answered: $totalQuestions\n" +
                    "✅ Correct Answers: $correctAnswers\n" +
                    "❌ Incorrect Answers: ${totalQuestions - correctAnswers}\n" +
                    "📈 Success Rate: $percentage%\n\n" +
                    getPerformanceFeedback(percentage)
        )
        dialogBuilder.setPositiveButton("Start New Session") { _, _ ->
            initializeSession()
        }
        dialogBuilder.setNegativeButton("Exit") { _, _ ->
            finish()
        }
        dialogBuilder.setCancelable(false)
        dialogBuilder.show()
    }

    private fun getPerformanceFeedback(percentage: Int): String {
        return when {
            percentage >= 90 -> "🌟 Outstanding! You're a Data Engineering expert!"
            percentage >= 80 -> "🔥 Excellent work! You have strong knowledge!"
            percentage >= 70 -> "👍 Good job! You're on the right track!"
            percentage >= 60 -> "📚 Not bad! Keep studying to improve!"
            else -> "💪 Keep practicing! You'll get better with time!"
        }
    }

    private fun showAnswer() {
        answerText.visibility = View.VISIBLE
        showAnswerBtn.visibility = View.GONE
        correctBtn.visibility = View.VISIBLE
        incorrectBtn.visibility = View.VISIBLE
        isAnswerVisible = true
    }

    private fun markAnswer(isCorrect: Boolean) {
        currentQuestion?.let { question ->
            // Update local database
            dbHelper.updateQuestionStats(question.id, isCorrect)

            // Update Firebase
            updateFirebaseStats(isCorrect)

            // Update session stats
            if (isCorrect) {
                correctAnswers++
                Toast.makeText(this, "Correct! 🎉", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Keep practicing! 💪", Toast.LENGTH_SHORT).show()
            }
        }

        correctBtn.visibility = View.GONE
        incorrectBtn.visibility = View.GONE
        nextBtn.visibility = View.VISIBLE
    }

    private fun updateFirebaseStats(isCorrect: Boolean) {
        currentQuestion?.let { question ->
            question.firebaseId?.let { firebaseId ->
                val questionRef = firebaseDatabase.child(firebaseId)

                if (isCorrect) {
                    questionRef.child("correctCount").setValue(question.correctCount + 1)
                    question.correctCount++
                } else {
                    questionRef.child("incorrectCount").setValue(question.incorrectCount + 1)
                    question.incorrectCount++
                }
            }
        }
    }

    private fun updateProgress() {
        progressText.text = "Question $currentIndex of $totalQuestions"
    }

    private fun showLoading(show: Boolean) {
        loadingProgressBar.visibility = if (show) View.VISIBLE else View.GONE
        if (show) {
            questionText.text = "Loading questions from Firebase..."
            answerText.visibility = View.GONE
            showAnswerBtn.visibility = View.GONE
            correctBtn.visibility = View.GONE
            incorrectBtn.visibility = View.GONE
            nextBtn.visibility = View.GONE
        }
    }
}