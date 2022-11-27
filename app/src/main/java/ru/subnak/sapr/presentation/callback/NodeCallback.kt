package ru.subnak.sapr.presentation.callback

import androidx.recyclerview.widget.DiffUtil
import ru.subnak.sapr.domain.model.Node

class NodeCallback : DiffUtil.ItemCallback<Node>() {

    override fun areItemsTheSame(oldItem: Node, newItem: Node): Boolean {
        return oldItem.nodeId == newItem.nodeId
    }

    override fun areContentsTheSame(oldItem: Node, newItem: Node): Boolean {
        return oldItem == newItem
    }
}