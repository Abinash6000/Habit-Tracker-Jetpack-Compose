package com.project.socialhabittracker.utils

import android.content.Context
import android.widget.Toast

object Extensions {
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}