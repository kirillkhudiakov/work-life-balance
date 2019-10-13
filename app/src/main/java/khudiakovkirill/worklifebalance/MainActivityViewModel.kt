package khudiakovkirill.worklifebalance

import android.widget.SeekBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {

    private val _workRatio = MutableLiveData<Int>()
    val workRatio: LiveData<Int>
        get() = _workRatio

    private val _restRatio = MutableLiveData<Int>()
    val restRatio: LiveData<Int>
        get() = _restRatio

    init {
        _workRatio.value = 5
        _restRatio.value = 5
    }

    fun onSeekBarProgressChanged(seekBar: SeekBar?, progressValue: Int, fromUser: Boolean) {
        _workRatio.value = progressValue
        // We can confidently use !! operator because seekBar.max is defined in layout file.
        _restRatio.value = seekBar!!.max - progressValue
    }
}