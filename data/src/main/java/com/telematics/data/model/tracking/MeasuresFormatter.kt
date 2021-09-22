package com.telematics.data.model.tracking

import com.telematics.domain.model.measures.DistanceMeasure
import java.util.*


interface MeasuresFormatter {

    fun getFullNewDate(date: Date?): String
    fun getDateMonthDay(date: Date?): String
    fun parseDate(date: String): Date?
    fun getTime(date: Date?): String
    fun getDateYearTime(date: Date?): String

    fun parseFullNewDate(date: String): Date?
    fun getDateWithTime(date: Date?): String

    fun getDistanceByKm(km: Double): Double
    fun getDistanceByKm(km: Float): Double
    fun getDistanceByKm(km: Int): Double
    fun getDistanceMeasureValue(): DistanceMeasure
}