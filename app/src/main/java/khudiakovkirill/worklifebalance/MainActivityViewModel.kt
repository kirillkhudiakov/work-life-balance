package khudiakovkirill.worklifebalance

import android.widget.SeekBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import kotlin.math.abs

enum class Status { WORKING, RESTING, UNDEFINED }

class MainActivityViewModel : ViewModel() {

    var status: Status = Status.UNDEFINED

    private val _workRatio = MutableLiveData<Int>()
    val workRatio: LiveData<Int>
        get() = _workRatio

    private val _restRatio = MutableLiveData<Int>()
    val restRatio: LiveData<Int>
        get() = _restRatio

    val chronometer = Chronometer()

    val time: LiveData<Long>
        get() = chronometer.time

    var interval: Long = 0
    var startTime: Long = 0

    init {
        _workRatio.value = 5
        _restRatio.value = 5
    }

    fun onSeekBarProgressChanged(seekBar: SeekBar, progressValue: Int) {
        _workRatio.value = progressValue
        _restRatio.value = seekBar.max - progressValue
    }

    fun onWorkButtonClicked() {
        when (status) {
            Status.WORKING -> {
                status = Status.UNDEFINED
                stopChronometer()
            }
            else -> {
                status = Status.WORKING
                startChronometer()
            }
        }
    }

    private fun startChronometer() {
        chronometer.start(interval)
    }

    private fun stopChronometer() {
        chronometer.stop()
        interval = chronometer.getLastInterval()
    }

    override fun onCleared() {
        super.onCleared()
        chronometer.stop()
    }
}