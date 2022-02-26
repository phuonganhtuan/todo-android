package com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.defaultnotificationringtone

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnPreparedListener
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseFragment
import com.trustedapp.todolist.planner.reminders.base.LoadDataState
import com.trustedapp.todolist.planner.reminders.data.models.entity.RingtoneEntity
import com.trustedapp.todolist.planner.reminders.data.models.entity.TODO_DEFAULT_RINGTONE_ID
import com.trustedapp.todolist.planner.reminders.databinding.FragmentDefautlNotificationRingtoneBinding
import com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.NotiReminderViewModel
import com.trustedapp.todolist.planner.reminders.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter

@AndroidEntryPoint
class DefautlNotificationRingtone : BaseFragment<FragmentDefautlNotificationRingtoneBinding>() {
    private val viewModel: NotiReminderViewModel by activityViewModels()
    private var adapter: DafaultNotificationRingtonAdapter? = null
    private var mediaPlayer: MediaPlayer = MediaPlayer()

    companion object {
        private const val READ_PERMISSION_CODE = 100
        private const val WRITE_PERMISSION_CODE = 101
    }

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentDefautlNotificationRingtoneBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        initView()
        initData()
        setupEvents()
        observeData()
    }

    private fun setupToolbar() = with(viewBinding.layoutTop) {
        textTitle.text = getString(R.string.default_notification_rington)
        button1.setImageResource(R.drawable.ic_arrow_left)
        button4.hide()
        button2.gone()
        button3.gone()
    }

    private fun initView() = with(viewBinding) {
        adapter = DafaultNotificationRingtonAdapter()
        recycleSystemRingtone.adapter = adapter
        recycleSystemRingtone.layoutManager = LinearLayoutManager(context)
        loadingBar.indeterminateTintList = ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorPrimary))
    }

    private fun initData() = with(viewBinding) {
        checkPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            READ_PERMISSION_CODE, {
                activity?.let { context?.let { it1 -> viewModel.getAllRington(it, it1) } }
            }
        )
    }

    private fun setupEvents() = with(viewBinding) {
        layoutTop.button1.setOnClickListener { findNavController().popBackStack() }
        adapter?.itemSelectListener = {
            viewModel.selectRingtoneEntity(it)
            playRingtone(it)
        }
        lnRecordRington.setOnClickListener {
            stopRingtone()
            findNavController().navigate(R.id.toRecordRingtone)
        }
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                listSystemRingtone.collect {
                    Log.e("observeData() - listSystemRingtone", it.toString())
                    if (it.isNotEmpty()) {
                        adapter?.submitList(it)
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectNotificationRingtone?.filter { it != null }?.collect {
                    adapter?.selectEntity = it
                    adapter?.notifyDataSetChanged()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                isLoading?.collect {
                    viewBinding.loadingBar.apply {
                        if (it == LoadDataState.LOADING) show() else gone()
                    }
                    viewBinding.recycleSystemRingtone.apply {
                        if (it == LoadDataState.LOADING || (it == LoadDataState.SUCCESS && listSystemRingtone?.value?.isEmpty() == true)) gone() else show()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRingtone()
    }

    override fun onResume() {
        super.onResume()
        stopRingtone()
    }

    private fun checkPermission(permission: String, requestCode: Int, callback: () -> Unit) {
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    permission
                )
            } != PackageManager.PERMISSION_GRANTED) {
            // Requesting the permission
            requestPermissions(arrayOf(permission), requestCode)
        } else {
            callback()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == READ_PERMISSION_CODE && (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED)
        ) {
            activity?.let { context?.let { it1 -> viewModel.getAllRington(it, it1) } }
        }
    }

    private fun stopRingtone(){
        if (mediaPlayer != null && mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
        }
    }

    private fun playRingtone(entity: RingtoneEntity) {
        try {
            if (mediaPlayer != null && mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            if (entity.id == TODO_DEFAULT_RINGTONE_ID) {
                mediaPlayer =
                    MediaPlayer.create(context?.getApplicationContext(), R.raw.to_do_default)

            } else {
                mediaPlayer =
                    MediaPlayer.create(context?.getApplicationContext(), entity.ringtoneUri)
            }

            mediaPlayer?.setOnPreparedListener {
                Handler().postDelayed(Runnable {
                    it.stop();
                } , it.duration.toLong())
                it.start()
            }
        } catch (e: Exception) {

        }

    }
}