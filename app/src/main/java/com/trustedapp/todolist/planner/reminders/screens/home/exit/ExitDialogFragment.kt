package com.trustedapp.todolist.planner.reminders.screens.home.exit

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ads.control.ads.Admod
import com.ads.control.funtion.AdCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseDialogFragment
import com.trustedapp.todolist.planner.reminders.databinding.FragmentExitDialogBinding
import com.trustedapp.todolist.planner.reminders.screens.home.HomeViewModel
import com.trustedapp.todolist.planner.reminders.utils.SPUtils
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.isInternetAvailable
import com.trustedapp.todolist.planner.reminders.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ExitDialogFragment : BaseDialogFragment<FragmentExitDialogBinding>() {
    private val viewModel: HomeViewModel by viewModels()

    var exitNativeAd: NativeAd? = null

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentExitDialogBinding {
        return FragmentExitDialogBinding.inflate(layoutInflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        setEvents()
        observeData()
    }

    fun initView() = with(viewBinding) {
        if (exitNativeAd != null) {
            viewBinding.flAdplaceholder.show()
            viewBinding.layoutShimmerframe.shimmerContainerNative.stopShimmer()
            viewBinding.layoutShimmerframe.shimmerContainerNative.gone()
            viewBinding.layoutAds.layoutExitAds.show()
            Admod.getInstance()
                .populateUnifiedNativeAdView(exitNativeAd, viewBinding.layoutAds.layoutExitAds)
        } else {
            viewBinding.layoutShimmerframe.shimmerContainerNative.gone()
            viewBinding.frameContent.gone()
            viewBinding.flAdplaceholder.gone()
        }
    }

    fun initData() = with(viewBinding) {

    }

    fun setEvents() = with(viewBinding) {
        btnExit.setOnClickListener {
            dismiss()
            activity?.finish()
        }
    }

    fun observeData() = with(viewModel) {

    }
}