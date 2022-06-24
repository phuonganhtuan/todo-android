package com.trustedapp.todolist.planner.reminders.screens.home.mine

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
import com.ads.control.ads.Admod
import com.ads.control.funtion.AdCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseFragment
import com.trustedapp.todolist.planner.reminders.databinding.FragmentMineBinding
import com.trustedapp.todolist.planner.reminders.screens.taskdetail.TaskDetailActivity
import com.trustedapp.todolist.planner.reminders.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class MineFragment : BaseFragment<FragmentMineBinding>() {

    private val viewModel: MineViewModel by viewModels()

    @Inject
    lateinit var chartAdapter: CatStatisticAdapter

    @Inject
    lateinit var taskAdapter: MineTaskAdapter

    private var nativeAds: NativeAd? = null

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMineBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupEvents()
        observeData()
        FirebaseLog.logEventMineScreen()
    }

    private fun initViews() = with(viewBinding) {
        recyclerNext7Days.adapter = taskAdapter
        recyclerChart.adapter = chartAdapter
    }

    private fun setupEvents() = with(viewBinding) {
        taskAdapter.onTaskClickListener = {
            startActivity(Intent(requireContext(), TaskDetailActivity::class.java).apply {
                putExtra(Constants.KEY_TASK_ID, it)
            })
        }
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                next7DaysTasks.collect {
                    taskAdapter.submitList(it)
                    if (it.isEmpty()) {
                        viewBinding.imageNoTask.show()
                    } else {
                        viewBinding.imageNoTask.gone()
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                completedTasksCount.collect {
                    viewBinding.textDoneNum.text = it.toString()
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                pendingTasksCount.collect {
                    viewBinding.textPendingNum.text = it.toString()
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                chartData.collect {
                    chartAdapter.submitList(it)
                    viewBinding.chart.setData(it.map { item -> item.percent })
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                tasksCount.collect {
                    viewBinding.apply {
                        if (it == 0) {
                            chart.gone()
                            recyclerChart.gone()
                            viewIndicatorBottom.gone()
                        } else {
                            chart.show()
                            recyclerChart.show()
                            viewIndicatorBottom.show()
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
    }

    private fun loadAds(isPrepare: Boolean = false) = with(viewBinding) {

        if (!FirebaseRemoteConfig.getInstance().getBoolean(SPUtils.KEY_NATIVE_MINE)) {
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
                getString(R.string.native_mine_ads_id),
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
        layoutShimmerSmall.shimmerContainerNativeSmall.startShimmer()
        if (nativeAds != null && !isPrepare) {
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    layoutShimmerSmall.shimmerContainerNativeSmall.stopShimmer()
                    layoutShimmerSmall.shimmerContainerNativeSmall.visibility = View.GONE
                    Admod.getInstance()
                        .populateUnifiedNativeAdView(
                            nativeAds,
                            layoutNativeAdsSmall.viewAdUnified
                        )
                    layoutNativeAdsSmall.viewAdUnified.visibility = View.VISIBLE
                } catch (ex: Exception) {

                }

            }, 500)
        }
    }

    fun updateAdsNativeWhenConnectInternet(isInternet: Boolean) = with(viewBinding) {
        if (isInternet) {
            if (nativeAds != null) {
                updateNativeAdsView(true)
                updateNativeAdsView(false)
            } else {
                updateNativeAdsView(true)
                loadAds(false)
            }
        } else {
            containerNativeAdSmall.gone()
        }
    }
}
