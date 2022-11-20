package ru.subnak.sapr.presentation.callback

import androidx.recyclerview.widget.DiffUtil
import ru.subnak.sapr.domain.model.Construction

class ConstructionCallback : DiffUtil.ItemCallback<Construction>() {

    override fun areItemsTheSame(oldItem: Construction, newItem: Construction): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Construction, newItem: Construction): Boolean {
        return oldItem == newItem
    }
}