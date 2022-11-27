package ru.subnak.sapr.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.subnak.sapr.R
import ru.subnak.sapr.databinding.CardviewRodBinding
import ru.subnak.sapr.domain.model.Rod
import ru.subnak.sapr.presentation.callback.RodCallback
import ru.subnak.sapr.presentation.viewholder.RodViewHolder
import java.math.MathContext
import java.math.RoundingMode

class RodListAdapter : ListAdapter<Rod, RodViewHolder>(RodCallback()) {

    var onRodListClickListener: ((Rod, View) -> Unit)? = null
    var onRodListLongClickListener: ((Rod, View) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RodViewHolder {
        val binding = CardviewRodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RodViewHolder, position: Int) {
        val rod = getItem(position)
        holder.binding.tvRodTitle.text = posToString(rod.rodId + 1, holder.itemView.context)
        holder.binding.tvRodSquare.text = trimToEngineeringString(rod.square)
        holder.binding.tvRodTension.text = trimToEngineeringString(rod.tension)
        holder.binding.tvLoadRunning.text = trimToEngineeringString(rod.loadRunning)
        holder.binding.tvRodElasticModule.text = trimToEngineeringString(rod.elasticModule)
        holder.itemView.setOnLongClickListener {
            onRodListLongClickListener?.invoke(rod, it)
            true
        }
        holder.itemView.setOnClickListener {
            onRodListClickListener?.invoke(rod, it)
        }
    }

    private fun trimToEngineeringString(double: Double): String {
        return double
            .toBigDecimal(MathContext(3, RoundingMode.HALF_UP))
            .toEngineeringString()
    }

    private fun posToString(position: Int, context: Context): String {
        return String.format(
            context.getString(R.string.rods_number),
            position
        )
    }
}