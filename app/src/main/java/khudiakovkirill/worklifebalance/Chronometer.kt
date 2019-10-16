package khudiakovkirill.worklifebalance

import androidx.lifecycle.MutableLiveData
import java.util.*
import kotlin.math.abs

/**
 * This class represents chronometer that can estimate time intervals
 */
class Chronometer {

    private var timer = Timer()

    private var startTime: Long = 0
    private var lastInterval: Long = 0

    val time = MutableLiveData<Long>()
    val eventTimeOver = MutableLiveData<Boolean>()

    var direction = MutableLiveData<Direction>()

    init {
        direction.value = Direction.UNDEFINED
        eventTimeOver.value = false
    }

    /**
     * Starts new time measurement. The method will update time field every second.
     * @param interval Duration of countdown. After that chronometer will continue to work anyway.
     */
    fun start(interval: Long = 0) {
        direction.value = if (interval > 0) Direction.DOWN else Direction.UP
        startTime = System.currentTimeMillis()
        timer = Timer()
        val task = ChronometerTask(interval)
        timer.scheduleAtFixedRate(task, 0, 1000)
    }

    /**
     * Stops current time measurement.
     */
    fun stop() {
        direction.value = Direction.UNDEFINED
        timer.cancel()
        lastInterval = System.currentTimeMillis() - startTime
    }

    /**
     * Return last duration of chronometer measurement.
     * @return last interval in milliseconds.
     */
    fun getLastInterval(): Long {
        return lastInterval
    }

    private inner class ChronometerTask(private val interval: Long) : TimerTask() {
        override fun run() {
            val elapsedTime = System.currentTimeMillis() - startTime
            if (direction.value == Direction.DOWN && elapsedTime >= interval) {
                direction.postValue(Direction.UP)
                eventTimeOver.postValue(true)
            }

            val currentTime: Long = abs(elapsedTime - interval) / 1000
            time.postValue(currentTime)
        }
    }

    enum class Direction { UP, DOWN, UNDEFINED }
}