package khudiakovkirill.worklifebalance

import android.widget.SeekBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    var action = MutableLiveData<Action>()
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

    val eventTimeOver: LiveData<Boolean>
        get() = chronometer.eventTimeOver

    private val direction: LiveData<Chronometer.Direction>
        get() = chronometer.direction

    val directionString: LiveData<String> = Transformations.map(direction) {
        when(it) {
            Chronometer.Direction.DOWN -> "Time left"
            Chronometer.Direction.UP -> "Extra time"
            else -> ""
        }
    }

    val workButtonText: LiveData<String> = Transformations.map(action) {
        when(it) {
            Action.WORKING -> "Stop\nwork"
            else -> "Start\nwork"
        }
    }

    val restButtonText: LiveData<String> = Transformations.map(action) {
        when(it) {
            Action.RESTING -> "Stop\nrest"
            else -> "Start\nrest"
        }
    }

    private var lastInterval: Long = 0

    init {
        _workRatio.value = 5
        _restRatio.value = 5
        action.value = Action.NONE
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
        when (action.value) {
            Action.WORKING -> {
                lastAction = Action.WORKING
                action.value = Action.NONE
                stopChronometer()
            }
            Action.RESTING -> {
                stopChronometer()
                lastAction = Action.RESTING
                action.value = Action.WORKING
                startChronometer()
            }
            Action.NONE -> {
                action.value = Action.WORKING
                startChronometer()
            }
        }
    }

    fun onRestButtonClicked() {
        when (action.value) {
            Action.RESTING -> {
                lastAction = Action.RESTING
                action.value = Action.NONE
                stopChronometer()
            }
            Action.WORKING -> {
                stopChronometer()
                lastAction = Action.WORKING
                action.value = Action.RESTING
                startChronometer()
            }
            Action.NONE -> {
                action.value = Action.RESTING
                startChronometer()
            }
        }
    }

    fun onTimeOverEventFinished() {
        chronometer.eventTimeOver.value = false
    }

    private fun startChronometer() {
        chronometer.start(getInterval())
    }

    private fun stopChronometer() {
        chronometer.stop()
        lastInterval = chronometer.getLastInterval()
    }

    private fun getInterval(): Long {
        if (lastAction == Action.NONE || action.value == lastAction) {
            return 0
        }

        // We can use null assertion operator because we set values in init-block.
        var ratio = _workRatio.value!!.toDouble() / _restRatio.value!!.toDouble()
        if (action.value == Action.RESTING) ratio = 1.0 / ratio

        return (lastInterval * ratio).toLong()
    }

    override fun onCleared() {
        super.onCleared()
        chronometer.stop()
    }

    enum class Action { WORKING, RESTING, NONE }
}