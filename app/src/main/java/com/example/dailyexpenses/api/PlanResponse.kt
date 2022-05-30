package com.example.dailyexpenses.api
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

abstract class PlanTemplate {
    abstract val id: Int?
    abstract val name: String
    abstract val price: Float
    abstract val date: Long
    abstract val confirm: Boolean?
    abstract val image: String?
}

data class Plan(
    override val id: Int? = null,
    override val name: String = "",
    override val price: Float = 0f,
    override val date: Long = 0,
    override val confirm: Boolean? = false,
    @SerializedName("category")
    val categoryId: Int = -1,
    @SerializedName("child")
    val childId: Int = -1,
    override val image: String? = null,
    @SerializedName("db_id")
    val dbId: Int = -1
): PlanTemplate()

data class PlanRV(
    override val id: Int? = null,
    override val name: String,
    override val price: Float,
    override val date: Long,
    override val confirm: Boolean?,
    val categoryName: String,
    override val image: String? = null
): PlanTemplate()

data class PlanConfirm(
    val confirm: Boolean
)

data class ChildrenPlan(
    val login: String,
    val plans: List<Plan>
)