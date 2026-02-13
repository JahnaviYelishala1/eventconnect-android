package com.example.eventconnect.data.network

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
interface ApiService {

    @GET("api/protected")
    suspend fun protectedCall(
        @Header("Authorization") token: String
    ): Response<ProtectedResponse>

    @Headers("Content-Type: application/json")
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

    @PATCH("api/events/{eventId}/complete")
    suspend fun completeEvent(
        @Header("Authorization") token: String,
        @Path("eventId") eventId: Int,
        @Body request: CompleteEventRequest
    ): Response<Map<String, Any>>

    @POST("api/ngos/register")
    suspend fun registerNgo(
        @Header("Authorization") token: String,
        @Body request: NGOCreateRequest
    ): Response<NGOResponse>

    @POST("api/ngos/documents")
    suspend fun uploadNgoDocument(
        @Header("Authorization") token: String,
        @Body request: NGODocumentRequest
    ): Response<Map<String, String>>

    @GET("api/ngos/me")
    suspend fun getMyNgo(
        @Header("Authorization") token: String
    ): Response<NgoMeResponse>

    @GET("api/admin/ngos")
    suspend fun getAllNgos(
        @Header("Authorization") token: String
    ): Response<List<AdminNgoResponse>>

    @PATCH("api/admin/ngos/{ngoId}/verify")
    suspend fun verifyNgo(
        @Header("Authorization") token: String,
        @Path("ngoId") ngoId: Int
    ): Response<Map<String, String>>

    @PATCH("api/admin/ngos/{ngoId}/reject")
    suspend fun rejectNgo(
        @Header("Authorization") token: String,
        @Path("ngoId") ngoId: Int
    ): Response<Map<String, String>>

    @PATCH("api/admin/ngos/{ngoId}/suspend")
    suspend fun suspendNgo(
        @Header("Authorization") token: String,
        @Path("ngoId") ngoId: Int
    ): Response<Map<String, String>>

    @GET("api/ngos/documents/status")
    suspend fun getNgoDocumentStatus(
        @Header("Authorization") token: String
    ): Response<NGODocumentStatusResponse>

    @GET("api/ngos/documents")
    suspend fun getMyNgoDocuments(
        @Header("Authorization") token: String
    ): Response<NGODocumentListResponse>

    // ---------------- ADMIN DOCUMENT ACTIONS ----------------
    @PATCH("api/admin/documents/{docId}/approve")
    suspend fun approveDocument(
        @Header("Authorization") token: String,
        @Path("docId") docId: Int
    ): Response<Map<String, String>>

    @PATCH("api/admin/documents/{docId}/reject")
    suspend fun rejectDocument(
        @Header("Authorization") token: String,
        @Path("docId") docId: Int
    ): Response<Map<String, String>>


    @POST("api/ngo/profile")
    suspend fun saveNgoProfile(
        @Header("Authorization") token: String,
        @Body request: NgoProfileRequest
    ): Response<Map<String, String>>

    @GET("api/ngo/profile")
    suspend fun getNgoProfile(
        @Header("Authorization") token: String
    ): Response<NgoProfile>

    @PUT("api/ngo/profile")
    suspend fun updateNgoProfile(
        @Header("Authorization") token: String,
        @Body request: NgoProfileRequest
    ): Response<ApiMessage>

    @Multipart
    @POST("api/ngos/upload-image")  // ✅ matches backend
    suspend fun uploadNgoImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Response<ImageUploadResponse>

    @GET("api/caterers/profile")
    suspend fun getCatererProfile(
        @Header("Authorization") token: String
    ): Response<CatererProfileResponse>

    @POST("api/caterers/profile")
    suspend fun createCatererProfile(
        @Header("Authorization") token: String,
        @Body request: CatererCreateRequest
    ): Response<Map<String, String>>

    @PUT("api/caterers/profile/me")
    suspend fun updateCatererProfile(
        @Header("Authorization") token: String,
        @Body request: CatererCreateRequest
    ): Response<CatererProfileResponse>

    @Multipart
    @POST("api/caterers/upload-image")
    suspend fun uploadCatererImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Response<ImageUploadResponse>

    // ---------------- ORGANIZER PROFILE ----------------

    @GET("api/organizers/profile")
    suspend fun getOrganizerProfile(
        @Header("Authorization") token: String
    ): Response<OrganizerProfileResponse?>

    @POST("api/organizers/profile")
    suspend fun createOrganizerProfile(
        @Header("Authorization") token: String,
        @Body request: OrganizerProfileRequest
    ): Response<ApiMessage>

    @PUT("api/organizers/profile")
    suspend fun updateOrganizerProfile(
        @Header("Authorization") token: String,
        @Body request: OrganizerProfileRequest
    ): Response<ApiMessage>

    @Multipart
    @POST("api/organizers/upload-image")
    suspend fun uploadOrganizerImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Response<ImageUploadResponse>

    @GET("api/caterers/match/{eventId}")
    suspend fun matchCaterers(
        @Header("Authorization") token: String,
        @Path("eventId") eventId: Int,
        @Query("veg_only") vegOnly: Boolean? = null,
        @Query("nonveg_only") nonVegOnly: Boolean? = null,
        @Query("min_price") minPrice: Double? = null,
        @Query("max_price") maxPrice: Double? = null,
        @Query("meal_style") mealStyle: String? = null
    ): Response<List<CatererResponse>>


    @GET("api/bookings/event/{eventId}")
    suspend fun getEventBookingStatus(
        @Header("Authorization") token: String,
        @Path("eventId") eventId: Int
    ): Response<EventBookingStatusResponse>



}
