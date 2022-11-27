package ru.subnak.sapr.domain.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Construction(
    val date: Long,
    val nodeValues: List<Node>,
    val rodValues: List<Rod>,
    val img: Bitmap? = null,
    val id: Int = UNDEFINED_ID
) : Parcelable {

    companion object {

        const val UNDEFINED_ID = 0
    }
}
