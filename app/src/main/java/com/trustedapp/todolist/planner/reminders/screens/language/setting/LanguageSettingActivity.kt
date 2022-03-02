package com.trustedapp.todolist.planner.reminders.screens.language.setting

import android.content.IntentFilter
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseActivity
import com.trustedapp.todolist.planner.reminders.databinding.ActivityLanguageSettingBinding
import com.trustedapp.todolist.planner.reminders.screens.language.LanguageModel
import com.trustedapp.todolist.planner.reminders.utils.NetworkChangeReceiver
import com.trustedapp.todolist.planner.reminders.utils.NetworkState
import com.trustedapp.todolist.planner.reminders.utils.SPUtils
import com.trustedapp.todolist.planner.reminders.utils.hide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class LanguageSettingActivity : BaseActivity<ActivityLanguageSettingBinding>() {

    override fun inflateViewBinding() = ActivityLanguageSettingBinding.inflate(layoutInflater)

    private var langList = mutableListOf<LanguageModel>()

    private val networkReceiver = NetworkChangeReceiver()

    @Inject
    lateinit var adapter: LanguageSettingAdapter

    override fun onActivityReady(savedInstanceState: Bundle?) {

    }

    override fun onActivityReady() {
        initViews()
        initData()
        setupEvents()
        observeData()
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(networkReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkReceiver)
    }

    private fun initViews() = with(viewBinding) {
        header.apply {
            button2.hide()
            button3.hide()
            button4.hide()
            button1.setBackgroundResource(R.drawable.ic_arrow_left)
            textTitle.text = getString(R.string.language)
        }
        recyclerLanguage.adapter = adapter
    }

    private fun observeData() {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                NetworkState.isHasInternet.collect {
                    adapter.enableAds = it
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun initData() {
        langList = mutableListOf(
            LanguageModel(
                id = 0,
                langName = "System default",
                langCode = ""
            ),
            LanguageModel(
                id = 1,
                langName = "Deutsch",
                langCode = "de"
            ),
            LanguageModel(
                id = 2,
                langName = "Enspanol",
                langCode = "es"
            ),
            LanguageModel(
                id = 3,
                langName = "Indonesia",
                langCode = "id"
            ),
            LanguageModel(
                id = 4,
                langName = "English",
                langCode = "en"
            ),
            LanguageModel(
                id = 5,
                langName = "Filipino",
                langCode = "fil"
            ),
            LanguageModel(
                id = 6,
                langName = "France",
                langCode = "fr"
            ),
            LanguageModel(
                id = 7,
                langName = "Japan",
                langCode = "ja"
            ),
            LanguageModel(
                id = 8,
                langName = "China",
                langCode = "zh"
            ),
            LanguageModel(
                id = 1,
                langName = "Portugal",
                langCode = "pt"
            ),
            LanguageModel(
                id = 4,
                langName = "Índia",
                langCode = "hi"
            ),
        )

        val langCode = langList.map { it.langCode }

        val currentLanguage = SPUtils.getCurrentLang(this)
        if (currentLanguage.isNullOrEmpty()) {
            val deviceLanguage = Locale.getDefault().language
            val isSupported = langCode.contains(deviceLanguage)
            if (isSupported) {
                val selectedLangIndex = langCode.indexOf(deviceLanguage)
                if (selectedLangIndex >= 0) adapter.selectedItem = selectedLangIndex
            } else {
                adapter.selectedItem = 4 // en by default
            }
        } else {
            adapter.selectedItem = langCode.indexOf(currentLanguage)
        }

        adapter.submitList(langList)
    }

    private fun setupEvents() = with(viewBinding) {
        header.button1.setOnClickListener {
            finish()
        }
        adapter.onItemSelected = {
            adapter.selectedItem = it
            adapter.notifyDataSetChanged()
            applyLanguage(langList[it].langCode)
        }
    }
}