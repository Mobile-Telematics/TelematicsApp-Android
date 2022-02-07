package com.telematics.domain.model.on_demand

enum class OnDemandState {
    ONLINE, OFFLINE, ON_DUTY;

    companion object {
        fun translation(s: String?): OnDemandState {
            return when (s) {
                "online" -> ONLINE
                "offline" -> OFFLINE
                "on_duty" -> ON_DUTY
                else -> OFFLINE
            }
        }
    }

    override fun toString(): String {
        return when (this) {
            ONLINE -> "online"
            OFFLINE -> "offline"
            ON_DUTY -> "on_duty"
        }
    }
}