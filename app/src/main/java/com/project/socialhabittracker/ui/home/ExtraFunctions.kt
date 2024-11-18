package com.project.socialhabittracker.ui.home

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun lastFiveDates(): List<String> {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val cal = Calendar.getInstance()
    val lastFiveDates = mutableListOf<String>()

    for (i in 1..5) {
        val date = sdf.format(cal.time)
        lastFiveDates.add(date)
        cal.add(Calendar.DATE, -1)
    }

    return lastFiveDates
}

fun convertToMillis(dateString: String, dateFormat: String): Long {
    // Create a SimpleDateFormat object with the specified date format
    val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())

    // Parse the date string to a Date object
    val date: Date = sdf.parse(dateString) ?: return -1 // Return -1 for error
    // Return the time in milliseconds
    return date.time
}