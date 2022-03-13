package com.trustedapp.todolist.planner.reminders.screens.newtask

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ads.control.ads.Admod
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseActivity
import com.trustedapp.todolist.planner.reminders.databinding.ActivityNewTaskBinding
import com.trustedapp.todolist.planner.reminders.screens.home.HomeActivity
import com.trustedapp.todolist.planner.reminders.screens.settings.rating.RatingDialogFragment
import com.trustedapp.todolist.planner.reminders.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class NewTaskActivity : BaseActivity<ActivityNewTaskBinding>() {

    override fun inflateViewBinding() = ActivityNewTaskBinding.inflate(layoutInflater)

    private val viewModel: NewTaskViewModel by viewModels()

    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    override fun onActivityReady() {
        setupToolbar()
        initView()
        setupEvents()
        observeData()
    }

    private fun initView() = with(viewBinding) {
        // Load Banner ads
        loadBannerAds()
    }

    private fun loadBannerAds() = with(viewBinding) {
        if (Firebase.remoteConfig.getBoolean(SPUtils.KEY_BANNER) && isInternetAvailable()) {
            include.visibility = View.VISIBLE
            Admod.getInstance()
                .loadBanner(this@NewTaskActivity, getString(R.string.banner_ads_id))
        } else {
            include.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        updateWidget()
    }


    private fun setupToolbar() = with(viewBinding.layoutTop) {
        button1.setImageResource(R.drawable.ic_arrow_left)
        button4.hide()
        button2.hide()
        button3.hide()
    }

    private fun setupEvents() = with(viewBinding) {
        layoutTop.button1.setOnClickListener { finish() }
    }

    private fun isShowRating(): Boolean {
        try {
            val numberOfNewTask = SPUtils.getNumberNewTask(this)
            var numberAppearRating =
                Firebase.remoteConfig.getString(SPUtils.KEY_RATING_APPEAR_NUMBER)
            var numberAppearRatingArr = numberAppearRating.split(",")
            return numberAppearRatingArr.contains(numberOfNewTask.toString())
        } catch (e: Exception) {
            return false
        }
    }

    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                isAdded.collect {
                    if (it) {
//                        requestOverlayPermission()
                        showToastMessage(getString(R.string.added_task))
                        if (isShowRating()) {
                            backToHomeAndShowRate()
                        } else {
                            finish()
                        }
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                NetworkState.isHasInternet.collect {
                    loadBannerAds()
                }
            }
        }
    }

    fun backToHomeAndShowRate() {
        val previousActivity = Intent(
            this,
            HomeActivity::class.java
        ).apply {
            putExtra(Constants.EXRA_APPEAR_RATE, true)
        }
        previousActivity.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(previousActivity)
    }
}