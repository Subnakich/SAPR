package ru.subnak.sapr.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.subnak.sapr.R
import ru.subnak.sapr.databinding.ConstructionCardviewBinding
import ru.subnak.sapr.domain.model.Construction
import ru.subnak.sapr.presentation.callback.ConstructionCallback
import ru.subnak.sapr.presentation.viewholder.ConstructionViewHolder
import java.time.Instant
import java.time.ZoneId

class ConstructionListAdapter : ListAdapter<Construction, ConstructionViewHolder>(
    ConstructionCallback()
) {

    var onConstructionListLongClickListener: ((Construction) -> Unit)? = null
    var onConstructionListClickListener: ((Construction) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConstructionViewHolder {
        val binding = ConstructionCardviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ConstructionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConstructionViewHolder, position: Int) {
        val construction = getItem(position)
        holder.binding.tvDateHistory.text = getDate(construction.date, holder.itemView.context)
        holder.binding.ivRod.setImageBitmap(construction.img)
        holder.itemView.setOnClickListener {
            onConstructionListClickListener?.invoke(construction)
        }
    }

    private fun getDate(date: Long, context: Context): String {
        val constructionDate = Instant
            .ofEpochMilli(date)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val cmpDate = Instant
            .ofEpochMilli(System.currentTimeMillis())
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        val text = when {
            cmpDate.minusYears(SUBTRACT_ONE.toLong()) > constructionDate -> context.getString(R.string.few_years_ago)
            cmpDate.minusMonths(SUBTRACT_ONE.toLong()) > constructionDate -> context.getString(R.string.few_months_ago)
            cmpDate.minusWeeks(SUBTRACT_THREE.toLong()) > constructionDate -> context.getString(R.string.one_month_ago)
            cmpDate.minusWeeks(SUBTRACT_TWO.toLong()) > constructionDate -> context.getString(R.string.two_weeks_ago)
            cmpDate.minusWeeks(SUBTRACT_ONE.toLong()) > constructionDate -> context.getString(R.string.one_week_ago)
            cmpDate.minusDays(SUBTRACT_ONE.toLong()) > constructionDate -> context.getString(R.string.few_days_ago)
            cmpDate > constructionDate -> context.getString(R.string.yesterday)
            else -> context.resources.getString(R.string.today)
        }
        return text
    }

    companion object {

        private const val SUBTRACT_ONE = 1
        private const val SUBTRACT_TWO = 2
        private const val SUBTRACT_THREE = 3
    }
}