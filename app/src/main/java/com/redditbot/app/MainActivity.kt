package com.redditbot.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 80, 40, 40)
        }
        val title = TextView(this).apply { text = "Reddit Bot"; textSize = 28f; setPadding(0,0,0,20) }
        val status = TextView(this).apply { text = "Status: Idle"; textSize = 16f; setPadding(0,0,0,20) }
        val startBtn = Button(this).apply {
            text = "Start Bot Service"
            setOnClickListener {
                startForegroundService(Intent(context, BotService::class.java))
                status.text = "Status: Running"
            }
        }
        val accessBtn = Button(this).apply {
            text = "Enable Accessibility"
            setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        }
        val stopBtn = Button(this).apply {
            text = "Stop Bot"
            setOnClickListener {
                stopService(Intent(context, BotService::class.java))
                status.text = "Status: Stopped"
            }
        }
        layout.addView(title); layout.addView(status)
        layout.addView(startBtn); layout.addView(accessBtn); layout.addView(stopBtn)
        setContentView(layout)
    }
}
