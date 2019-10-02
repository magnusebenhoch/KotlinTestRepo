package com.example.kotlintestapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var currentCount: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById<TextView>(R.id.textView)

    }

    fun toastMe(view: View) {
        val toast = Toast.makeText(this, "This is the first Toast", Toast.LENGTH_SHORT)
        toast.show()
    }

    fun count(view: View) {
        currentCount = textView.text.toString()
        var count: Int = Integer.parseInt(currentCount)
        Log.i(count.toString(), "Count")
        count++


        textView.text = count.toString()
        Log.i("test", "test")
    }

    fun navigateToRandomScreen(view: View) {
        val randomIntent = Intent(this, RandomScreen::class.java)

        startActivity(randomIntent)
    }
}
