package khudiakovkirill.worklifebalance

import android.widget.SeekBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

enum class Status { WORKING, RESTING, UNDEFINED }

class MainActivityViewModel : ViewModel() {

    private var status = Status.UNDEFINED
    private var lastStatus = Status.UNDEFINED

    private val _workRatio = MutableLiveData<Int>()
    val workRatio: LiveData<Int>
        get() = _workRatio

    private val _restRatio = MutableLiveData<Int>()
    val restRatio: LiveData<Int>
        get() = _restRatio

    private val chronometer = Chronometer()

    val time: LiveData<Long>
        get() = chronometer.time

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
        when (status) {
            Status.WORKING -> {
                lastStatus = Status.WORKING
                status = Status.UNDEFINED
                stopChronometer()
            }
            Status.RESTING -> {
                stopChronometer()
                lastStatus = Status.RESTING
                status = Status.WORKING
                startChronometer()
            }
            Status.UNDEFINED -> {
                status = Status.WORKING
                startChronometer()
            }
        }
    }

    fun onRestButtonClicked() {
        when (status) {
            Status.RESTING -> {
                lastStatus = Status.RESTING
                status = Status.UNDEFINED
                stopChronometer()
            }
            Status.WORKING -> {
                stopChronometer()
                lastStatus = Status.WORKING
                status = Status.RESTING
                startChronometer()
            }
            Status.UNDEFINED -> {
                status = Status.RESTING
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
        if (lastStatus == Status.UNDEFINED || status == lastStatus) {
            return 0
        }

        // We can use null assertion operator because we set values in init-block.
        var ratio = _workRatio.value!!.toDouble() / _restRatio.value!!.toDouble()
        if (status == Status.RESTING) ratio = 1.0 / ratio

        return (lastInterval * ratio).toLong()
    }

    override fun onCleared() {
        super.onCleared()
        chronometer.stop()
    }
}