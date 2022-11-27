package ru.subnak.sapr.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Rod(
    val square: Double,
    val elasticModule: Double,
    val voltage: Double,
    val loadRunning: Double,
    val rodId: Int,
    val constructionId: Int = UNDEFINED_ID
) : Parcelable {

    companion object {

        const val UNDEFINED_ID = 0
    }
}
