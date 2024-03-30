package com.example.ecommerce.data.model

import java.lang.Exception

sealed class Resources<T>(
    val data: T? = null, val exception: Exception ? = null
) {
    class Loading<T>(data: T? = null) : Resources<T>(data)

    class Success<T>(data: T) : Resources<T>(data)

    class Error<T>(message: Exception, data: T?=null) : Resources<T>(data, message)
}