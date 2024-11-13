package question.skill.quiz

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        val topView: View = findViewById(R.id.TopTextView)
        val middleView: View = findViewById(R.id.MiddleTextView)
        val bottomView: View = findViewById(R.id.BottomTextView)

        startAnimations(topView, middleView, bottomView)

        startWithDelay {
            startActivity(Intent(this, MainMenuScreen::class.java))
            finish()
        }
    }

    private fun startAnimations(topView: View, middleView: View, bottomView: View) {
        val topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        val middleAnimation = AnimationUtils.loadAnimation(this, R.anim.middle_animation)
        val bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)

        topView.startAnimation(topAnimation)
        middleView.startAnimation(middleAnimation)
        bottomView.startAnimation(bottomAnimation)
    }

    private fun startWithDelay(splashScreenTimeout: Long = 4000, onComplete: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed({
            onComplete()
        }, splashScreenTimeout)
    }
}
