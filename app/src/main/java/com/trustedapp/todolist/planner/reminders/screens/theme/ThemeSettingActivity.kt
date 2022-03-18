package com.trustedapp.todolist.planner.reminders.screens.theme

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
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
import com.bumptech.glide.Glide
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseActivity
import com.trustedapp.todolist.planner.reminders.databinding.ActivityThemeSettingBinding
import com.trustedapp.todolist.planner.reminders.screens.home.HomeActivity
import com.trustedapp.todolist.planner.reminders.utils.*
import com.trustedapp.todolist.planner.reminders.utils.Constants.EXTRA_THEME_UPDATE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class ThemeSettingActivity : BaseActivity<ActivityThemeSettingBinding>() {

    @Inject
    lateinit var colorAdapter: ThemeTextureColorAdapter

    @Inject
    lateinit var textureAdapter: ThemeTextureColorAdapter

    @Inject
    lateinit var sceneryAdapter: ThemeSceneryAdapter

    private var textures = listOf<Drawable>()
    private var sceneries = listOf<Drawable>()

    private var texture = -1
    private var scenery = -1
    private var color = -1

    private var currentStep = 1

    private var isLoadingAds = false

    private var interstitialCreateAd: InterstitialAd? = null

    override fun inflateViewBinding() = ActivityThemeSettingBinding.inflate(layoutInflater)

    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    override fun onActivityReady() {
        initViews()
        initData()
        setupEvents()
//        prepareInterTheme()
        observeData()
    }

    private fun initViews() = with(viewBinding) {
        recyclerColors.adapter = colorAdapter
        recyclerTextures.adapter = textureAdapter
        recyclerSceneries.adapter = sceneryAdapter
        toStep(1)
        header.apply {
            button2.hide()
            button3.hide()
            button4.hide()
            button1.setImageResource(R.drawable.ic_arrow_left)
            textTitle.text = getString(R.string.theme)
        }
        val theme = SPUtils.getSavedTheme(this@ThemeSettingActivity)
        color = theme.first
        texture = theme.second
        scenery = theme.third
        imageBgOverlay.gone()
        imageColorPreview.setColorFilter(getColor(colors[color]))
        if (scenery > -1) {
            imageBgOverlay.show()
            Glide.with(this@ThemeSettingActivity)
                .load(ContextCompat.getDrawable(this@ThemeSettingActivity, sceneryIds[scenery]))
                .into(imageBgPreview)
            return
        } else if (texture > -1) {
            imageBgOverlay.gone()
            Glide.with(this@ThemeSettingActivity)
                .load(ContextCompat.getDrawable(this@ThemeSettingActivity, textureIds[texture]))
                .into(imageBgPreview)
        }
        loadBannerAds()
    }

    private fun initData() {

        textures = textureIds.map { getDrawableCompat(it) }
        sceneries = sceneryIds.map { getDrawableCompat(it) }

        colorAdapter.selectedIndex = color
        textureAdapter.selectedIndex = texture
        sceneryAdapter.selectedIndex = scenery
        colorAdapter.submitList(colors.map { item -> getColorDrawable(item) })
        textureAdapter.submitList(textures)
        sceneryAdapter.submitList(sceneries)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupEvents() = with(viewBinding) {
        colorAdapter.onItemSelected = {
            colorAdapter.selectedIndex = it
            colorAdapter.notifyDataSetChanged()
            imageColorPreview.setColorFilter(getColor(colors[it]))
        }
        textureAdapter.onItemSelected = {
            textureAdapter.selectedIndex = it
            textureAdapter.notifyDataSetChanged()
            sceneryAdapter.selectedIndex = -1
            sceneryAdapter.notifyDataSetChanged()
            imageBgOverlay.gone()
            imageBgPreview.setImageDrawable(textures[it])
        }
        sceneryAdapter.onItemSelected = {
            sceneryAdapter.selectedIndex = it
            sceneryAdapter.notifyDataSetChanged()
            textureAdapter.selectedIndex = -1
            textureAdapter.notifyDataSetChanged()
            imageBgPreview.setImageDrawable(sceneries[it])
            imageBgOverlay.show()
        }
        header.button1.setOnClickListener {
            finish()
        }
        textApply.setOnClickListener {
            toHome()
        }
        textColor.setOnClickListener {
            toStep(1)
        }
        textTexture.setOnClickListener {
            toStep(2)
        }
        textImage.setOnClickListener {
            toStep(3)
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
                .loadBanner(this@ThemeSettingActivity, getString(R.string.banner_ads_id))
        } else {
            include.visibility = View.GONE
        }
    }

    private fun toHome() {
        SPUtils.saveTheme(
            this@ThemeSettingActivity,
            if (colorAdapter.selectedIndex > -1) colorAdapter.selectedIndex else 0,
            textureAdapter.selectedIndex,
            sceneryAdapter.selectedIndex
        )
        startActivity(Intent(this, HomeActivity::class.java).apply {
            putExtra(EXTRA_THEME_UPDATE, true)
        })
        finish()
    }

    private fun toStep(step: Int) = with(viewBinding) {
        val greyColor =
            ContextCompat.getColor(this@ThemeSettingActivity, R.color.color_text_secondary)
        textColor.setTextColor(greyColor)
        textTexture.setTextColor(greyColor)
        textImage.setTextColor(greyColor)
        when (step) {
            1 -> {
                currentStep = step
                textColor.setTextColor(Color.BLACK)
                recyclerSceneries.gone()
                recyclerTextures.gone()
                recyclerColors.show()
            }
            2 -> {
                currentStep = step
                textTexture.setTextColor(Color.BLACK)
                recyclerColors.gone()
                recyclerTextures.show()
                recyclerSceneries.gone()
                textApply.show()
            }
            3 -> {
                currentStep = step
                textImage.setTextColor(Color.BLACK)
                recyclerColors.gone()
                recyclerTextures.gone()
                recyclerSceneries.show()
                textApply.show()
            }
        }
    }

    private fun getColorDrawable(color: Int) = ContextCompat.getColor(this, color).toDrawable()

    private fun getDrawableCompat(drawable: Int) = ContextCompat.getDrawable(this, drawable)!!

    private fun prepareInterTheme() {

//        isLoadingAds = true
        Admod.getInstance().getInterstitalAds(
            this@ThemeSettingActivity,
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
            toHome()
            return
        }

        if (!this.isInternetAvailable()) {
            isLoadingAds = false
            toHome()
            return
        }

        Admod.getInstance().setOpenActivityAfterShowInterAds(true)
        Admod.getInstance()
            .forceShowInterstitial(
                this@ThemeSettingActivity,
                interstitialCreateAd,
                object : AdCallback() {
                    override fun onAdClosed() {
                        isLoadingAds = false
                        toHome()
                    }

                    override fun onAdLeftApplication() {
                        super.onAdLeftApplication()
                        isLoadingAds = false
                        toHome()

                    }

                    override fun onAdFailedToLoad(i: LoadAdError?) {
                        super.onAdFailedToLoad(i)
                        isLoadingAds = false
                        toHome()
                    }
                })
    }
}
