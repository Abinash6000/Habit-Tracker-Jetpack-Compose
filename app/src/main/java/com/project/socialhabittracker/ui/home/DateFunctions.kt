package com.project.socialhabittracker.ui.home

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun lastFiveDates(): List<String> {
//    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val cal = Calendar.getInstance()
    val lastFiveDates = mutableListOf<String>()

    for (i in 1..5) {
        val date = convertToDateMonthYear(cal.timeInMillis)
//        val date = sdf.format(cal.time)
        lastFiveDates.add(date)
        cal.add(Calendar.DATE, -1)
    }

    return lastFiveDates
}

fun convertToMillis(dateString: String): Long {
    // Create a SimpleDateFormat object with the specified date format
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Parse the date string to a Date object
    val date: Date = sdf.parse(dateString) ?: return -1 // Return -1 for error12
    // Return the time in milliseconds
    Log.d("abcde", "convertToMillis: $date")
    return date.time
}

fun convertToDateMonthYear(dateInMillis: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//    Log.d("abcde", "convertToDateMonthYear: ${sdf.format(dateInMillis)}")
    return sdf.format(dateInMillis)
}

fun convertToDate(dateInMillis: Long): String {
    val sdf = SimpleDateFormat("dd", Locale.getDefault())
    Log.d("abcde", "convertToDate: ${sdf.format(dateInMillis)}")
    return sdf.format(dateInMillis)
}

fun convertToMonthYear(dateInMillis: Long): String {
    val sdf = SimpleDateFormat("MM\nyy", Locale.getDefault())
    Log.d("abcde", "convertToMonthYear: ${sdf.format(dateInMillis)}")
    return sdf.format(dateInMillis)
}