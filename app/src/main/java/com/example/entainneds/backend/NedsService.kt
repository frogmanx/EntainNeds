package com.example.entainneds.backend

import retrofit2.http.GET
import retrofit2.http.Query

interface NedsService {

    @GET("/rest/v1/racing/")
    suspend fun getRaces(@Query("method") method: String, @Query("count") count: Int): NedsResponse
}