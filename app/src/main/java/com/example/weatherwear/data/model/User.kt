package com.example.weatherwear.data.model

import android.provider.ContactsContract.CommonDataKinds.Email

data class User(
    //val id : Int,
    val memberEmail: String,
    val memberPassword: String,
    val memberName: String,
    val userType: String
)

data class Member(
    val id: Long?,
    val memberEmail: String,
    val memberPassword: String,
    val memberName: String,
    val userType: String
)
