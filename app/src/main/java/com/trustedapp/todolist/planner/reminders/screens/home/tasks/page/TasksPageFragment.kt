package com.trustedapp.todolist.planner.reminders.screens.home.tasks.page

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ads.control.ads.Admod
import com.ads.control.funtion.AdCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.appstate.AppState
import com.trustedapp.todolist.planner.reminders.base.BaseFragment
import com.trustedapp.todolist.planner.reminders.data.models.model.TaskPageType
import com.trustedapp.todolist.planner.reminders.databinding.FragmentTasksPageBinding
import com.trustedapp.todolist.planner.reminders.screens.taskdetail.TaskDetailActivity
import com.trustedapp.todolist.planner.reminders.utils.*
import com.trustedapp.todolist.planner.reminders.utils.Constants.KEY_TASK_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.lang.Exception
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TasksPageFragment : BaseFragment<FragmentTasksPageBinding>() {

    private val viewModel: TasksPageViewModel by viewModels()

    private var type = TaskPageType.TODAY

    private var nativeAds: NativeAd? = null

    @Inject
    lateinit var adapter: TaskAdapter

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentTasksPageBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initViews()
        observeData()
        setupEvents()
    }

    override fun onStart() {
        super.onStart()
        adapter.notifyDataSetChanged()
    }

    private fun initViews() = with(viewBinding) {
        recyclerTasks.adapter = adapter
        if (viewModel.type == TaskPageType.TODAY) {
            adapter.isHideDay = true
        }
    }

    private fun initData() {
        if (viewModel.type == null) viewModel.type = type
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                when (type) {
                    TaskPageType.TODAY -> {
                        todayTasks.collect {
                            adapter.submitList(it)
                            viewBinding.textTaskCount.text =
                                "${requireContext().getStringByLocale(R.string.today_task)} (${it.count()})"
                            showOrHideNoTask(it.isNullOrEmpty())
                        }
                    }
                    TaskPageType.FUTURE -> {
                        futureTasks.collect {

                            adapter.submitList(it)
                            viewBinding.textTaskCount.text =
                                "${requireContext().getStringByLocale(R.string.future_task)} (${it.count()})"
                            showOrHideNoTask(it.isNullOrEmpty())
                        }
                    }
                    TaskPageType.DONE -> {
                        doneTasks.collect {
                            adapter.submitList(it)
                            viewBinding.textTaskCount.text =
                                "${requireContext().getStringByLocale(R.string.done_task)} (${it.count()})"
                            showOrHideNoTask(it.isNullOrEmpty())
                        }
                    }
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            NetworkState.isHasInternet.collect {
//                    loadBannerAds()
                updateAdsNativeWhenConnectInternet(it)
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                AppState.isCreatedTask.collect {
                    if (it) {
                        try {
                            Handler(Looper.getMainLooper()).postDelayed({
                                viewBinding.recyclerTasks.scrollToPosition(0)
                            }, 200)
                        } catch (exeption: Exception) {

                        }
                        AppState.setIsCreatedTask(false)
                    }

                }
            }
        }
    }

    fun updateAdsNativeWhenConnectInternet(isInternet: Boolean) = with(viewBinding) {
        if (isInternet) {
            if (nativeAds != null) {
                updateNativeAdsView(false)
            } else {
                loadAds(false)
            }
        } else {
            containerNativeAdSmall.gone()
        }
    }

    private fun showOrHideNoTask(isShow: Boolean) = with(viewBinding) {
        if (isShow) {
            imageNoTask.show()
            textTaskCount.gone()
        } else {
            imageNoTask.gone()
            textTaskCount.show()
        }
    }

    private fun setupEvents() = with(viewBinding) {
        adapter.setOnTaskListener(object : OnTaskInteract {
            override fun onItemClick(id: Int) {
                startActivity(Intent(requireContext(), TaskDetailActivity::class.java).apply {
                    putExtra(KEY_TASK_ID, id)
                })
            }

            override fun onMarkChange(id: Int) {
                viewModel.updateMark(id)
            }

            override fun onStatusChange(id: Int) {
                viewModel.updateStatus(requireContext(), id)
            }
        })
    }

    private fun loadAds(isPrepare: Boolean = false) = with(viewBinding) {

        if (!FirebaseRemoteConfig.getInstance().getBoolean(SPUtils.KEY_NATIVE_TASK)) {
            containerNativeAdSmall.gone()
            return@with
        }
        if (!context?.isInternetAvailable()!!) {
            containerNativeAdSmall.gone()
            return@with
        }

        Admod.getInstance()
            .loadNativeAd(
                activity,
                getString(R.string.native_task_ads_id),
                object : AdCallback() {
                    override fun onAdClosed() {
                        super.onAdClosed()
                        updateNativeAdsView(isPrepare)
                    }

                    override fun onAdFailedToLoad(i: LoadAdError?) {
                        super.onAdFailedToLoad(i)
                        updateNativeAdsView(isPrepare)
                    }

                    override fun onUnifiedNativeAdLoaded(unifiedNativeAd: NativeAd?) {
                        super.onUnifiedNativeAdLoaded(unifiedNativeAd)
                        nativeAds = unifiedNativeAd
                        updateNativeAdsView(isPrepare)
                    }
                })
    }

    fun updateNativeAdsView(isPrepare: Boolean) = with(viewBinding) {
        containerNativeAdSmall.visibility = View.VISIBLE
        layoutNativeAdsSmall.viewAdUnified.visibility = View.GONE
        layoutShimmerSmall.shimmerContainerNativeSmall.visibility = View.VISIBLE
        if (nativeAds != null && !isPrepare) {
            Handler(Looper.getMainLooper()).postDelayed({
                layoutShimmerSmall.shimmerContainerNativeSmall.stopShimmer()
                layoutShimmerSmall.shimmerContainerNativeSmall.visibility = View.GONE
                Admod.getInstance()
                    .populateUnifiedNativeAdView(
                        nativeAds,
                        layoutNativeAdsSmall.viewAdUnified
                    )
                layoutNativeAdsSmall.viewAdUnified.visibility = View.VISIBLE
            }, 500)
        }
    }

    companion object {
        fun newInstance(taskType: TaskPageType) = TasksPageFragment().apply {
            type = taskType
        }
    }
}
