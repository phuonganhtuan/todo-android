package com.trustedapp.todolist.planner.reminders.screens.language.setting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.ads.control.ads.Admod
import com.ads.control.funtion.AdCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseViewHolder
import com.trustedapp.todolist.planner.reminders.databinding.ItemLanguageSettingBinding
import com.trustedapp.todolist.planner.reminders.screens.language.LanguageDiffCallback
import com.trustedapp.todolist.planner.reminders.screens.language.LanguageModel
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.show
import javax.inject.Inject

class LanguageSettingAdapter @Inject constructor() :
    ListAdapter<LanguageModel, LanguageSettingVH>(LanguageDiffCallback()) {

    var onItemSelected: ((Int) -> Unit)? = null
    var selectedItem = 0
    var enableAds = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageSettingVH {
        val itemViewBinding =
            ItemLanguageSettingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LanguageSettingVH(itemViewBinding, onItemSelected)
    }

    override fun onBindViewHolder(holder: LanguageSettingVH, position: Int) {
        holder.displayData(getItem(position), selectedItem == position, position == 6, enableAds)
    }
}

class LanguageSettingVH(
    private val itemViewBinding: ItemLanguageSettingBinding,
    private val onItemSelected: ((Int) -> Unit)?
) : BaseViewHolder<LanguageModel>(itemViewBinding) {

    private var ad: NativeAd? = null

    init {
        itemViewBinding.radioLanguage.setOnClickListener {
            onItemSelected?.let { it(absoluteAdapterPosition) }
        }
    }

    override fun displayData(entity: LanguageModel) = with(itemViewBinding) {
    }

    fun displayData(entity: LanguageModel, isSelected: Boolean, hasAds: Boolean, enableAds: Boolean) =
        with(itemViewBinding) {
            radioLanguage.text = entity.langName
            if (enableAds && hasAds) displayAds() else adView.gone()
            radioLanguage.isChecked = isSelected
        }

    private fun displayAds() = with(itemViewBinding) {
        adView.show()
        if (ad != null) {
            display()
        } else {
            skeletonLayout.showSkeleton()
            imageAdDescLoading.show()
            Admod.getInstance()
                .loadNativeAd(
                    itemView.context,
                    itemView.context.getString(R.string.native_language_setting_ads_id),
                    object : AdCallback() {
                        override fun onUnifiedNativeAdLoaded(unifiedNativeAd: NativeAd) {
                            this@LanguageSettingVH.ad = unifiedNativeAd
                            display()
                        }
                    })
        }
    }

    private fun display() = with(itemViewBinding) {
        skeletonLayout.showOriginal()
        imageAdDescLoading.gone()
        Admod.getInstance()
            .populateUnifiedNativeAdView(ad, adView)
        imageIcon.setImageDrawable(ad?.icon?.drawable)
    }
}
