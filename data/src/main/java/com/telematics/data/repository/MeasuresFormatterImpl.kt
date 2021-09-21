package com.telematics.data.repository

import android.util.Log
import com.telematics.data.model.tracking.MeasuresFormatter
import com.telematics.domain.model.measures.DateMeasure
import com.telematics.domain.model.measures.DistanceMeasure
import com.telematics.domain.model.measures.TimeMeasure
import com.telematics.domain.repository.SettingsRepo
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class MeasuresFormatterImpl @Inject constructor(
    private val settingsRepo: SettingsRepo
) : MeasuresFormatter {

    private val dateMeasure: DateMeasure
        get() {
            return settingsRepo.getDateMeasure()
        }

    private val timeMeasure: TimeMeasure
        get() {
            return settingsRepo.getTimeMeasure()
        }

    private val distanceMeasure: DistanceMeasure
        get() {
            return settingsRepo.getDistanceMeasure()
        }

    private val dateMonthDay = SimpleDateFormat("MMM d", Locale.ENGLISH)

    private val fullNewDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    private val fullDate =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH)

    private val hh_mm = SimpleDateFormat("HH:mm", Locale.ENGLISH)
    private val hh_mm_a = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

    private val MMMM_d_yyyy = SimpleDateFormat("MMMM d yyyy", Locale.ENGLISH)
    private val d_MMMM_yyyy = SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH)

    private val dd_MM = SimpleDateFormat("dd.MM", Locale.ENGLISH)
    private val MM_dd = SimpleDateFormat("MM.dd", Locale.ENGLISH)


    override fun getFullNewDate(date: Date?): String {
        return if (date == null) {
            ""
        } else fullNewDate.format(date)
    }

    override fun getDateMonthDay(date: Date?): String {
        return if (date == null) {
            ""
        } else dateMonthDay.format(date)
    }

    override fun parseDate(date: String): Date? {
        return try {
            fullDate.parse(date)
        } catch (e: ParseException) {
            null
        }
    }

    override fun getTime(date: Date?): String {
        if (date == null) return ""

        return when (timeMeasure) {
            TimeMeasure.H24 -> hh_mm.format(date)
            TimeMeasure.H12 -> hh_mm_a.format(date)
        }
    }

    override fun getDateYearTime(date: Date?): String {
        if (date == null) return ""
        val d = when (dateMeasure) {
            DateMeasure.DD_MM -> d_MMMM_yyyy.format(date)
            DateMeasure.MM_DD -> MMMM_d_yyyy.format(date)
        }
        val t = when (timeMeasure) {
            TimeMeasure.H24 -> hh_mm.format(date)
            TimeMeasure.H12 -> hh_mm_a.format(date)
        }
        return "$d, $t"
    }

    override fun parseFullNewDate(date: String): Date? {
        return try {
            fullNewDate.parse(date)
        } catch (e: ParseException) {
            Log.d("TAG", "ParseException " + e.message)
            null
        }
    }

    override fun getDateWithTime(date: Date?): String {

        if (date == null) return ""

        val d = when (dateMeasure) {
            DateMeasure.DD_MM -> dd_MM.format(date)
            DateMeasure.MM_DD -> MM_dd.format(date)
        }

        val t = when (timeMeasure) {
            TimeMeasure.H24 -> hh_mm.format(date)
            TimeMeasure.H12 -> hh_mm_a.format(date)
        }

        return "$d, $t"
    }

    override fun getDistanceByKm(km: Int): Double {
        return getDistanceByKm(km.toDouble())
    }

    override fun getDistanceByKm(km: Float): Double {
        return getDistanceByKm(km.toDouble())
    }

    override fun getDistanceByKm(km: Double): Double {

        val k = when (distanceMeasure) {
            DistanceMeasure.KM -> 1.0
            DistanceMeasure.MI -> 0.621371
        }
        return km * k
    }

    override fun getDistanceMeasureValue(): DistanceMeasure {
        return distanceMeasure
    }
}