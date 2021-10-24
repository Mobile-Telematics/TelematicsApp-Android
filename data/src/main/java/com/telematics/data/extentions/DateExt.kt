package com.telematics.data.extentions

import android.util.Log
import com.telematics.data.extentions.DateFormatString.FORMAT_DATE_FROM_SERVER
import com.telematics.data.extentions.DateFormatString.FORMAT_DAY_MONTH_FULL_YEAR
import com.telematics.data.extentions.DateFormatString.FORMAT_DISPLAYABLE
import com.telematics.data.extentions.DateFormatString.FORMAT_FULL_MONTH_YEAR
import com.telematics.data.extentions.DateFormatString.FORMAT_ISO8601
import com.telematics.data.extentions.DateFormatString.FORMAT_ISO8601_JUST_SECONDS
import com.telematics.data.extentions.DateFormatString.FORMAT_MONTH_ABBREVIATION
import com.telematics.data.extentions.DateFormatString.FORMAT_TIME
import com.telematics.data.extentions.DateFormatString.FORMAT_TIME_FULL_RAXEL_SDK_TRACKS
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object DateFormatString {
    internal const val FORMAT_FOR_FILE_NAME = "yyyyMMdd_HHmmss"
    internal const val FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    internal const val FORMAT_ISO8601_JUST_SECONDS = "yyyy-MM-dd'T'HH:mm:ssZ"
    internal const val FORMAT_ISO8601_JUST_SECONDS_Z = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    internal const val FORMAT_ISO8601_JUST_SECONDS_DECIMAL = "yyyy-MM-dd'T'HH:mm:ss.sssZ"
    internal const val FORMAT_ISO_ZONED_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZ"
    internal const val FORMAT_ISO8601_ZONED_DATE_TIME =
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ" // 2018-10-15T11:09:01.577513+11:00
    internal const val FORMAT_MONTH_ABBREVIATION = "dd MMM yyyy"
    internal const val FORMAT_DAY_MONTH_FULL_YEAR = "dd MMMM yyyy"
    internal const val FORMAT_DISPLAYABLE = "dd/MM/yyyy"
    internal const val FORMAT_DISPLAYABLE_MONTH = "MMM/yyyy"
    internal const val FORMAT_DISPLAYABLE_MONTH_AM = "dd MMM yyyy, HH:mma"
    internal const val FORMAT_DISPLAYABLE_TAG_INFO = "dd/MM/yyyy HH:mma"
    internal const val FORMAT_TIME = "hh:mma"
    internal const val FORMAT_DATE_FROM_SERVER = "yyyy-MM-dd"
    internal const val FORMAT_FULL_MONTH_YEAR = "MMMM yyyy"
    internal const val FORMAT_COMPLETE_TIME_FULL_MONTH = "dd MMMM yyyy, HH:mm:ss"
    internal const val FORMAT_TIME_FULL_RAXEL_SDK_TRACKS = "yyyy-MM-dd'T'HH:mm:ssZZ"

}

sealed class DateFormat(val format: String) {

    class Iso8601DateTime : DateFormat(FORMAT_ISO8601)
    class MonthAbbreviationFormat : DateFormat(FORMAT_MONTH_ABBREVIATION)
    class DateDisplayFormat : DateFormat(FORMAT_DISPLAYABLE)
    class TimeFormat : DateFormat(FORMAT_TIME)
    class ServerFormat : DateFormat(FORMAT_DATE_FROM_SERVER)
    class DayMonthFullYear : DateFormat(FORMAT_DAY_MONTH_FULL_YEAR)
}

fun Date.toStringWithFormat(stringFormat: String): String {
    val format = SimpleDateFormat(stringFormat, Locale.getDefault())
    if (stringFormat == DateFormat.Iso8601DateTime().format) {
        format.timeZone = TimeZone.getTimeZone("UTC")
    }
    return try {
        format.format(this)
    } catch (e: ParseException) {
        Log.e("date parse error ", "Exception during parsing date to string", e)
        ""
    }
}

fun Date.toStringWithFormatLocalTimeZone(stringFormat: String): String {
    val format = SimpleDateFormat(stringFormat, Locale.getDefault())
    format.timeZone = TimeZone.getDefault()
    return try {
        format.format(this)
    } catch (e: ParseException) {
        Log.e("date parse error ", "Exception during parsing date to string", e)
        ""
    }
}

fun Date.durationFromNow(): Date {
    val durationLong = System.currentTimeMillis() - this.time
    return Date(durationLong)
}

fun Date.toFullMonthAndYearOnly() = toStringWithFormat(FORMAT_FULL_MONTH_YEAR)

fun Date.toDayOrdinalAndMonthAndYear(): String {
    val cal = Calendar.getInstance()
    cal.time = this

    val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
    val dayOfMonthOrdinal = when {
        dayOfMonth in 11..13 -> "th"
        dayOfMonth % 10 == 1 -> "st"
        dayOfMonth % 10 == 2 -> "nd"
        dayOfMonth % 10 == 3 -> "rd"
        else -> "th"
    }
    val monthDisplayName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
    val year = cal.get(Calendar.YEAR)

    return "$dayOfMonth$dayOfMonthOrdinal $monthDisplayName $year"
}

fun String.toDateWithFormat(stringFormat: String, locale: Locale? = Locale.getDefault()): Date {
    val format = SimpleDateFormat(stringFormat, locale)
    return format.parse(this)
}

fun String.toDateWithFormatUtc(stringFormat: String): Date {
    val format = SimpleDateFormat(stringFormat, Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone("UTC")
    return format.parse(this)
}

fun String.toStringDateSafe(dateInStringFormat: String, dateOutStringFormat: String): String {
    return try {
        this.toDateWithFormat(dateInStringFormat)
            .toStringWithFormat(dateOutStringFormat)
    } catch (e: Exception) {
        this
    }
}

fun String.toCalendarWithFormat(stringFormat: String): Calendar {
    val date = toDateWithFormat(stringFormat).time
    return Calendar.getInstance().apply { timeInMillis = date }
}

fun String.iso8601InSecondsToLong(): Long {
    var result: Long? = 0L
    val format = SimpleDateFormat(FORMAT_ISO8601_JUST_SECONDS, Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone("GMT")
    result = format.parse(this).time
    return result
}


fun Long.timeMillsToIso8601InSeconds(): String {
    return SimpleDateFormat(
        FORMAT_TIME_FULL_RAXEL_SDK_TRACKS,
        Locale.getDefault()
    ).format(Date(this))
}

fun Date.plusYears(years: Int = 0): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar.add(Calendar.YEAR, years)
    return calendar.time
}

fun Triple<Int, Int, Int>.toTimeInMillis(): Long {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.YEAR, first)
    calendar.set(Calendar.MONTH, second)
    calendar.set(Calendar.DAY_OF_MONTH, third)
    return calendar.timeInMillis
}

fun Pair<Long?, Long?>.timeMillsToDurationString(dateFormat: DateFormat = DateFormat.TimeFormat()): String {
    if (first == null || second == null) {
        return ""
    }
    val start = SimpleDateFormat(dateFormat.format, Locale.getDefault()).format(Date(this.first!!))
    val end = SimpleDateFormat(dateFormat.format, Locale.getDefault()).format(Date(second!!))
    return String.format("%s - %s", start, end)
}

fun Long.timeMillsToDisplayableString(dateFormat: DateFormat = DateFormat.DateDisplayFormat()): String {
    return SimpleDateFormat(dateFormat.format, Locale.getDefault()).format(Date(this))
}

fun Long.timeMillsToIso8601(): String {
    return SimpleDateFormat(DateFormat.Iso8601DateTime().format, Locale.getDefault()).format(
        Date(
            this
        )
    )
}

fun String.iso8601TimeToLong(): Long? {
    var result: Long? = null
    if (!this.isEmpty()) {
        val simpleDateFormat =
            SimpleDateFormat(DateFormat.Iso8601DateTime().format, Locale.getDefault())
        simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT")
        try {
            result = simpleDateFormat.parse(this).time
        } catch (e: ParseException) {
            error { "Error during parsing a date : " + e.message }
        }
    }
    return result
}

fun String.stringDateToTimeInMillis(dateFormat: DateFormat = DateFormat.ServerFormat()): Long? {
    val result: Long?
    try {
        val simpleDateFormat = SimpleDateFormat(dateFormat.format, Locale.getDefault())
        result = simpleDateFormat.parse(this).time
    } catch (e: ParseException) {
        return null
    }
    return result
}

fun Long.stringWithFormat(format: String) =
    Date(this).toStringWithFormat(format)

fun Long?.toDateLongWithIso8601DateTimeFormat() =
    toStringWithFormat(this, DateFormat.Iso8601DateTime())

fun Long?.toStringWithDisplayFormat(dateFormat: DateFormat = DateFormat.DateDisplayFormat()) =
    toStringWithFormat(this, dateFormat)

fun Long?.toStringWithTimeFormat() =
    toStringWithFormat(this, DateFormat.TimeFormat())


private fun toStringWithFormat(value: Long?, dateFormat: DateFormat) =
    if (value != null) Date(value).toStringWithFormat(dateFormat.format) else ""

fun Date?.toStringWithFormat(dateFormat: DateFormat) = this?.toStringWithFormat(dateFormat.format)
    ?: ""

fun Date.toCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar
}

fun Calendar.getAge(): String {
    val today = Calendar.getInstance()
    var age = today.get(Calendar.YEAR) - this.get(Calendar.YEAR)

    if (today.get(Calendar.DAY_OF_YEAR) < this.get(Calendar.DAY_OF_YEAR)) {
        age--
    }
    val ageInt = age
    return ageInt.toString()
}