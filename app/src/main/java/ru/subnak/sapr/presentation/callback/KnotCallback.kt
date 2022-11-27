package ru.subnak.sapr.presentation.callback

import androidx.recyclerview.widget.DiffUtil
import ru.subnak.sapr.domain.model.Knot

class KnotCallback : DiffUtil.ItemCallback<Knot>() {

    override fun areItemsTheSame(oldItem: Knot, newItem: Knot): Boolean {
        return oldItem.x == newItem.x
    }

    override fun areContentsTheSame(oldItem: Knot, newItem: Knot): Boolean {
        return oldItem == newItem
    }
}