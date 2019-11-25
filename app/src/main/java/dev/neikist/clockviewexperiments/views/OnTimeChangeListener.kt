package dev.neikist.clockviewexperiments.views

@FunctionalInterface
interface OnTimeChangeListener {
    fun onTimeChange(hour: Int, minute: Int)
}