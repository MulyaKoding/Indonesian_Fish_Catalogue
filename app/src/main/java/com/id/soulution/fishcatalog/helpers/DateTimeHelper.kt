package com.id.soulution.fishcatalog.helpers

import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class DateTimeHelper {
    fun currentTime(): String {
        val id = Locale("in", "ID")
        val pattern = "EEEE, dd MMM yyyy"
        val today = Date()

        val dfs = DateFormatSymbols(id)

        val days: Array<String> = dfs.weekdays
        val newDays = arrayOfNulls<String>(days.size)
        for (i in days.indices) {
            newDays[i] = days[i]
        }

        // Set String array of weekdays.

        // Set String array of weekdays.
        dfs.weekdays = newDays

        // Gets String array of default format of short months.

        // Gets String array of default format of short months.
        val shortMonths: Array<String> = dfs.shortMonths
        val months = arrayOfNulls<String>(shortMonths.size)
        for (j in shortMonths.indices) {
            months[j] = shortMonths[j]
        }
        dfs.shortMonths = months
        val sdf = SimpleDateFormat(pattern, dfs)
        return sdf.format(today)
    }
}