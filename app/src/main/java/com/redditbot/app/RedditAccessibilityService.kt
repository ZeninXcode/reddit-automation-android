package com.redditbot.app

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class RedditAccessibilityService : AccessibilityService() {
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra("command")) {
                "upvote" -> findAndClick("com.reddit.frontpage:id/upvote_button")
                "comment" -> findAndClick("com.reddit.frontpage:id/comment_button")
                "scroll" -> performGlobalAction(GLOBAL_ACTION_ACCESSIBILITY_BUTTON)
            }
        }
    }

    private fun findAndClick(viewId: String) {
        rootInActiveWindow?.findAccessibilityNodeInfosByViewId(viewId)
            ?.firstOrNull()?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        registerReceiver(receiver, IntentFilter("com.redditbot.COMMAND"))
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}
    override fun onDestroy() { super.onDestroy(); unregisterReceiver(receiver) }
}
