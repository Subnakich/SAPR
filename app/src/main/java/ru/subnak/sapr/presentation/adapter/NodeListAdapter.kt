package ru.subnak.sapr.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.subnak.sapr.R
import ru.subnak.sapr.databinding.CardviewNodeBinding
import ru.subnak.sapr.domain.model.Node
import ru.subnak.sapr.presentation.callback.NodeCallback
import ru.subnak.sapr.presentation.viewholder.NodeViewHolder
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat

class NodeListAdapter : ListAdapter<Node, NodeViewHolder>(NodeCallback()) {

    var onNodeListClickListener: ((Node, View) -> Unit)? = null
    var onNodeListLongClickListener: ((Node, View) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NodeViewHolder {
        val binding = CardviewNodeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NodeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NodeViewHolder, position: Int) {
        val node = getItem(position)
        holder.binding.tvNodeTitle.text = posToString(node.nodeId + 1, holder.itemView.context)
        holder.binding.tvNodeCoordX.text = trimToEngineeringString(node.x)
        holder.binding.tvNodeProp.text = propYesOrNo(node.prop, holder.itemView.context)
        holder.binding.tvNodeLoadConcentrated.text = trimToEngineeringString(node.loadConcentrated)
        holder.itemView.setOnLongClickListener {
            onNodeListLongClickListener?.invoke(node, it)
            true
        }
        holder.itemView.setOnClickListener {
            onNodeListClickListener?.invoke(node, it)
        }
    }

    private fun propYesOrNo(prop: Boolean, context: Context): String {
        return if (prop) {
            context.getString(R.string.node_cv_yes)
        } else {
            context.getString(R.string.node_cv_no)
        }
    }

    private fun trimToEngineeringString(double: Double): String {
        return double
            .toBigDecimal(MathContext(3,RoundingMode.HALF_UP))
            .toEngineeringString()
    }

    private fun posToString(position: Int, context: Context): String {
        return String.format(
            context.getString(R.string.nodes_number),
            position
        )
    }
}