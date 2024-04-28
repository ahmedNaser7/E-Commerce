package com.example.ecommerce.data.model.user

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class UserDetailsModel(

    var createdAt: Long? = null,
    var id: String? = null,
    var email: String? = null,
    var name: String? = null,
    var disabled: Boolean? = null,
    var reviews: List<String>? = null,
) : Parcelable
