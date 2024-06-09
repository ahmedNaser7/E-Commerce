package com.example.ecommerce.data.model

import java.lang.Exception

sealed class Resource<T>(
    val data: T? = null, val exception: Exception ? = null
) {
    class Loading<T>(data: T? = null) : Resource<T>(data)

    class Success<T>(data: T) : Resource<T>(data)

    class Error<T>(message: Exception, data: T?=null) : Resource<T>(data, message)
}