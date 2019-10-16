package khudiakovkirill.worklifebalance

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import khudiakovkirill.worklifebalance.databinding.ActivityMainBinding

private const val CHANNEL_ID = "chronometer_channel"
private const val CHANNEL_NAME = "Chronometer channel"
private const val CHANNEL_DESCRIPTION = "Chronometer channel description"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()

        val viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) =
                viewModel.onSeekBarProgressChanged(seekBar, progress)
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        viewModel.eventTimeOver.observe(this, Observer { timeOver ->
            if (timeOver) {
                createNotification(viewModel.action.value!!)
                viewModel.onTimeOverEventFinished()
            }
        })
    }

    private fun createNotification(currentAction: MainActivityViewModel.Action) {
        val intent = Intent(this, MainActivity::class.java).apply {
            action = "android.intent.action.MAIN"
            addCategory("android.intent.category.LAUNCHER")
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val text = getString(R.string.time_to) + " " +
                if (currentAction == MainActivityViewModel.Action.WORKING)
                    getString(R.string.rest)
                else
                    getString(R.string.work)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_android_black)
            .setContentTitle(getString(R.string.switch_activity))
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(this)) {
            notify(0, notification)
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = getString(R.string.channel_name)
//            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("MainActivity", "I was destroyed(")
    }
}

