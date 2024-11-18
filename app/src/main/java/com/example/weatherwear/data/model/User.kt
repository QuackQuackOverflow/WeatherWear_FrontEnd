package com.example.weatherwear.data.model

import android.provider.ContactsContract.CommonDataKinds.Email

data class User(
    val memberName: String,
    val memberEmail: String,
    val memberPassword: String,
    val userType: String
)
