package com.example.ecommerce.ui

import android.view.View
import androidx.core.content.ContextCompat
import com.example.ecommerce.R
import com.google.android.material.snackbar.Snackbar

fun View.showSnakeBarError(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)
        .setAction(this.context.resources.getString(R.string.ok)) {}.setActionTextColor(
            ContextCompat.getColor(this.context, R.color.white)
        ).show()
}
