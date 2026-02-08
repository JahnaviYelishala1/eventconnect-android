package com.example.eventconnect.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @GET("api/protected")
    suspend fun protectedCall(
        @Header("Authorization") token: String
    ): Response<Map<String, Any>>

    @POST("api/users/select-role")
    suspend fun selectRole(
        @Query("role") role: String,
        @Header("Authorization") token: String
    ): Response<Map<String, String>>

    // 🔹 Prediction ONLY (no DB save)
    @POST("api/events/predict-food")
    suspend fun predictFood(
        @Header("Authorization") token: String,
        @Body request: FoodPredictionRequest
    ): Response<FoodPredictionResponse>

    // 🔹 CREATE EVENT + SAVE TO DB (THIS WAS MISSING)
    @POST("api/events")
    suspend fun createEvent(
        @Header("Authorization") token: String,
        @Body request: EventCreateRequest
    ): Response<CreateEventResponse>

    @GET("api/events/my-events")
    suspend fun getMyEvents(
        @Header("Authorization") token: String
    ): Response<List<EventResponse>>

}
