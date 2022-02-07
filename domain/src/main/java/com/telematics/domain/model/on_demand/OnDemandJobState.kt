package com.telematics.domain.model.on_demand


enum class OnDemandJobState {
    CURRENT, ACCEPTED, COMPLETE, PAUSED;

    override fun toString(): String {
        return this.name
    }
}