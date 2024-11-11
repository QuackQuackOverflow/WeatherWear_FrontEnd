package com.example.weatherwear.data.model

/*
    private Long id;
    private String memberEmail;
    private String memberPassword;
    private String memberName;
 */

data class User(
    val username: String,
    val id: String,
    val password: String,
    val temperaturePreference: String
)
