package com.google.firebase.quickstart.fcm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.quickstart.fcm.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.default_notification_channel_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW))
        }


        val filter = IntentFilter()
        filter.addAction("com.example.Broadcast")
        val receiver = object:  BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                addViewDebugLayout(binding, intent.getStringExtra("push"))
            }
        }
        registerReceiver(receiver, filter)

        // If a notification message is tapped, any data accompanying the notification
        // message is available in the intent extras. In this sample the launcher
        // intent is fired when the notification is tapped, so any accompanying data would
        // be handled here. If you want a different intent fired, set the click_action
        // field of the notification message to the desired intent. The launcher intent
        // is used when no click_action is specified.
        //
        // Handle possible data accompanying notification message.
        // [START handle_data_extras]
        intent.extras?.let {
            for (key in it.keySet()) {
                val value = intent.extras?.get(key)
                Log.d(TAG, "Key: $key Value: $value")
            }
        }
        // [END handle_data_extras]

        binding.subscribeButton.setOnClickListener {
            Log.d(TAG, "Subscribing to wikipedia topic")
            // [START subscribe_topics]
            FirebaseMessaging.getInstance().subscribeToTopic("wikipedia")
                    .addOnCompleteListener { task ->
                        var msg = getString(R.string.msg_subscribed)
                        if (!task.isSuccessful) {
                            msg = getString(R.string.msg_subscribe_failed)
                        }
                        Log.d(TAG, msg)
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        addViewDebugLayout(binding, msg)
                    }
            // [END subscribe_topics]
        }
        binding.logTokenButton.setOnClickListener {
            // Get token
            // [START retrieve_current_token]
            FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w(TAG, "getInstanceId failed", task.exception)
                            return@OnCompleteListener
                        }

                        // Get new Instance ID token
                        val token = task.result?.token

                        // Log and toast
                        val msg = getString(R.string.msg_token_fmt, token)
                        Log.d(TAG, msg)
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        addViewDebugLayout(binding, msg)
                    })
            // [END retrieve_current_token]
        }

        Toast.makeText(this, "See README for setup instructions", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("SetTextI18n")
    private fun addViewDebugLayout(binding: ActivityMainBinding, msg: String) {
        // Append log msg into the debug layout
        val debugLayout = binding.debug
        val debugText = TextView(this)
        val currentTime: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

        debugText.text = Html.fromHtml("<b>$currentTime</b> $msg");
        debugText.setTextIsSelectable(true);
        debugText.setPadding(0, 15, 0, 15);
        debugLayout.addView(debugText)
        binding.scroll.post { binding.scroll.fullScroll(View.FOCUS_DOWN) }
    }

    companion object {

        private const val TAG = "MainActivity"
    }
}
