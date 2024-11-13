package question.skill.quiz

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
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
import question.skill.quiz.ui.theme.QuizTheme

data class LeaderboardEntry(val name: String, val score: Int)

class QuestionScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Listas originais
                    val questions = listOf(
                        "Qual é o tipo secundário do Pokémon Gyarados, além do tipo Water?",
                        "Em qual geração foi introduzido o tipo Dark?",
                        "Qual Pokémon é necessário para obter uma Prism Scale para evoluir Feebas em Milotic?",
                        "Em qual cidade da região de Kanto se encontra o ginásio de tipo Psychic, liderado por Sabrina?",
                        "Qual Pokémon é conhecido como \"The Genetic Pokémon\" e é um clone de Mew?",
                        "Qual golpe, que Ash usa frequentemente com seu Pikachu, é do tipo Electric e tem a habilidade de paralisar o oponente?",
                        "Qual Pokémon pode evoluir para três tipos diferentes (Vaporeon, Jolteon ou Flareon) na primeira geração?",
                        "Qual item é necessário para evoluir o Pokémon Poliwhirl em Politoed?",
                        "Qual a habilidade que garante imunidade a golpes do tipo Ground?",
                        "Qual Pokémon tem as formas de Attack, Defense, Speed e Normal, dependendo da sua localização?",
                        "Qual é o golpe do tipo Bug que pode reduzir a Special Defense do oponente e foi introduzido na terceira geração?",
                        "Em qual geração foi introduzida a mecânica de Mega Evolução?",
                        "Qual é a habilidade única de Shedinja que permite que ele só seja atingido por golpes que são super efetivos?",
                        "Qual item é necessário para evoluir o Pokémon Sneasel em Weavile?",
                        "Qual Pokémon do tipo Ghost/Dragon foi introduzido na quarta geração como parte do trio lendário \"Creation Trio\"?"
                    )

                    val options = listOf(
                        listOf("Flying", "Dragon", "Poison", "Ground"),
                        listOf("Segunda geração", "Primeira geração", "Terceira geração", "Quarta geração"),
                        listOf("Feebas", "Gyarados", "Magikarp", "Lapras"),
                        listOf("Saffron City", "Celadon City", "Cerulean City", "Fuchsia City"),
                        listOf("Mewtwo", "Mew", "Ditto", "Genesect"),
                        listOf("Thunderbolt", "Thunder Shock", "Iron Tail", "Volt Tackle"),
                        listOf("Eevee", "Pikachu", "Clefairy", "Meowth"),
                        listOf("King's Rock", "Water Stone", "Moon Stone", "Sun Stone"),
                        listOf("Levitate", "Sturdy", "Intimidate", "Mold Breaker"),
                        listOf("Deoxys", "Castform", "Rotom", "Arceus"),
                        listOf("Bug Buzz", "Leech Life", "X-Scissor", "Signal Beam"),
                        listOf("Sexta geração", "Quarta geração", "Quinta geração", "Sétima geração"),
                        listOf("Wonder Guard", "Magic Guard", "Levitate", "Pressure"),
                        listOf("Razor Claw", "Razor Fang", "Black Belt", "Metal Coat"),
                        listOf("Giratina", "Palkia", "Dialga", "Darkrai")
                    )

                    val correctAnswers = listOf(
                        "Flying",
                        "Segunda geração",
                        "Feebas",
                        "Saffron City",
                        "Mewtwo",
                        "Thunderbolt",
                        "Eevee",
                        "King's Rock",
                        "Levitate",
                        "Deoxys",
                        "Bug Buzz",
                        "Sexta geração",
                        "Wonder Guard",
                        "Razor Claw",
                        "Giratina"
                    )

                    val questionImages = listOf(
                        R.drawable.question1,
                        R.drawable.question2,
                        R.drawable.question3,
                        R.drawable.question4,
                        R.drawable.question5,
                        R.drawable.question6,
                        R.drawable.question7,
                        R.drawable.question8,
                        R.drawable.question9,
                        R.drawable.question10,
                        R.drawable.question11,
                        R.drawable.question12,
                        R.drawable.question13,
                        R.drawable.question14,
                        R.drawable.question15
                    )

                    // Embaralha as listas juntas
                    val indices = questions.indices.toList().shuffled()
                    val shuffledQuestions = indices.map { questions[it] }
                    val shuffledOptions = indices.map { options[it] }
                    val shuffledCorrectAnswers = indices.map { correctAnswers[it] }
                    val shuffledQuestionImages = indices.map { questionImages[it] }

                    QuestionGameScreen(
                        shuffledQuestions,
                        shuffledOptions,
                        shuffledCorrectAnswers,
                        shuffledQuestionImages,
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
    questionImages: List<Int>,
    modifier: Modifier = Modifier
) {
    var enabled by remember { mutableStateOf(true) }
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
        enabled = false
        val timeTaken = System.currentTimeMillis() - startTime
        isCorrect = selectedOption == correctAnswers[currentQuestionIndex]
        if (isCorrect) {
            var mediaPlayer = MediaPlayer.create(context, R.raw.acerto)
            mediaPlayer.start()
            score += if (timeTaken < 10000) 15 else 10
        }
        else {
            var mediaPlayer = MediaPlayer.create(context, R.raw.erro)
            mediaPlayer.start()
        }
        showResult = true
    }

    fun nextQuestion() {
        enabled = true
        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
            shuffledOptions = options[currentQuestionIndex].shuffled()
            showResult = false
            startTimer()
        } else {
            finished = true
        }
    }

    LaunchedEffect(currentQuestionIndex) {
        startTimer()
        shuffledOptions = options[currentQuestionIndex].shuffled()
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
                    painter = painterResource(id = questionImages[currentQuestionIndex]),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(400.dp)
                        .height(400.dp)
                        .padding(16.dp)
                )

                shuffledOptions.forEach { option ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        enabled = enabled,
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
    val leaderboardData = sharedPreferences.getString("leaderboard_data", "") ?: ""
    val safeName = name.replace("|", "").replace(",", "")
    val newEntry = "$safeName,$score"
    val updatedLeaderboardData = if (leaderboardData.isEmpty()) {
        newEntry
    } else {
        "$leaderboardData|$newEntry"
    }
    sharedPreferences.edit().putString("leaderboard_data", updatedLeaderboardData).apply()
}
