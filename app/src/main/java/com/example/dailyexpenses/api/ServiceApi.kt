package com.example.dailyexpenses.api

import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

sealed class ApiResponse<T>(
    data: T? = null,
    exception: Exception? = null
){
    data class Success<T>(val data: T): ApiResponse<T>(data, null)
    data class Error<T>(val exception: Exception): ApiResponse<T>(null, exception)
}


interface ServiceApi {

    @GET("api/v1/categories")
    suspend fun getCategories(): List<Category>

    @GET("api/v1/parents")
    suspend fun getParents(): Response<List<Parent>>

    @GET("api/v1/check_parent")
    suspend fun checkParent(@Query("login") login: String): Response<Parent?>

    @POST("api/v1/parent")
    suspend fun createParent(@Body parentPost: Parent): Response<Parent>

    @POST("api/v1/save_parent_encoded/")
    suspend fun saveParentEncoded(@Body parentPost: Parent): Parent

    @GET("api/v1/parent/{id}/children")
    suspend fun getParentChildren(@Path("id") id: Int): Response<ParentChildren>

    @GET("api/v1/children")
    suspend fun getChildren(): Response<List<Child>>

    @POST("api/v1/child")
    suspend fun createChild(@Body child: Child): Response<Child>

    @POST("api/v1/save_child_encoded/")
    suspend fun saveChildEncoded(@Body child: Child): Child
//    ResponseBody
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
        @Part image: MultipartBody.Part?
    ): ResponseBody

    @POST("api/v1/plan/list")
    suspend fun sendPlansForApproval(@Body plans: List<Plan>): Response<List<Plan>>

    @DELETE("api/v1/plan/{id}/destroy")
    suspend fun deletePlan(@Path("id") id: Int): Response<Unit>

    @GET("api/v1/child/{id}/plans")
    suspend fun getChildPlans(@Path("id") id: Int): Response<ChildrenPlan>

    @GET("api/v1/child/filtered_plans")
    suspend fun getFilteredPlans(@Query("mode") mode: Boolean, @Query("childId") childId: Int): List<Plan>

    @PATCH("api/v1/plan/{id}/confirm")
    suspend fun confirmPlan(@Path("id") id: Int, @Body plan: PlanConfirm): Plan

    @PATCH("api/v1/send_invitation/{childId}")
    suspend fun sendInvitation(
        @Path("childId") childId: Int,
        @Body childInvitation: ChildInvitation
    ): Response<Child>

    @GET("api/v1/check_invitation")
    suspend fun checkInvitation(
        @Query("parentId") parentId: Int,
        @Query("login") login: String
    ): Response<Child>

//    @GET("api/v1/children_by_parentId")
//    suspend fun getChildrenInvitations(@Query("parentId") parentId: Int): Response<List<ChildOfParent>>

//    @GET("api/v1/get_invitation_id")
//    suspend fun getInvitationId(@Query("childId") childId: Int, @Query("parentId") parentId: Int): Response<Invitation>

    @PATCH("api/v1/confirm_invitation/{childId}")
    suspend fun confirmInvitation(
        @Path("childId") childId: Int,
        @Body childInvitation: ChildInvitation
    ): Response<Child>

//    @PATCH("api/v1/confirm_invitation/{childId}")
//    fun cancelInvitation(
//        @Path("childId") childId: Int,
//        @Body childInvitation: ChildInvitation
//    ): Flow<Response<Child>>

    @GET("api/v1/check_child_parent")
    suspend fun checkChildParent(@Query("login") login: String): Response<ChildParent>

    companion object {
        const val BASE_URL = "https://expenses-api-app.herokuapp.com/"
    }

}