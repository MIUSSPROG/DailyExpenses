package com.example.dailyexpenses.api

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ServiceApi {

    @GET("api/v1/categories")
    suspend fun getCategories(): Response<Category>

    @GET("api/v1/parents")
    suspend fun getParents(): Response<List<Parent>>

    @POST("api/v1/parent")
    suspend fun createParent(@Body parentPost: Parent): Response<Parent>

    @GET("api/v1/parent/{id}/children")
    suspend fun getParentChildren(@Path("id") id: Int): Response<ParentChildren>

    @GET("api/v1/children")
    suspend fun getChildren(): Response<List<Child>>

    @POST("api/v1/child")
    suspend fun createChild(@Body child: Child): Response<Child>

    @Multipart
    @POST("api/v1/plan")
    suspend fun createPlan(
        @Part("name") name: String,
        @Part("price") price: Float,
        @Part("date") date: String,
        @Part("confirm") confirm: Boolean,
        @Part("category") categoryId: Int,
        @Part("child") childId: Int,
        @Part image: MultipartBody.Part
    ): ResponseBody


    @GET("api/v1/plan/children/{id}")
    suspend fun getChildrenPlan(@Path("id") id: Int): Response<ChildrenPlan>

    @PATCH("api/v1/plan/{id}/confirm")
    suspend fun confirmPlan(@Path("id") id: Int, @Body plan: Plan)

    companion object{
//        const val BASE_URL = "http://127.0.0.1:8000/"
//        const val BASE_URL = "http://10.0.2.2:8000/"
        const val BASE_URL = "https://daily-expenses.herokuapp.com/"
//        http://10.0.2.2:8000/api/v1/categories
    }

}