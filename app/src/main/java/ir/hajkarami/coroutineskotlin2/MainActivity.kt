package ir.hajkarami.coroutineskotlin2

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class MainActivity : AppCompatActivity() {
    private lateinit var textView1 : TextView
    private lateinit var textView2 : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        textView1 = findViewById(R.id.txt1)
        textView2 = findViewById(R.id.txt2)

        CoroutineScope(Dispatchers.IO).launch{
            textView2.text = "Hello We Use Coroutines in ${Thread.currentThread().name}"
        }
        CoroutineScope(Dispatchers.Main).launch{
            textView1.text = "Hello We Use Coroutines in ${Thread.currentThread().name}"
        }
    }
}