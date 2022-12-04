package ru.subnak.sapr.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Construction(
    val date: Long,
    val nodeValues: List<Node>,
    val rodValues: List<Rod>,
    val img: String,
    val id: Int = UNDEFINED_ID
) : Parcelable {

    companion object {

        const val UNDEFINED_ID = 0
    }
}
