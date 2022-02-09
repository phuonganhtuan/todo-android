package com.example.todo.screens.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.todo.R
import com.example.todo.base.BaseActivity
import com.example.todo.databinding.ActivityHomeBinding
import com.example.todo.screens.home.tasks.suggest.SuggestActivity
import com.example.todo.utils.SPUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>() {

    private val viewModel: HomeViewModel by viewModels()

    override fun inflateViewBinding() = ActivityHomeBinding.inflate(layoutInflater)

    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    override fun onActivityReady() {
        if (SPUtils.isFirstTime(this)) {
            viewModel.createInitData()
            SPUtils.saveFirstTimeLaunched(this)
            showSuggest()
        }
        setupEvents()
        requestPermissions()
    }

    fun openDrawer() {
        viewBinding.layoutDrawer.open()
    }

    private fun showSuggest() {
        startActivity(Intent(this, SuggestActivity::class.java))
    }

    private fun setupEvents() = with(viewBinding) {
        bottomBar.setupWithNavController(findNavController(R.id.home_nav_host_fragment))
//        (navigationSideView.getHeaderView(0)
//            .findViewById(R.id.buttonHide) as ImageView).setOnClickListener {
//            layoutDrawer.close()
//        }
    }

    private fun requestPermissions() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + packageName)
            )
            startActivityForResult(intent, 0)
        }
    }
}
