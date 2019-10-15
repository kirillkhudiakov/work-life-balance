package khudiakovkirill.worklifebalance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*
import kotlin.math.abs

class Chronometer {

    private lateinit var timer: Timer

    private var startTime: Long = 0
    private var lastInterval: Long = 0

    val time = MutableLiveData<Long>()

    fun start(interval: Long) {
        startTime = System.currentTimeMillis()
        timer = Timer()
        val task = ChronometerTask(interval)
        timer.scheduleAtFixedRate(task, 0, 1000)
    }

    fun stop() {
        timer.cancel()
        lastInterval = System.currentTimeMillis() - startTime
    }

    fun getLastInterval(): Long {
        return lastInterval
    }

    inner class ChronometerTask(private val interval: Long) : TimerTask() {
        override fun run() {
            val currentTime: Long = abs(interval - (System.currentTimeMillis() - startTime)) / 1000
            time.postValue(currentTime)
        }
    }
}