package com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.dailyreminderringtone

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseDialogFragment
import com.trustedapp.todolist.planner.reminders.base.LoadDataState
import com.trustedapp.todolist.planner.reminders.data.models.entity.RingtoneEntity
import com.trustedapp.todolist.planner.reminders.data.models.entity.TODO_DEFAULT_RINGTONE_ID
import com.trustedapp.todolist.planner.reminders.databinding.FragmentDailyReminderRingtoneBinding
import com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.NotiReminderViewModel
import com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.defaultnotificationringtone.DafaultNotificationRingtonAdapter
import com.trustedapp.todolist.planner.reminders.utils.getColorFromAttr
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DailyReminderRingtone : BaseDialogFragment<FragmentDailyReminderRingtoneBinding>() {
    private var adapter: DafaultNotificationRingtonAdapter? = null
    private val viewModel: NotiReminderViewModel by activityViewModels()
    private lateinit var selItem: RingtoneEntity
    private var mediaPlayer: MediaPlayer = MediaPlayer()

    companion object {
        private const val READ_PERMISSION_CODE = 100
    }

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentDailyReminderRingtoneBinding {
        return FragmentDailyReminderRingtoneBinding.inflate(layoutInflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        setupEvents()
        observeData()
    }

    private fun initView() = with(viewBinding) {
        adapter = DafaultNotificationRingtonAdapter()
        recycleReminderRingtone.adapter = adapter
        recycleReminderRingtone.layoutManager = LinearLayoutManager(context)
        loadingBar.indeterminateTintList =
            ColorStateList.valueOf(requireContext().getColorFromAttr(R.attr.colorPrimary))
    }

    private fun initData() {
        viewModel.selectDailyRingtone.value.let {
            if (it != null) {
                selItem = it
                adapter?.selectEntity = selItem
            }
        }
        if (viewModel.listSystemRingtone.value.count() == 0) {
            checkPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                READ_PERMISSION_CODE
            ) {
                activity?.let {
                    context?.let { it1 ->
                        viewModel.getAllRington(it, it1)
                    }
                }
            }
        }
    }

    private fun setupEvents() = with(viewBinding) {
        adapter?.itemSelectListener = {
            selItem = it
            adapter?.selectEntity = selItem
            adapter?.notifyDataSetChanged()
            playRingtone(selItem)
        }
        btnDone.setOnClickListener {
            viewModel.selectDailyRingtoneEntity(selItem)
            dismiss()
        }
    }

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
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                isLoading.collect {
                    viewBinding.loadingBar.apply {
                        if (it == LoadDataState.LOADING) show() else gone()
                    }
                    viewBinding.recycleReminderRingtone.apply {
                        if (it == LoadDataState.LOADING || (it == LoadDataState.SUCCESS && listSystemRingtone.value.isEmpty())) gone() else show()
                    }
                }
            }
        }
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
            activity?.let {
                context?.let { it1 ->
                    viewModel.getAllRington(it, it1)
                }
            }
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
                }, it.duration.toLong())
                it.start()
            }
        } catch (e: Exception) {

        }

    }
}