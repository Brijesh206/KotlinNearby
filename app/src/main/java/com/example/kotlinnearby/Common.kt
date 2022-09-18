package com.example.kotlinnearby

import com.example.kotlinnearby.Model.Result
import com.example.kotlinnearby.Remote.IGoogleAPIService
import com.example.kotlinnearby.Remote.RetrofitClient

object Common {
    var currentResult: Result? = null

    private val GOOGLE_API_URL="https://maps.googleapis.com/"
    val googleApiService: IGoogleAPIService
        get()= RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService::class.java)
}