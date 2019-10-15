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

    val time: MutableLiveData<Long> = MutableLiveData<Long>()

    var timer = Timer()

    var interval: Long = 0
    var startTime: Long = 0

    init {
        _workRatio.value = 5
        _restRatio.value = 5
//        timer.scheduleAtFixedRate(object : TimerTask() {
//            override fun run() {
//                time.postValue(System.currentTimeMillis())
//            }
//        }, 0, 1000)
    }

    fun onSeekBarProgressChanged(seekBar: SeekBar, progressValue: Int) {
        _workRatio.value = progressValue
        _restRatio.value = seekBar.max - progressValue
    }

    fun onWorkButtonClicked() {
        when (status) {
            Status.WORKING -> {
                status = Status.UNDEFINED
                stopWorking()
            }
            else -> {
                status = Status.WORKING
                startWorking()
            }
        }
    }

    private fun startWorking() {
        startTime = System.currentTimeMillis()
        timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val currentTime: Long = abs(interval - (System.currentTimeMillis() - startTime)) / 1000
                time.postValue(currentTime)
            }
        }, 0, 1000)
    }

    private fun stopWorking() {
        timer.cancel()
        interval = System.currentTimeMillis() - startTime
    }

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }
}