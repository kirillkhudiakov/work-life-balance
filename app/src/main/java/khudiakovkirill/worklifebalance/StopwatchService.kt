package khudiakovkirill.worklifebalance

import android.app.IntentService
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import java.util.*

class StopwatchService : Service() {

    private val binder: IBinder = StopwatchBinder()

    val time = MutableLiveData<Long>()

    private val timer = Timer()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                time.postValue(System.currentTimeMillis())
            }
        }, 0, 5000)
    }

    inner class StopwatchBinder : Binder() {
        fun getService(): StopwatchService {
            return this@StopwatchService
        }
    }
}