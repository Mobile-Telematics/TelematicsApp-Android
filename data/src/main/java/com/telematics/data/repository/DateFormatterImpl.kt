package com.telematics.data.repository

import com.telematics.data.model.tracking.DateFormatter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateFormatterImpl : DateFormatter {

    private val fullNewDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    private val dateMonthDay = SimpleDateFormat("MMM d", Locale.ENGLISH)
    private val fullDate =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH)
    private val hh_mm = SimpleDateFormat("HH:mm", Locale.ENGLISH)
    private val MMMM_d_yyyy = SimpleDateFormat("MMMM d yyyy", Locale.ENGLISH)


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

        return hh_mm.format(date)
    }

    override fun getDateYearTime(date: Date?): String {
        if (date == null) return ""
        val d = MMMM_d_yyyy.format(date)
        val t = hh_mm.format(date)
        return "$d, $t"
    }
}