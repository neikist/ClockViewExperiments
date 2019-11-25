package dev.neikist.clockviewexperiments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import dev.neikist.clockviewexperiments.views.OnTimeChangeListener
import kotlinx.android.synthetic.main.activity_main.*
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {

    private var started = false
    @SuppressLint("SimpleDateFormat")
    private var numberFormat = NumberFormat.getIntegerInstance().also {
        it.minimumIntegerDigits = 2
    }
    private var minutes = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        start_btn.setOnClickListener(this::startBtnHandler)
        clock.onTimeChangeListener = object : OnTimeChangeListener {
            override fun onTimeChange(hour: Int, minute: Int) {
                showTimeText(hour, minute)
                minutes = hour * 60 + minute
            }

        }

    }

    private fun startBtnHandler(view: View) {
        started = !started
        if (started) {
            start_btn.text = getString(R.string.stop)
            class Ticker : Runnable {
                override fun run() {
                    minutes += 1
                    val hour = (minutes / 60)
                    clock.setTime(hour % 12, minutes - hour * 60)
                    showTimeText(clock.hour, clock.minute)
                    if (started) Handler().postDelayed(this, 16)

                }

            }

            Handler().post(Ticker())
        } else {
            start_btn.text = getString(R.string.start)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showTimeText(hour: Int, minute: Int) {
        time_tv.text = "${numberFormat.format(hour)}:${numberFormat.format(minute)}"
    }
}
