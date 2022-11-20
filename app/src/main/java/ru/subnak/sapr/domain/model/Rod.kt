package ru.subnak.sapr.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Rod(
    val square: Int,
    val elasticModule: Int,
    val voltage: Int,
    val loadRunning: Int,
    val rodId: Int = UNDEFINED_ID,
    val constructionId: Int = UNDEFINED_ID
) : Parcelable {

    companion object {

        const val UNDEFINED_ID = 0
    }
}
