package com.trustedapp.todolist.planner.reminders.screens.theme

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseActivity
import com.trustedapp.todolist.planner.reminders.databinding.ActivityThemeBinding
import com.trustedapp.todolist.planner.reminders.screens.home.HomeActivity
import com.trustedapp.todolist.planner.reminders.utils.SPUtils
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.show
import dagger.hilt.android.AndroidEntryPoint
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

    override fun inflateViewBinding() = ActivityThemeBinding.inflate(layoutInflater)

    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    override fun onActivityReady() {
        initViews()
        initData()
        setupEvents()
    }

    private fun initViews() = with(viewBinding) {
        recyclerColors.adapter = colorAdapter
        recyclerTextures.adapter = textureAdapter
        recyclerSceneries.adapter = sceneryAdapter
        toStep(1)
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
            SPUtils.saveTheme(this@ThemeActivity, 0, -1, -1)
            toHome()
        }
        textApply.setOnClickListener {
            SPUtils.saveTheme(
                this@ThemeActivity,
                colorAdapter.selectedIndex,
                textureAdapter.selectedIndex,
                sceneryAdapter.selectedIndex
            )
            toHome()
        }
    }

    private fun toHome() {
        startActivity(Intent(this@ThemeActivity, HomeActivity::class.java))
    }

    private fun toStep(step: Int) = with(viewBinding) {
        when (step) {
            1 -> {
                currentStep = step
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

    private fun getColorDrawable(color: Int) = ContextCompat.getColor(this, color).toDrawable()

    private fun getDrawableCompat(drawable: Int) = ContextCompat.getDrawable(this, drawable)!!
}
