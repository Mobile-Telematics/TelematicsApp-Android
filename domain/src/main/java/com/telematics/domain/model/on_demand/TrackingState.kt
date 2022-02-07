package com.telematics.domain.model.on_demand

enum class TrackingState(val index: Int) {
    AUTO(0), DEMAND(1), DISABLE(2);

    companion object {
        fun parse(index: Int?): TrackingState {
            return when (index) {
                0 -> AUTO
                1 -> DEMAND
                2 -> DISABLE
                else -> AUTO
            }
        }
    }
}