package com.example.dailyexpenses.api

import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    @POST("api/v1/save_parent_encoded/")
    suspend fun saveParentEncoded(@Body parentPost: Parent): ResponseBody

    @GET("api/v1/parent/{id}/children")
    suspend fun getParentChildren(@Path("id") id: Int): Response<ParentChildren>

    @GET("api/v1/children")
    suspend fun getChildren(): Response<List<Child>>

    @POST("api/v1/child")
    suspend fun createChild(@Body child: Child): Response<Child>

    @POST("api/v1/save_child_encoded/")
    suspend fun saveChildEncoded(@Body child: Child): ResponseBody

    @GET("api/v1/check_child/")
    suspend fun checkChild(
        @Query("login") login: String,
        @Query("password") password: String
    ): Response<Child?>

    @GET("api/v1/check_parent")
    suspend fun checkParent(
        @Query("login") login: String,
        @Query("password") password: String
    ): Response<Parent?>

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

    @POST("api/v1/send_invitation")
    suspend fun sendInvitation(@Body invitation: Invitation): Response<Invitation>

    @GET("api/v1/children_by_parentId")
    suspend fun getChildrenInvitations(@Query("parentId") parentId: Int): Response<List<ChildOfParent>>

    @GET("api/v1/get_invitation_id")
    suspend fun getInvitationId(@Query("childId") childId: Int, @Query("parentId") parentId: Int): Response<Invitation>

    @PATCH("api/v1/confirm_invitation/{id}")
    suspend fun confirmInvitation(@Path("id") id: Int, @Body invitation: Invitation): ResponseBody

    companion object {
        //        const val BASE_URL = "http://127.0.0.1:8000/"
//        const val BASE_URL = "http://10.0.2.2:8000/"
        const val BASE_URL = "https://daily-expenses.herokuapp.com/"
//        http://10.0.2.2:8000/api/v1/categories
    }

}