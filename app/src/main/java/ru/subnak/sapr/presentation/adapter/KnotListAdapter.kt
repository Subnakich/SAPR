package ru.subnak.sapr.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.subnak.sapr.R
import ru.subnak.sapr.databinding.KnotCardviewBinding
import ru.subnak.sapr.domain.model.Knot
import ru.subnak.sapr.presentation.callback.KnotCallback
import ru.subnak.sapr.presentation.viewholder.KnotViewHolder

class KnotListAdapter : ListAdapter<Knot, KnotViewHolder>(KnotCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KnotViewHolder {
        val binding = KnotCardviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return KnotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: KnotViewHolder, position: Int) {
        val knot = getItem(position)
        holder.binding.tvKnotTitle.text = posToString(position+1, holder.itemView.context)
        holder.binding.tvKnotCoordX.text = knot.x.toString()
        holder.binding.tvKnotProp.text = knot.prop.toString()
        holder.binding.tvKnotLoadConcentrated.text = knot.loadConcentrated.toString()
    }

    private fun posToString(position: Int, context: Context): String {
        return String.format(
            context.getString(R.string.knots_number),
            position
        )
    }
}