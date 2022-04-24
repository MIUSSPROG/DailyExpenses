package com.example.dailyexpenses.repository

import com.example.dailyexpenses.api.*
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.HttpException
import retrofit2.Retrofit
import java.io.IOException
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val serviceApi: ServiceApi) {

    suspend fun getCategories() = serviceApi.getCategories()

    suspend fun getParents() = serviceApi.getParents()

//    suspend fun createParent(parentPost: Parent) = serviceApi.createParent(parentPost)

    suspend fun createParentEncoded(parentPost: Parent) = serviceApi.saveParentEncoded(parentPost)

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

//    suspend fun createChild(child: Child): ApiResponse<Child>{
//        return try {
//            val response = serviceApi.createChild(child).body()!!
//                ApiResponse.Success(data = response)
//        }catch (e: HttpException){
//            ApiResponse.Error(exception = e)
//        }catch (e: IOException){
//            ApiResponse.Error(exception = e)
//        }
//    }

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

    suspend fun sendPlansForApproval(plans: List<Plan>): ApiResponse<Int>{
        return try {
            val response = serviceApi.sendPlansForApproval(plans).code()
            ApiResponse.Success(data = response)
        }catch (e: HttpException){
            ApiResponse.Error(exception = e)
        }catch (e: IOException){
            ApiResponse.Error(exception = e)
        }
    }

    suspend fun getChildPlans(id: Int) = serviceApi.getChildPlans(id)

    suspend fun getFilteredPlans(mode: Boolean, childId: Int): ApiResponse<List<Plan>>{
        return try {
            val response = serviceApi.getFilteredPlans(mode, childId)
            ApiResponse.Success(data = response)
        }catch (e: HttpException){
            ApiResponse.Error(exception = e)
        }catch (e: IOException){
            ApiResponse.Error(exception = e)
        }
    }

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

    suspend fun deletePlan(childId: Int) = serviceApi.deletePlan(childId)

    suspend fun sendInvitation(id: Int, childInvitation: ChildInvitation) = serviceApi.sendInvitation(id, childInvitation)

    suspend fun checkInvitation(parentId: Int, childLogin: String) = serviceApi.checkInvitation(parentId, childLogin)

//    suspend fun getChildrenInvitations(parentId: Int) = serviceApi.getParentChildren(parentId)

//    suspend fun getInvitationId(parentId: Int, childId: Int) = serviceApi.getInvitationId(childId, parentId)

    suspend fun confirmInvitation(id: Int, childInvitation: ChildInvitation) = serviceApi.confirmInvitation(id, childInvitation)

    suspend fun cancelInvitation(id: Int, childInvitation: ChildInvitation) = serviceApi.confirmInvitation(id, childInvitation)

    suspend fun checkParent(login: String) = serviceApi.checkParent(login)

    suspend fun checkChildParent(login: String) = serviceApi.checkChildParent(login)
}