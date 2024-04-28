package com.example.ecommerce.domain

import com.example.ecommerce.data.model.user.UserDetailsModel
import com.example.ecommerce.data.model.user.UserDetailsPreferences

fun UserDetailsModel.toUserDetailsPreferences():UserDetailsPreferences{
    return UserDetailsPreferences.newBuilder()
        .setId(id)
        .setName(name)
        .setEmail(email)
        .addAllReviews(reviews?.toList())
        .build()
}

fun UserDetailsPreferences.toUserDetailsModel():UserDetailsModel{
    return UserDetailsModel(
        id = id,
        name = name,
        email = email,
        reviews = reviewsList
    )
}