package com.example.dataengflashcards

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var categoriesAdapter: CategoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        setupViews()
        loadCategories()
    }

    private fun setupViews() {
        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView)
        categoriesRecyclerView.layoutManager = LinearLayoutManager(this)

        categoriesAdapter = CategoriesAdapter { category ->
            val intent = Intent(this, FlashcardActivity::class.java)
            intent.putExtra("CATEGORY_NAME", category.categoryName)
            startActivity(intent)
        }

        categoriesRecyclerView.adapter = categoriesAdapter
    }

    private fun loadCategories() {
        val categories = dbHelper.getAllCategories()
        categoriesAdapter.updateCategories(categories)
    }

    override fun onResume() {
        super.onResume()
        loadCategories() // Refresh data when returning from flashcard activity
    }
}

class CategoriesAdapter(private val onCategoryClick: (CategoryProgress) -> Unit) :
    RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    private var categories = listOf<CategoryProgress>()

    fun updateCategories(newCategories: List<CategoryProgress>) {
        categories = newCategories
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): CategoryViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount() = categories.size

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryTitle: TextView = itemView.findViewById(R.id.categoryTitle)
        private val masteredProgress: TextView = itemView.findViewById(R.id.masteredProgress)
        private val masteredProgressBar: ProgressBar = itemView.findViewById(R.id.masteredProgressBar)
        private val practiceButton: Button = itemView.findViewById(R.id.practiceButton)

        fun bind(category: CategoryProgress) {
            categoryTitle.text = category.categoryName
            masteredProgress.text = "${category.masteredWords} of ${category.totalWords} dataeng quests mastered"
            masteredProgressBar.progress = category.masteredPercentage.toInt()

            practiceButton.setOnClickListener {
                onCategoryClick(category)
            }

            itemView.setOnClickListener {
                onCategoryClick(category)
            }
        }
    }
}

class FlashcardActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var questions: List<DataEngQuestion>
    private var currentQuestionIndex = 0

    private lateinit var categoryTitle: TextView
    private lateinit var termText: TextView
    private lateinit var definitionText: TextView
    private lateinit var exampleText: TextView
    private lateinit var tapToSeeButton: Button
    private lateinit var knewItButton: Button
    private lateinit var didntKnowButton: Button
    private lateinit var backButton: ImageButton
    private lateinit var statusTag: TextView
    private lateinit var masteredProgress: TextView
    private lateinit var reviewingProgress: TextView
    private lateinit var learningProgress: TextView
    private lateinit var masteredProgressBar: ProgressBar
    private lateinit var reviewingProgressBar: ProgressBar
    private lateinit var learningProgressBar: ProgressBar

    private var isDefinitionVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard)

        dbHelper = DatabaseHelper(this)

        setupViews()
        loadQuestions()
        displayCurrentQuestion()
    }

    private fun setupViews() {
        categoryTitle = findViewById(R.id.categoryTitle)
        termText = findViewById(R.id.termText)
        definitionText = findViewById(R.id.definitionText)
        exampleText = findViewById(R.id.exampleText)
        tapToSeeButton = findViewById(R.id.tapToSeeButton)
        knewItButton = findViewById(R.id.knewItButton)
        didntKnowButton = findViewById(R.id.didntKnowButton)
        backButton = findViewById(R.id.backButton)
        statusTag = findViewById(R.id.statusTag)
        masteredProgress = findViewById(R.id.masteredProgress)
        reviewingProgress = findViewById(R.id.reviewingProgress)
        learningProgress = findViewById(R.id.learningProgress)
        masteredProgressBar = findViewById(R.id.masteredProgressBar)
        reviewingProgressBar = findViewById(R.id.reviewingProgressBar)
        learningProgressBar = findViewById(R.id.learningProgressBar)

        tapToSeeButton.setOnClickListener {
            showDefinition()
        }

        knewItButton.setOnClickListener {
            handleAnswer(true)
        }

        didntKnowButton.setOnClickListener {
            handleAnswer(false)
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadQuestions() {
        val categoryName = intent.getStringExtra("CATEGORY_NAME") ?: return
        categoryTitle.text = categoryName
        questions = dbHelper.getQuestionsByCategory(categoryName)

        if (questions.isEmpty()) {
            Toast.makeText(this, "No questions available for this category", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun displayCurrentQuestion() {
        if (currentQuestionIndex >= questions.size) {
            Toast.makeText(this, "You've completed all questions in this category!", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val question = questions[currentQuestionIndex]

        termText.text = question.term
        definitionText.text = question.definition
        exampleText.text = question.example

        statusTag.text = "LEARNING"
        statusTag.setBackgroundResource(R.drawable.learning_tag_background)

        hideDefinition()
        updateProgressBars()
    }

    private fun updateProgressBars() {
        val categories = dbHelper.getAllCategories()
        val currentCategory = categories.find { it.categoryName == categoryTitle.text.toString() }

        currentCategory?.let { category ->
            masteredProgress.text = "You have mastered ${category.masteredWords} out of ${category.totalWords} quests"
            reviewingProgress.text = "You are reviewing ${category.reviewingWords} out of ${category.totalWords} quests"
            learningProgress.text = "You are learning ${category.learningWords} out of ${category.totalWords} quests"

            masteredProgressBar.progress = category.masteredPercentage.toInt()
            reviewingProgressBar.progress = category.reviewingPercentage.toInt()
            learningProgressBar.progress = category.learningPercentage.toInt()
        }
    }

    private fun showDefinition() {
        isDefinitionVisible = true
        definitionText.visibility = View.VISIBLE
        exampleText.visibility = View.VISIBLE
        tapToSeeButton.visibility = View.GONE
        knewItButton.visibility = View.VISIBLE
        didntKnowButton.visibility = View.VISIBLE
    }

    private fun hideDefinition() {
        isDefinitionVisible = false
        definitionText.visibility = View.GONE
        exampleText.visibility = View.GONE
        tapToSeeButton.visibility = View.VISIBLE
        knewItButton.visibility = View.GONE
        didntKnowButton.visibility = View.GONE
    }

    private fun handleAnswer(knew: Boolean) {
        val question = questions[currentQuestionIndex]
        dbHelper.updateQuestionStatus(question.id, knew)

        currentQuestionIndex++

        if (currentQuestionIndex < questions.size) {
            displayCurrentQuestion()
        } else {
            Toast.makeText(this, "Great job! You've completed this session.", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}