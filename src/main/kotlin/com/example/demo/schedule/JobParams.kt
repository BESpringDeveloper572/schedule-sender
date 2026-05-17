package com.example.demo.schedule

import com.example.demo.mvc.SendRequest

data class JobParams(
    val sendRequest: SendRequest,
    val userId: String
)