package question.skill.quiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import question.skill.quiz.ui.theme.QuizTheme
import question.skill.quiz.R

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
                    )
                    val options = listOf(
                        listOf("São Paulo", "Rio de Janeiro", "Brasília", "Salvador"),
                        listOf("Amazônia", "Congo", "Sibéria", "Daintree"),
                        listOf("Vênus", "Terra", "Mercúrio", "Marte")
                    )
                    QuestionGameScreen(questions, options, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun QuestionGameScreen(
    questions: List<String>,
    options: List<List<String>>,
    modifier: Modifier = Modifier
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background_image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

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
            options[currentQuestionIndex].forEach { option ->
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        currentQuestionIndex = (currentQuestionIndex + 1) % questions.size
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(text = option)
                }
            }
        }
    }
}
