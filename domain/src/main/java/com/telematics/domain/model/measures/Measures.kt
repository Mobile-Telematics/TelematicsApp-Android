package com.telematics.domain.model.measures

const val MILES = "mi"
const val KILOMETERS = "km"

enum class DistanceMeasure(val value: String) {
    KM(KILOMETERS),
    MI(MILES);

    companion object {
        fun parse(value: String?): DistanceMeasure {
            return when (value) {
				MILES -> MI
                else -> KM
            }
        }

        val default = KM
    }
}

const val MONTH_DAY = "mm/dd"
const val DAY_MONTH = "dd/mm"

enum class DateMeasure(val value: String) {
    DD_MM(DAY_MONTH),
    MM_DD(MONTH_DAY);

    companion object {
        fun parse(value: String?): DateMeasure {
            return when (value) {
				MONTH_DAY -> MM_DD
                else -> DD_MM
            }
        }

        val default = DD_MM
    }
}

const val FORMAT_12 = "12h"
const val FORMAT_24 = "24h"

enum class TimeMeasure(val value: String) {
    H24(FORMAT_24),
    H12(FORMAT_12);

    companion object {
        fun parse(value: String?): TimeMeasure {
            return when (value) {
				FORMAT_12 -> H12
                else -> H24
            }
        }

        val default = H24
    }
}