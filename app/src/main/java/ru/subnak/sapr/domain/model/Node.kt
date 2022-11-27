package ru.subnak.sapr.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Node(
    val x: Double,
    val loadConcentrated: Double,
    val prop: Boolean,
    val nodeId: Int,
    val constructionId: Int = UNDEFINED_ID
) : Parcelable {

    companion object {

        const val UNDEFINED_ID = 0
    }
}