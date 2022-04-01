package com.trustedapp.todolist.planner.reminders.screens.settings.policy

import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ads.control.ads.Admod
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseActivity
import com.trustedapp.todolist.planner.reminders.databinding.ActivityPolicyBinding
import com.trustedapp.todolist.planner.reminders.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class PolicyActivity : BaseActivity<ActivityPolicyBinding>() {
    override fun inflateViewBinding() = ActivityPolicyBinding.inflate(layoutInflater)

    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    override fun onActivityReady() {
        initViews()
        initData()
        setupEvents()
        obseverData()
    }

    private fun initViews() = with(viewBinding) {
        header.apply {
            button2.hide()
            button3.hide()
            button4.hide()
            button1.setBackgroundResource(R.drawable.ic_arrow_left)
            textTitle.text = getString(R.string.policy)
        }
        // Load Banner ads
//        loadBannerAds()
    }

    private fun loadBannerAds() = with(viewBinding) {
        if (Firebase.remoteConfig.getBoolean(SPUtils.KEY_BANNER) && isInternetAvailable()) {
            include.visibility = View.VISIBLE
            Admod.getInstance()
                .loadBanner(this@PolicyActivity, getString(R.string.banner_ads_id))
        } else {
            include.visibility = View.GONE
        }
    }

    private fun initData() = with(viewBinding.webView) {
        loadUrl(context.getString(R.string.url_policy))
    }

    private fun setupEvents() = with(viewBinding) {
        header.button1.setOnClickListener {
            finish()
        }
    }

    fun obseverData(){
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                NetworkState.isHasInternet.collect {
//                    loadBannerAds()
                }
            }
        }
    }
}