package com.example.ecommerce.utils


fun String.isValidEmail():Boolean{
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}