package com.telematics.data.model.tracking

import java.util.*


interface DateFormatter {

    fun getFullNewDate(date: Date?): String
    fun getDateMonthDay(date: Date?): String
    fun parseDate(date: String): Date?
    fun getTime(date: Date?): String
    fun getDateYearTime(date: Date?): String

    fun parseFullNewDate(date:String):Date?
    fun getDateWithTime(date: Date?): String
}