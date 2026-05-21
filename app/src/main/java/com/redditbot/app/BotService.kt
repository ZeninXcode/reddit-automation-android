package com.redditbot.app

import android.app.*
import android.content.Intent
import android.os.*
import androidx.core.app.NotificationCompat
import org.json.JSONObject

class BotService : Service() {
    private lateinit var wsClient: WebSocketClient
    private val CHANNEL_ID = "BotServiceChannel"
    private val SERVER_URL = "wss://real-phone-reddit-automation-server-production.up.railway.app"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, buildNotification("Reddit Bot running..."))
        val phoneId = "phone_" + android.provider.Settings.Secure.getString(
            contentResolver, android.provider.Settings.Secure.ANDROID_ID)
        wsClient = WebSocketClient(SERVER_URL, phoneId) { command -> handleCommand(command) }
        wsClient.connect()
    }

    private fun handleCommand(command: JSONObject) {
        val intent = Intent("com.redditbot.COMMAND")
        intent.putExtra("command", command.optString("command"))
        intent.putExtra("params", command.optString("params"))
        sendBroadcast(intent)
    }

    private fun buildNotification(text: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Reddit Bot").setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW).build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Bot Service", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onDestroy() { super.onDestroy(); wsClient.disconnect() }
}
