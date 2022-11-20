package ru.subnak.sapr.presentation.callback

import androidx.recyclerview.widget.DiffUtil
import ru.subnak.sapr.domain.model.Rod

class RodCallback : DiffUtil.ItemCallback<Rod>() {

    override fun areItemsTheSame(oldItem: Rod, newItem: Rod): Boolean {
        return oldItem.rodId == newItem.rodId
    }

    override fun areContentsTheSame(oldItem: Rod, newItem: Rod): Boolean {
        return oldItem == newItem
    }
}