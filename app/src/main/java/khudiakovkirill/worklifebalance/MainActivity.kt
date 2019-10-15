package khudiakovkirill.worklifebalance

import android.content.ComponentName
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import khudiakovkirill.worklifebalance.databinding.ActivityMainBinding
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

//        val intent = Intent(this, StopwatchService::class.java)
//        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

//    private val connection = object : ServiceConnection {
//        override fun onServiceDisconnected(name: ComponentName?) {
//
//        }
//
//        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//            val binder = service as StopwatchService.StopwatchBinder
//            val s = binder.getService()
//            binding.timeText.text = s.time.value.toString()
//        }
//
//    }
}

