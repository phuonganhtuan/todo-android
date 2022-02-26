package com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.defaultnotificationringtone

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseFragment
import com.trustedapp.todolist.planner.reminders.data.models.entity.RingtoneEntity
import com.trustedapp.todolist.planner.reminders.data.models.entity.RingtoneEntityTypeEnum
import com.trustedapp.todolist.planner.reminders.data.models.entity.TODO_DEFAULT_RINGTONE_ID
import com.trustedapp.todolist.planner.reminders.databinding.FragmentRecordRingtoneBinding
import com.trustedapp.todolist.planner.reminders.screens.settings.notireminder.NotiReminderViewModel
import com.trustedapp.todolist.planner.reminders.utils.gone
import com.trustedapp.todolist.planner.reminders.utils.hide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import java.io.File
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class RecordRingtone : BaseFragment<FragmentRecordRingtoneBinding>() {
    private val viewModel: NotiReminderViewModel by activityViewModels()
    private var output: String? = null
    private var state: Boolean = false
    private val mediaRecorder: MediaRecorder by lazy {
        MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(output)
            setAudioEncodingBitRate(384000)
            setAudioSamplingRate(44100)
        }
    }
    private var startHTime = 0L
    private val customHandler = Handler(Looper.getMainLooper())
    var timeInMilliseconds = 0L
    var timeSwapBuff = 0L
    var updatedTime = 0L

    @Inject
    lateinit var adapter: RecordRingtoneAdapter
    private var mediaPlayer: MediaPlayer = MediaPlayer()

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentRecordRingtoneBinding {
        return FragmentRecordRingtoneBinding.inflate(layoutInflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        initView()
        initData()
        setupEvents()
        observeData()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRecording()
        stopRingtone()
    }

    private fun setupToolbar() = with(viewBinding.layoutTop) {
        textTitle.text = getString(R.string.default_notification_rington)
        button1.setImageResource(R.drawable.ic_arrow_left)
        button4.hide()
        button2.gone()
        button3.gone()
    }

    private fun initView() = with(viewBinding) {
        recycleSystemRingtone.adapter = adapter
    }

    private fun initData() {
        context?.let { viewModel.getAllRecord(it) }
    }

    private fun setupEvents() = with(viewBinding) {
        imgRecord.setOnClickListener {
            viewModel.setRecording(!viewModel.isRecording.value)
        }
        adapter.itemSelectListener = {
            viewModel.selectRingtoneEntity(it)
        }
        adapter.imgPlayListener = {
            playRingtone(it)
        }
        adapter.imgDeleteListener = {
            viewModel.removeRecord(requireContext(), it)
        }
        layoutTop.button1.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                isRecording.collect {
                    viewBinding.imgRecord.setImageResource(if (it) R.drawable.ic_recording_no_bg else R.drawable.ic_mic)
                    if (it) {
                        checkPermission()
                    } else {
                        stopRecording()
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                listRecord.collect {
                    if (it.isNotEmpty()) {
                        adapter.submitList(it)
                    }
                    val absolutePath =
                        requireContext().getExternalFilesDir(null)?.absolutePath + "/TodoRecord"
                    if (File(absolutePath).mkdirs()) {
                    }
                    output =
                        absolutePath + "/Ringtone_${it.count() + 1}.mp3"

                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectNotificationRingtone.filter { it != null }.collect {
                    adapter.selectEntity = it
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    private val REQUEST_CODE = 0

    private fun checkPermission() {
        context.let {
            if (it != null && ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions = arrayOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                activity?.let { it1 ->
                    ActivityCompat.requestPermissions(
                        it1,
                        permissions,
                        REQUEST_CODE
                    )
                }
            } else {
                startRecording()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE && (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED)
        ) {
            startRecording()
        }
    }

    private fun startRecording() {
        try {
            stopRingtone()
            val file = File(output ?: "")
            file.createNewFile()
            mediaRecorder.apply {
                setOutputFile(output)
                prepare()
                start()
            }
            state = true
            startHTime = SystemClock.uptimeMillis()
            customHandler.removeCallbacksAndMessages(null)
            customHandler.postDelayed(updateTimerThread, 0)
            viewBinding.apply {
                content.startRippleAnimation()
                textRecord.text = getString(R.string.recording)
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        if (state) {
            mediaRecorder.stop()
            mediaRecorder.release()
            state = false
            timeSwapBuff += timeInMilliseconds
            customHandler.removeCallbacks(updateTimerThread)
            if (output?.isNotEmpty() == true) {
                val entity = RingtoneEntity(
                    viewModel.listRecord.value.count() + 1,
                    output!!.substring(output!!.lastIndexOf("/") + 1),
                    Uri.fromFile(File(output)),
                    RingtoneEntityTypeEnum.RECORD
                )
                viewModel.addRecord(entity)
                Handler(Looper.getMainLooper()).postDelayed({
                    viewBinding.recycleSystemRingtone.smoothScrollToPosition(0)
                }, 200)
            }
            viewBinding.apply {
                content.stopRippleAnimation()
                tvDuration.text = "00:00"
                textRecord.text = getString(R.string.start_record)
            }
        }
    }

    private fun playRingtone(entity: RingtoneEntity) {
        try {
            stopRecording()
            stopRingtone()
            mediaPlayer = if (entity.id == TODO_DEFAULT_RINGTONE_ID) {
                MediaPlayer.create(context?.applicationContext, R.raw.to_do_default)

            } else {
                MediaPlayer.create(context?.applicationContext, entity.ringtoneUri)
            }

            mediaPlayer.setOnPreparedListener {
                Handler().postDelayed(Runnable {
                    it.stop()
                }, it.duration.toLong())
                it.start()
            }
        } catch (e: Exception) {

        }
    }

    private fun stopRingtone() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
    }

    private val updateTimerThread: Runnable = object : Runnable {
        override fun run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startHTime
            updatedTime = timeSwapBuff + timeInMilliseconds
            var secs = (updatedTime / 1000).toInt()
            val mins = secs / 60
            secs %= 60
            if (viewBinding.tvDuration != null) viewBinding.tvDuration.text =
                "" + String.format("%02d", mins) + ":" + String.format("%02d", secs)
            customHandler.postDelayed(this, 1000)
        }
    }
}