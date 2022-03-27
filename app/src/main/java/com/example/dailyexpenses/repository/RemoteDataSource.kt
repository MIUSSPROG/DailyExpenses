package com.example.dailyexpenses.repository

import com.example.dailyexpenses.api.*
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.Retrofit
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val serviceApi: ServiceApi) {

    suspend fun getCategories() = serviceApi.getCategories()

    suspend fun getParents() = serviceApi.getParents()

    suspend fun createParent(parentPost: Parent) = serviceApi.createParent(parentPost)

    suspend fun createParentEncoded(parentPost: Parent) = serviceApi.saveParentEncoded(parentPost)

    suspend fun getParentChildren(id: Int) = serviceApi.getParentChildren(id)

    suspend fun getChildren() = serviceApi.getChildren()

    suspend fun createChild(child: Child) = serviceApi.createChild(child)

    suspend fun createChildEncoded(child: Child) = serviceApi.saveChildEncoded(child)

    suspend fun checkChild(child: Child) = serviceApi.checkChild(child.login, child.password)

    suspend fun checkParent(parent: Parent) = serviceApi.checkParent(parent.login, parent.password)

    suspend fun createPlan(
        name: String,
        price: Float,
        date: String,
        confirm: Boolean,
        categoryId: Int,
        childId: Int,
        image: MultipartBody.Part
    ) = serviceApi.createPlan(name, price, date, confirm, categoryId, childId, image)

    suspend fun getChildrenPlan(id: Int) = serviceApi.getChildrenPlan(id)

    suspend fun confirmPlan(id: Int, plan: Plan) = serviceApi.confirmPlan(id, plan)

    suspend fun sendInvitation(invitation: Invitation) = serviceApi.sendInvitation(invitation)
}