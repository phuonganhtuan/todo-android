package com.trustedapp.todolist.planner.reminders.screens.language.start

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ads.control.ads.Admod
import com.ads.control.funtion.AdCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseActivity
import com.trustedapp.todolist.planner.reminders.databinding.ActivityLanguageBinding
import com.trustedapp.todolist.planner.reminders.screens.home.HomeActivity
import com.trustedapp.todolist.planner.reminders.screens.language.LanguageModel
import com.trustedapp.todolist.planner.reminders.screens.theme.ThemeActivity
import com.trustedapp.todolist.planner.reminders.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*


@AndroidEntryPoint
class LanguageSetupActivity : BaseActivity<ActivityLanguageBinding>() {

    private var langList = mutableListOf<LanguageModel>()

    private var selectedItem = 0

    private var nativeAds: NativeAd? = null

    private val networkReceiver = NetworkChangeReceiver()

    override fun inflateViewBinding() = ActivityLanguageBinding.inflate(layoutInflater)

    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    override fun onActivityReady() {
        initViews()
        initData()
        setupEvents()
        loadAds()
    }

    private fun setupEvents() = with(viewBinding) {
        buttonApply.setOnClickListener {
            applyLanguage(langList[selectedItem].langCode)
            LangData.isUpdatedLang.value = true
            if (SPUtils.isFirstTime(this@LanguageSetupActivity)) {
                startActivity(Intent(this@LanguageSetupActivity, ThemeActivity::class.java))
            } else {
                startActivity(Intent(this@LanguageSetupActivity, HomeActivity::class.java))
            }
            finish()
        }
        lang1.radioChoose.setOnClickListener {
            selectLang(0)
        }
        lang2.radioChoose.setOnClickListener {
            selectLang(1)
        }
        lang3.radioChoose.setOnClickListener {
            selectLang(2)
        }
        lang4.radioChoose.setOnClickListener {
            selectLang(3)
        }
        lang5.radioChoose.setOnClickListener {
            selectLang(4)
        }
    }

    private fun initData() {
        val supportLangs = listOf("en", "pt", "es", "fr", "hi")
        val deviceLanguage = Locale.getDefault().language
        val isInAppLangs = supportLangs.contains(deviceLanguage)

        langList = mutableListOf(
            LanguageModel(
                id = 0,
                langName = "English",
                flagId = ContextCompat.getDrawable(this, R.drawable.flag_england),
                langCode = "en"
            ),
            LanguageModel(
                id = 1,
                langName = "Portugal",
                flagId = ContextCompat.getDrawable(this, R.drawable.flag_portugal),
                langCode = "pt"
            ),
            LanguageModel(
                id = 2,
                langName = "Espanha",
                flagId = ContextCompat.getDrawable(this, R.drawable.flag_spain),
                langCode = "es"
            ),
            LanguageModel(
                id = 3,
                langName = "França",
                flagId = ContextCompat.getDrawable(this, R.drawable.flag_france),
                langCode = "fr"
            ),
            LanguageModel(
                id = 4,
                langName = "Índia",
                flagId = ContextCompat.getDrawable(this, R.drawable.flag_india),
                langCode = "hi"
            ),
        )

        if (isInAppLangs) {
            val index = supportLangs.indexOf(deviceLanguage)
            val deviceLangObject = langList[index]
            langList.removeAt(index)
            langList.add(0, deviceLangObject)
        }
        viewBinding.lang1.apply {
            imageFlag.setImageDrawable(langList[0].flagId)
            textName.text = langList[0].langName
            radioChoose.isChecked = true
        }
        viewBinding.lang2.apply {
            imageFlag.setImageDrawable(langList[1].flagId)
            textName.text = langList[1].langName
        }
        viewBinding.lang3.apply {
            imageFlag.setImageDrawable(langList[2].flagId)
            textName.text = langList[2].langName
        }
        viewBinding.lang4.apply {
            imageFlag.setImageDrawable(langList[3].flagId)
            textName.text = langList[3].langName
        }
        viewBinding.lang5.apply {
            imageFlag.setImageDrawable(langList[4].flagId)
            textName.text = langList[4].langName
        }
    }

    private fun selectLang(index: Int) = with(viewBinding) {
        val langViews = listOf(
            lang1.radioChoose,
            lang2.radioChoose,
            lang3.radioChoose,
            lang4.radioChoose,
            lang5.radioChoose
        )
        langViews.forEach { it.isChecked = false }
        langViews[index].isChecked = true
        selectedItem = index
    }

    private fun initViews() = with(viewBinding) {
        lang1.radioChoose.isChecked = false
        lang2.radioChoose.isChecked = false
        lang3.radioChoose.isChecked = false
        lang4.radioChoose.isChecked = false
        lang5.radioChoose.isChecked = false

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                NetworkState.isHasInternet.collect {
                    layoutAds.visibility = if (it) {
                        if (false) {
                            View.INVISIBLE
                        } else {
                            if (nativeAds == null) {
                                loadAds()
                            }
                            View.VISIBLE
                        }
                    } else {
                        View.INVISIBLE
                    }
                }
            }
        }
    }

    private fun loadAds() = with(viewBinding) {
        if (!FirebaseRemoteConfig.getInstance().getBoolean(SPUtils.KEY_NATIVE_LANGUAGE)) {
            layoutAds.hide()
            return@with
        }
        if (!isInternetAvailable()) return@with
        skeletonLayout.showSkeleton()
        Admod.getInstance()
            .loadNativeAd(
                this@LanguageSetupActivity,
                getString(R.string.native_language_setup_ads_id),
                object : AdCallback() {
                    override fun onUnifiedNativeAdLoaded(unifiedNativeAd: NativeAd) {
                        this@LanguageSetupActivity.nativeAds = unifiedNativeAd
                        skeletonLayout.showOriginal()
                        imageAdDescLoading.gone()
                        Admod.getInstance().populateUnifiedNativeAdView(unifiedNativeAd, adView)
                        imageIcon.setImageDrawable(unifiedNativeAd.icon?.drawable)
                        imageContent.setImageDrawable(unifiedNativeAd.mediaContent?.mainImage)
                    }
                })
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(networkReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkReceiver)
    }
}
