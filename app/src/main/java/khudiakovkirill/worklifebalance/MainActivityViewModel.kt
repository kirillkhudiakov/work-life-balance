package khudiakovkirill.worklifebalance

import android.widget.SeekBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

enum class Action { WORKING, RESTING, NONE }

class MainActivityViewModel : ViewModel() {

    private var action = Action.NONE
    private var lastAction = Action.NONE

    private val _workRatio = MutableLiveData<Int>()
    val workRatio: LiveData<Int>
        get() = _workRatio

    private val _restRatio = MutableLiveData<Int>()
    val restRatio: LiveData<Int>
        get() = _restRatio

    private val chronometer = Chronometer()

    val time: LiveData<Long>
        get() = chronometer.time

    private val direction: LiveData<Chronometer.Direction>
        get() = chronometer.direction

    val directionString: LiveData<String> = Transformations.map(direction) {
        when(it) {
            Chronometer.Direction.DOWN -> "Time left"
            Chronometer.Direction.UP -> "Extra time"
            else -> ""
        }
    }

    private var lastInterval: Long = 0

    init {
        _workRatio.value = 5
        _restRatio.value = 5
    }

    fun onSeekBarProgressChanged(seekBar: SeekBar, progressValue: Int) {
        when(progressValue) {
            0 -> {
                seekBar.progress = 1
            }
            10 -> {
                seekBar.progress = 9
            }
            else -> {
                _workRatio.value = progressValue
                _restRatio.value = seekBar.max - progressValue
            }
        }
    }

    fun onWorkButtonClicked() {
        when (action) {
            Action.WORKING -> {
                lastAction = Action.WORKING
                action = Action.NONE
                stopChronometer()
            }
            Action.RESTING -> {
                stopChronometer()
                lastAction = Action.RESTING
                action = Action.WORKING
                startChronometer()
            }
            Action.NONE -> {
                action = Action.WORKING
                startChronometer()
            }
        }
    }

    fun onRestButtonClicked() {
        when (action) {
            Action.RESTING -> {
                lastAction = Action.RESTING
                action = Action.NONE
                stopChronometer()
            }
            Action.WORKING -> {
                stopChronometer()
                lastAction = Action.WORKING
                action = Action.RESTING
                startChronometer()
            }
            Action.NONE -> {
                action = Action.RESTING
                startChronometer()
            }
        }
    }

    private fun startChronometer() {
        chronometer.start(getInterval())
    }

    private fun stopChronometer() {
        chronometer.stop()
        lastInterval = chronometer.getLastInterval()
    }

    private fun getInterval(): Long {
        if (lastAction == Action.NONE || action == lastAction) {
            return 0
        }

        // We can use null assertion operator because we set values in init-block.
        var ratio = _workRatio.value!!.toDouble() / _restRatio.value!!.toDouble()
        if (action == Action.RESTING) ratio = 1.0 / ratio

        return (lastInterval * ratio).toLong()
    }

    override fun onCleared() {
        super.onCleared()
        chronometer.stop()
    }
}