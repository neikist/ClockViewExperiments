package dev.neikist.clockviewexperiments

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        class Ticker : Runnable {
            var init = 0
            override fun run() {
                init += 2
                clock.hour = (init / 60) % 12
                clock.minutes = init - clock.hour * 60

                Handler().postDelayed(this, 16)
            }

        }

        Handler().postDelayed(Ticker(), 500)


    }
}
