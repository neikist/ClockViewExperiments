package dev.neikist.clockviewexperiments.views.renderers.contracts

interface HourClockHandRenderer :
    ClockHandRenderer {
    var hours: Int
    var minutes: Int
}