package io.b3.quicktalk.internal

import android.os.Handler

interface TimerListener {
    fun timeout()
}

interface Timer {
    fun invalidate()
    fun timeout(delay: Double)
    fun setListener(listener: TimerListener)
}

class TimerImpl : Timer {

    private val handler = Handler()

    private val trigger = Runnable { this@TimerImpl.listener!!.timeout() }

    private var listener: TimerListener? = null

    override fun setListener(listener: TimerListener) {
        this.listener = listener
    }

    override fun invalidate() {
        handler.removeCallbacks(trigger)
    }

    override fun timeout(delay: Double) {
        handler.postDelayed(trigger, delay.toLong() * 1000)
    }
}