package question.skill.quiz

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.compose.ui.unit.sp
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import question.skill.quiz.ui.theme.QuizTheme

data class LeaderboardEntry(val name: String, val score: Int)

class QuestionScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val questions = listOf(
                        "Qual é a capital do Brasil?",
                        "Qual é a maior floresta tropical do mundo?",
                        "Qual é o planeta mais próximo do Sol?"
                    ).shuffled() // Embaralha as perguntas

                    val options = listOf(
                        listOf("São Paulo", "Rio de Janeiro", "Brasília", "Salvador"),
                        listOf("Amazônia", "Congo", "Sibéria", "Daintree"),
                        listOf("Vênus", "Terra", "Mercúrio", "Marte")
                    )

                    val correctAnswers = listOf(
                        "Brasília",
                        "Amazônia",
                        "Mercúrio"
                    )

                    QuestionGameScreen(
                        questions,
                        options,
                        correctAnswers,
                        Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionGameScreen(
    questions: List<String>,
    options: List<List<String>>,
    correctAnswers: List<String>,
    modifier: Modifier = Modifier
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var shuffledOptions by remember { mutableStateOf(options[currentQuestionIndex].shuffled()) }
    var showResult by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var startTime by remember { mutableStateOf(0L) }
    var finished by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("leaderboard", Context.MODE_PRIVATE)

    var userName by remember { mutableStateOf("") }
    var isNameSaved by remember { mutableStateOf(false) }

    fun startTimer() {
        startTime = System.currentTimeMillis()
    }

    fun calculateScore(selectedOption: String) {
        val timeTaken = System.currentTimeMillis() - startTime
        isCorrect = selectedOption == correctAnswers[currentQuestionIndex]
        if (isCorrect) {
            score += if (timeTaken < 10000) 15 else 10
        }
        showResult = true
    }

    fun nextQuestion() {
        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
            shuffledOptions = options[currentQuestionIndex].shuffled()
            showResult = false
            startTimer() // Reinicia o timer para a próxima pergunta
        } else {
            finished = true // Marca o quiz como concluído
        }
    }

    LaunchedEffect(currentQuestionIndex) {
        startTimer()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background_image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        if (!finished) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = questions[currentQuestionIndex],
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    textAlign = TextAlign.Center,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )

                Image(
                    painter = painterResource(id = R.drawable.exemplo),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(200.dp)
                        .height(200.dp)
                )

                shuffledOptions.forEach { option ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            calculateScore(option)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(text = option)
                    }
                }

                if (showResult) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isCorrect) "Resposta correta!" else "Resposta incorreta!",
                        color = if (isCorrect) Color.Green else Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { nextQuestion() }) {
                        Text(text = "Próxima Pergunta")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Quiz Finalizado!",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Pontuação Final: $score pontos",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (!isNameSaved) {
                    // Campo de entrada para o nome do usuário
                    TextField(
                        value = userName,
                        onValueChange = { userName = it },
                        label = { Text("Digite seu nome") },
                        textStyle = LocalTextStyle.current.copy(color = Color.Black),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (userName.isNotBlank()) {
                                saveScore(sharedPreferences, userName, score)
                                isNameSaved = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    ) {
                        Text(text = "Salvar Pontuação")
                    }
                } else {
                    Text(
                        text = "Pontuação salva com sucesso!",
                        fontWeight = FontWeight.Bold,
                        color = Color.Green
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val intent = Intent(context, MainMenuScreen::class.java)
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    ) {
                        Text(text = "Voltar ao Início")
                    }
                }
            }
        }
    }
}

fun saveScore(sharedPreferences: SharedPreferences, name: String, score: Int) {
    val gson = Gson()
    val leaderboardJson = sharedPreferences.getString("leaderboard_data", null)
    val type = object : TypeToken<MutableList<LeaderboardEntry>>() {}.type
    val leaderboard: MutableList<LeaderboardEntry> = if (leaderboardJson != null) {
        gson.fromJson(leaderboardJson, type)
    } else {
        mutableListOf()
    }

    leaderboard.add(LeaderboardEntry(name, score))

    val updatedJson = gson.toJson(leaderboard)
    sharedPreferences.edit().putString("leaderboard_data", updatedJson).apply()
}
