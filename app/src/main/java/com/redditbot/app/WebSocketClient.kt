package com.redditbot.app

import okhttp3.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class WebSocketClient(
    private val serverUrl: String,
    private val phoneId: String,
    private val onCommand: (JSONObject) -> Unit
) {
    private val client = OkHttpClient.Builder().pingInterval(30, TimeUnit.SECONDS).build()
    private var webSocket: WebSocket? = null

    fun connect() {
        val request = Request.Builder().url(serverUrl).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                ws.send(JSONObject().apply {
                    put("type", "register_phone"); put("phoneId", phoneId)
                }.toString())
            }
            override fun onMessage(ws: WebSocket, text: String) {
                try {
                    val msg = JSONObject(text)
                    if (msg.getString("type") == "command") onCommand(msg)
                } catch (e: Exception) { e.printStackTrace() }
            }
            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({ connect() }, 5000)
            }
            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({ connect() }, 5000)
            }
        })
    }

    fun send(data: JSONObject) { webSocket?.send(data.toString()) }
    fun disconnect() { webSocket?.close(1000, "Disconnected") }
}
