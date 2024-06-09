package com.example.ecommerce.ui.common

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class UserDetailsUiModel(
  var createdAt:Int?=null,
  var email:String?=null,
  var id:String?=null,
  var name:String?=null,
  var reviews:List<String>?=null,
):Parcelable
