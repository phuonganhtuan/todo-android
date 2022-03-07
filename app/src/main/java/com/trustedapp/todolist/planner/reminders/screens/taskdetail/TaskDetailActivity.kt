package com.trustedapp.todolist.planner.reminders.screens.taskdetail

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.ads.control.ads.Admod
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseActivity
import com.trustedapp.todolist.planner.reminders.databinding.ActivityTaskDetailBinding
import com.trustedapp.todolist.planner.reminders.screens.newtask.NewTaskViewModel
import com.trustedapp.todolist.planner.reminders.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class TaskDetailActivity : BaseActivity<ActivityTaskDetailBinding>() {

    private val viewModel: NewTaskViewModel by viewModels()

    override fun inflateViewBinding() = ActivityTaskDetailBinding.inflate(layoutInflater)

    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    override fun onActivityReady() {
        setupToolbar()
        initView()
        initData()
        setupEvents()
        observeData()
    }

    override fun onPause() {
        super.onPause()
        updateWidget()
    }

    private fun setupToolbar() = with(viewBinding.layoutTop) {
        button1.setImageResource(R.drawable.ic_arrow_left)
        button3.setImageResource(R.drawable.ic_edit)
        button4.setImageResource(R.drawable.ic_more)
        button2.hide()
    }

    private fun initView() = with(viewBinding) {
        loadBannerAds()
    }

    private fun loadBannerAds() = with(viewBinding){
        if (Firebase.remoteConfig.getBoolean(SPUtils.KEY_BANNER) && isInternetAvailable() == true) {
            include.visibility = View.VISIBLE
            Admod.getInstance()
                .loadBanner(this@TaskDetailActivity, getString(R.string.banner_ads_id))
        } else {
            include.visibility = View.GONE
        }
    }

    private fun initData() {
        val taskId = intent?.extras?.getInt(Constants.KEY_TASK_ID)
        taskId ?: finish()
        viewModel.initData(taskId!!)
    }

    private fun setupEvents() = with(viewBinding) {
        layoutTop.button1.setOnClickListener { finish() }
        layoutTop.button3.setOnClickListener {
            if (viewModel.isEditing.value) viewModel.toViewMode() else viewModel.toEditMode()
        }
        layoutTop.button4.setOnClickListener {
            showMoreMenu()
        }
    }

    private fun showMoreMenu() {
        val popup = PopupMenu(this, viewBinding.layoutTop.button4)
        popup.menuInflater.inflate(R.menu.task_detail_menu, popup.menu)
        val item = popup.menu.getItem(0)
        val item1 = popup.menu.getItem(1)
        val item2= popup.menu.getItem(2)
        val item3 = popup.menu.getItem(3)
        val item4 = popup.menu.getItem(4)
        val title =
            if (viewModel.task.value.task.isDone) R.string.mark_as_undone else R.string.mark_as_done
        val s = SpannableString(getStringByLocale(title))
        s.setSpan(
            ForegroundColorSpan(getColorFromAttr(R.attr.colorPrimary)), 0, s.length, 0
        )
        item.title = s
        item1.title = getStringByLocale(R.string.share)
        item2.title = getStringByLocale(R.string.delete)
        item3.title = getStringByLocale(R.string.duplicate_task)
        item4.title = getStringByLocale(R.string.bookmark)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.markAsDone -> markAsDone()
                R.id.share -> shareTask()
                R.id.delete -> deleteTask()
                R.id.duplicateTask -> duplicateTask()
                R.id.bookmark -> bookmark()
            }
            true
        }
        popup.show()
    }

    private fun markAsDone() {
        viewModel.markAsDone()
    }

    private fun shareTask() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Demo shared text")
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
    }

    private fun deleteTask() {
        findNavController(R.id.task_nav_host_fragment).navigate(R.id.toDeleteTask)
    }

    private fun duplicateTask() {
        viewModel.duplicateTask()
        showToastMessage(getString(R.string.duplicated))
    }

    private fun bookmark() {
        findNavController(R.id.task_nav_host_fragment).navigate(R.id.toBookmark)
    }

    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                isEditing.collect {
                    val icon = if (it) R.drawable.ic_done_grey else R.drawable.ic_edit
                    viewBinding.layoutTop.button3.setImageResource(icon)
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                isRemoved.collect {
                    if (it) finish()
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                validated.collect {
                    viewBinding.layoutTop.button3.apply {
                        if (!isEditing.value) {
                            isEnabled = true
                        } else {
                            if (it) show() else gone()
                        }
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                isAdded.collect {
                    if (it) {
//                        requestOverlayPermission()
                        showToastMessage(getString(R.string.saved))
                        finish()
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
}