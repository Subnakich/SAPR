package ru.subnak.sapr.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Knot(
    val x: Int,
    val loadConcentrated: Int,
    val prop: Boolean,
    val knotNumber: Int,
    val knotId: Int = UNDEFINED_ID,
    val constructionId: Int = UNDEFINED_ID
): Parcelable {

    companion object {

        const val UNDEFINED_ID = 0
    }
}