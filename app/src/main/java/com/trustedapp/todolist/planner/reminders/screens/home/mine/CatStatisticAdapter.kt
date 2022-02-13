package com.trustedapp.todolist.planner.reminders.screens.home.mine

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseDiffCallBack
import com.trustedapp.todolist.planner.reminders.base.BaseViewHolder
import com.trustedapp.todolist.planner.reminders.data.models.entity.DefaultCategories
import com.trustedapp.todolist.planner.reminders.data.models.model.ChartStatisticModel
import com.trustedapp.todolist.planner.reminders.databinding.ItemChartBinding
import javax.inject.Inject

class CatStatisticAdapter @Inject constructor() :
    ListAdapter<ChartStatisticModel, CatStatisticViewHolder>(CatStatisticDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatStatisticViewHolder {
        val itemViewBinding =
            ItemChartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CatStatisticViewHolder(itemViewBinding)
    }

    override fun onBindViewHolder(holder: CatStatisticViewHolder, position: Int) {
        holder.displayData(getItem(position))
    }
}

class CatStatisticViewHolder(private val itemViewBinding: ItemChartBinding) :
    BaseViewHolder<ChartStatisticModel>(itemViewBinding) {

    @SuppressLint("SetTextI18n")
    override fun displayData(entity: ChartStatisticModel) = with(itemViewBinding) {
        textName.text = itemView.context.getString(when(entity.name) {
            DefaultCategories.PERSONAL.name -> R.string.cat_personal
            DefaultCategories.WORK.name -> R.string.cat_work
            DefaultCategories.BIRTHDAY.name -> R.string.cat_birthday
            DefaultCategories.WISHLIST.name -> R.string.cat_wish_list
            "" -> R.string.no_cat
            else -> R.string.cat_other
        }) + ":"
        textCount.text = entity.value
        viewColor.setBackgroundColor(entity.color)
    }
}

class CatStatisticDiffCallback : BaseDiffCallBack<ChartStatisticModel>()
