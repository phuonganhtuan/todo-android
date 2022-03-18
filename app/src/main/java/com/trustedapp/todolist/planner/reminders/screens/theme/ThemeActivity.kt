package com.trustedapp.todolist.planner.reminders.screens.theme

import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ads.control.ads.Admod
import com.ads.control.funtion.AdCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseActivity
import com.trustedapp.todolist.planner.reminders.databinding.ActivityThemeBinding
import com.trustedapp.todolist.planner.reminders.screens.home.HomeActivity
import com.trustedapp.todolist.planner.reminders.screens.home.canShowSuggest
import com.trustedapp.todolist.planner.reminders.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class ThemeActivity : BaseActivity<ActivityThemeBinding>() {

    @Inject
    lateinit var colorAdapter: ThemeTextureColorAdapter

    @Inject
    lateinit var textureAdapter: ThemeTextureColorAdapter

    @Inject
    lateinit var sceneryAdapter: ThemeSceneryAdapter

    private var textures = listOf<Drawable>()
    private var sceneries = listOf<Drawable>()

    private var currentStep = 1

    private var isLoadingAds = false

    private var interstitialCreateAd: InterstitialAd? = null

    override fun inflateViewBinding() = ActivityThemeBinding.inflate(layoutInflater)

    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    override fun onActivityReady() {
        initViews()
        initData()
        setupEvents()
        observeData()
        prepareInterTheme()
    }

    private fun initViews() = with(viewBinding) {
        recyclerColors.adapter = colorAdapter
        recyclerTextures.adapter = textureAdapter
        recyclerSceneries.adapter = sceneryAdapter
        toStep(1)
        loadBannerAds()
    }

    private fun initData() {

        textures = textureIds.map { getDrawableCompat(it) }
        sceneries = sceneryIds.map { getDrawableCompat(it) }

        colorAdapter.selectedIndex = 0
        colorAdapter.submitList(colors.map { item -> getColorDrawable(item) })
        textureAdapter.submitList(textures)
        sceneryAdapter.submitList(sceneries)
    }

    private fun setupEvents() = with(viewBinding) {
        colorAdapter.onItemSelected = {
            colorAdapter.selectedIndex = it
            colorAdapter.notifyDataSetChanged()
            imageColorPreview.setColorFilter(getColor(colors[it]))
            toStep(2)
        }
        textureAdapter.onItemSelected = {
            textureAdapter.selectedIndex = it
            textureAdapter.notifyDataSetChanged()
            imageBgOverlay.gone()
            imageBgPreview.setImageDrawable(textures[it])
            toStep(3)
        }
        sceneryAdapter.onItemSelected = {
            sceneryAdapter.selectedIndex = it
            sceneryAdapter.notifyDataSetChanged()
            imageBgPreview.setImageDrawable(sceneries[it])
            imageBgOverlay.show()
        }
        buttonPreviousStep.setOnClickListener {
            toStep(currentStep - 1)
        }
        textSkip.setOnClickListener {
            canShowSuggest = true
            startActivity(Intent(this@ThemeActivity, HomeActivity::class.java))
            finish()
        }
        textApply.setOnClickListener {
            loadInterTheme()
        }
    }

    private fun toHome() {
        SPUtils.saveTheme(
            this@ThemeActivity,
            if (colorAdapter.selectedIndex > -1) colorAdapter.selectedIndex else 0,
            textureAdapter.selectedIndex,
            sceneryAdapter.selectedIndex
        )
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun toStep(step: Int) = with(viewBinding) {
        when (step) {
            1 -> {
                currentStep = step
                textStepName.text = getString(R.string.colors)
                buttonPreviousStep.gone()
                recyclerSceneries.gone()
                recyclerTextures.gone()
                recyclerColors.show()
                indicator1.background = getDrawableCompat(R.drawable.bg_primary_rounded_8)
                indicator2.background =
                    getDrawableCompat(R.drawable.bg_ripple_white_stroke_grey_border_rounded_8)
                indicator3.background =
                    getDrawableCompat(R.drawable.bg_ripple_white_stroke_grey_border_rounded_8)
            }
            2 -> {
                currentStep = step
                textStepName.text = getString(R.string.textures)
                buttonPreviousStep.show()
                recyclerColors.gone()
                recyclerTextures.show()
                recyclerSceneries.gone()
                indicator1.background =
                    getDrawableCompat(R.drawable.bg_ripple_white_stroke_grey_border_rounded_8)
                indicator2.background = getDrawableCompat(R.drawable.bg_primary_rounded_8)
                indicator3.background =
                    getDrawableCompat(R.drawable.bg_ripple_white_stroke_grey_border_rounded_8)
                textApply.show()
            }
            3 -> {
                currentStep = step
                textStepName.text = getString(R.string.sceneries)
                buttonPreviousStep.show()
                recyclerColors.gone()
                recyclerTextures.gone()
                recyclerSceneries.show()
                indicator1.background =
                    getDrawableCompat(R.drawable.bg_ripple_white_stroke_grey_border_rounded_8)
                indicator2.background =
                    getDrawableCompat(R.drawable.bg_ripple_white_stroke_grey_border_rounded_8)
                indicator3.background = getDrawableCompat(R.drawable.bg_primary_rounded_8)
                textApply.show()
            }
        }
    }

    private fun observeData() {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                NetworkState.isHasInternet.collect {
                    loadBannerAds()
                }
            }
        }
    }

    private fun loadBannerAds() = with(viewBinding) {
        if (Firebase.remoteConfig.getBoolean(SPUtils.KEY_BANNER) && isInternetAvailable()) {
            include.visibility = View.VISIBLE
            Admod.getInstance()
                .loadBanner(this@ThemeActivity, getString(R.string.banner_ads_id))
        } else {
            include.visibility = View.GONE
        }
    }

    private fun getColorDrawable(color: Int) = ContextCompat.getColor(this, color).toDrawable()

    private fun getDrawableCompat(drawable: Int) = ContextCompat.getDrawable(this, drawable)!!

    private fun prepareInterTheme() {

//        isLoadingAds = true
        Admod.getInstance().getInterstitalAds(
            this@ThemeActivity,
            getString(R.string.inter_theme_ads_id),
            object : AdCallback() {
                override fun onInterstitialLoad(interstitialAd: InterstitialAd) {
                    interstitialCreateAd = interstitialAd
                }
            })
    }

    private fun loadInterTheme() {
        if (!Firebase.remoteConfig.getBoolean(SPUtils.KEY_INTER_THEME)) {
            isLoadingAds = false
            canShowSuggest = true
            toHome()
            return
        }

        if (!this.isInternetAvailable()) {
            isLoadingAds = false
            canShowSuggest = true
            toHome()
            return
        }

        Admod.getInstance().setOpenActivityAfterShowInterAds(true)
        Admod.getInstance()
            .forceShowInterstitial(
                this@ThemeActivity,
                interstitialCreateAd,
                object : AdCallback() {
                    override fun onAdClosed() {
                        isLoadingAds = false
                        toHome()
                    }

                    override fun onAdLeftApplication() {
                        super.onAdLeftApplication()
                        isLoadingAds = false
                        canShowSuggest = true
                        toHome()

                    }

                    override fun onAdFailedToLoad(i: LoadAdError?) {
                        super.onAdFailedToLoad(i)
                        isLoadingAds = false
                        canShowSuggest = true
                        toHome()
                    }
                })
    }
}
