package com.example.dailyexpenses.repository

import com.example.dailyexpenses.api.*
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val serviceApi: ServiceApi) {

    suspend fun getCategories() = serviceApi.getCategories()

    suspend fun getParents() = serviceApi.getParents()

    suspend fun createParentEncoded(parentPost: Parent): ApiResponse<Parent>{
        return try{
            val response = serviceApi.saveParentEncoded(parentPost)
            ApiResponse.Success(data = response)
        }catch (e: Exception) {
            ApiResponse.Error(exception = e)
        }
    }

    suspend fun getParentChildren(id: Int):ApiResponse<ParentChildren>{
        return try {
            val response = serviceApi.getParentChildren(id).body()!!
            ApiResponse.Success(data = response)
        }catch (e: HttpException){
            ApiResponse.Error(exception = e)
        }catch (e: IOException){
            ApiResponse.Error(exception = e)
        }
    }

    suspend fun getChildren() = serviceApi.getChildren()

    suspend fun createChildEncoded(child: Child): ApiResponse<Child>{
        return try {
            val response = serviceApi.saveChildEncoded(child)
            ApiResponse.Success(data = response)
        }catch (e: HttpException){
            ApiResponse.Error(exception = e)
        }catch (e: IOException){
            ApiResponse.Error(exception = e)
        }
    }

    suspend fun checkChild(child: Child) = serviceApi.checkChild(child.login, child.password)

    suspend fun checkParent(parent: Parent) = serviceApi.checkParent(parent.login, parent.password)

//    suspend fun createPlan(plan: Plan) = serviceApi.createPlan(plan.name, plan.price, plan.date, plan.confirm, plan.categoryId, plan.childId, plan.image)

    suspend fun sendPlansForApproval(plans: List<Plan>): ApiResponse<List<Plan>>{
        return try {
            val response = serviceApi.sendPlansForApproval(plans)
            ApiResponse.Success(data = response.body()!!)
        }catch (e: HttpException){
            ApiResponse.Error(exception = e)
        }catch (e: IOException){
            ApiResponse.Error(exception = e)
        }
    }

    suspend fun getChildPlans(id: Int, fromDateUnix: Long, toDateUnix: Long, confirmed: Boolean? = null) = serviceApi.getChildPlans(childId = id, fromDateUnix = fromDateUnix, toDateUnix = toDateUnix, confirmed=confirmed)

//    suspend fun getAllChildPlans(id: Int) = serviceApi.getAllChildPlans(childId = id)
//
//    suspend fun getFilteredPlans(mode: Boolean, childId: Int) = serviceApi.getFilteredPlans(mode, childId)

    suspend fun confirmPlan(id: Int, plan: PlanConfirm): ApiResponse<Plan>{
        return try {
            val response = serviceApi.confirmPlan(id, plan)
            ApiResponse.Success(data = response)
        }catch (e: HttpException){
            ApiResponse.Error(exception = e)
        }catch (e: IOException){
            ApiResponse.Error(exception = e)
        }
    }

    suspend fun deletePlan(planId: Int): ApiResponse<Int>{
        return try {
            serviceApi.deletePlan(planId)
            ApiResponse.Success(data = 204)
        }catch (e: HttpException){
            ApiResponse.Error(exception = e)
        }catch (e: IOException){
            ApiResponse.Error(exception = e)
        }
    }

    suspend fun sendInvitation(id: Int, childInvitation: ChildInvitation) = serviceApi.sendInvitation(id, childInvitation)

    suspend fun checkInvitation(parentId: Int, childLogin: String) = serviceApi.checkInvitation(parentId, childLogin)

    suspend fun confirmInvitation(id: Int, childInvitation: ChildInvitation): ApiResponse<Response<Child>>{
        return try{
            val response = serviceApi.confirmInvitation(id, childInvitation)
            ApiResponse.Success(response)
        }catch (e: Exception){
            ApiResponse.Error(e)
        }
    }

    suspend fun cancelInvitation(id: Int, childInvitation: ChildInvitation) = serviceApi.confirmInvitation(id, childInvitation)

    suspend fun checkParent(login: String) = serviceApi.checkParent(login)

    suspend fun checkChildParent(login: String) = serviceApi.checkChildParent(login)
}