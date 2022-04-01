package com.trustedapp.todolist.planner.reminders.screens.newtask

import android.annotation.SuppressLint
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.ads.control.ads.Admod
import com.ads.control.funtion.AdCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseFragment
import com.trustedapp.todolist.planner.reminders.data.models.entity.CategoryEntity
import com.trustedapp.todolist.planner.reminders.databinding.FragmentNewTaskBinding
import com.trustedapp.todolist.planner.reminders.screens.newtask.category.OnCatInteractListener
import com.trustedapp.todolist.planner.reminders.screens.newtask.category.SelectCategoryAdapter
import com.trustedapp.todolist.planner.reminders.screens.newtask.subtask.OnSubTaskInteract
import com.trustedapp.todolist.planner.reminders.screens.newtask.subtask.SubTaskAdapter
import com.trustedapp.todolist.planner.reminders.screens.taskdetail.attachment.AttachmentAdapter
import com.trustedapp.todolist.planner.reminders.utils.*
import com.trustedapp.todolist.planner.reminders.utils.helper.getCatName
import com.trustedapp.todolist.planner.reminders.utils.helper.getCategoryColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class NewTaskFragment : BaseFragment<FragmentNewTaskBinding>() {

    private val viewModel: NewTaskViewModel by activityViewModels()

    private var categoriesPopup: PopupWindow? = null

    @Inject
    lateinit var subTaskAdapter: SubTaskAdapter

    @Inject
    lateinit var categoryAdapter: SelectCategoryAdapter

    @Inject
    lateinit var attachmentAdapter: AttachmentAdapter

    private var isLoadingAds: Boolean = false
    private var interstitialCreateAd: InterstitialAd? = null
    private var nativeAds: NativeAd? = null

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentNewTaskBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initData()
        setupEvents()
        observeData()
        prepareInterCreate()
    }

    private fun initData() {
        viewModel.setIsNewTask(requireContext(), true)
        viewModel.addSubTask()
        validateTask()
    }

    private fun initViews() = with(viewBinding) {
        recyclerSubTasks.adapter = subTaskAdapter
        recyclerAttachment.adapter = attachmentAdapter
        recyclerAttachment.itemAnimator = null
        hideDateTime()
    }

    private fun setupEvents() = with(viewBinding) {
        buttonNewSubTask.setOnClickListener {
            viewModel.addSubTask()
        }
        buttonAddCalendar.setOnClickListener {
            findNavController().navigate(R.id.toAddCalendar)
        }
        buttonAttachment.setOnClickListener {
            findNavController().navigate(R.id.toSelectAttachment)
        }
        textCategory.setOnClickListener {
            categoriesPopup?.showAsDropDown(it, -50, -20)
        }
        editTaskName.boldWhenFocus()
        editNote.boldWhenFocus()
        subTaskAdapter.setOnTaskListener(object : OnSubTaskInteract {
            override fun onTitleChanged(index: Int, title: String) {
                viewModel.updateSubTask(index, title)
            }
        })
        categoryAdapter.setOnCatListener(object : OnCatInteractListener {
            override fun onCatClick(index: Int) {
                if (index == viewModel.categories.value.size) {
                    createNewCategory()
                } else {
                    viewModel.selectCat(index)
                }
                categoriesPopup?.dismiss()
            }
        })
        switchRepeat.setOnClickListener {
            onCheckChangeRepeat()
        }
        switchReminder.setOnClickListener {
            onCheckChangeReminder()
        }
//        buttonCreateTask.setOnClickListener {
//            loadInterCreate()
//        }
        editTaskName.addTextChangedListener {
            validateTask()
        }
        attachmentAdapter.onAttachmentRemoveListener = {
            viewModel.removeAttachment(it)
        }
        attachmentAdapter.onAttachmentClickListener = {
            val attachment = viewModel.attachments.value[it]
            FileUtils.openAttachment(requireContext(), attachment)
        }
        hideKeyboardTouchOutside(layoutNewTask)
    }

    private fun validateTask() {
        viewModel.validate(viewBinding.editTaskName.text.toString().trim())
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                subtasks.collect {
                    validateTask()
                    subTaskAdapter.submitList(it)
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                canAddSubTask.collect {
                    validateTask()
                    if (it) viewBinding.buttonNewSubTask.show() else viewBinding.buttonNewSubTask.gone()
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectedCatIndex.collect {
                    validateTask()
                    viewBinding.apply {
                        if (it == -1) {
                            textCategory.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.color_text_secondary_dark
                                )
                            )
                            textCategory.text = getString(R.string.no_category)
                        } else {
                            textCategory.setTextColor(
                                getCategoryColor(
                                    requireContext(),
                                    categories.value[it]
                                )
                            )
                            textCategory.text =
                                getCatName(requireContext(), categories.value[it].name)
                        }
                    }
                    categoryAdapter.selectedIndex = it
                    categoryAdapter.notifyDataSetChanged()
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                categories.collect {
                    val catIdSum = it.sumOf { cat -> cat.id }
                    val listCats = it.toMutableList().apply {
                        add(
                            CategoryEntity(
                                catIdSum,
                                name = getString(R.string.create_new_category)
                            )
                        )
                    }
                    categoryAdapter.submitList(listCats)
                    setupCatsPopup(listCats)
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectedDate.collect {
                    validateTask()
                    if (_hasTime.value)
                        viewBinding.buttonAddCalendar.text =
                            DateTimeUtils.getComparableDateString(it)
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectedHour.filter { it > -1 }
                    .combine(selectedMinute.filter { it > -1 }) { hour, minute ->
                        val date = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, hour)
                            set(Calendar.MINUTE, minute)
                        }
                        DateTimeUtils.getHourMinuteFromMillisecond(date.timeInMillis)
                    }.collect {
                        _hasTime.value = true
                        viewBinding.buttonAddCalendar.text =
                            DateTimeUtils.getComparableDateString(selectedDate.value)
                        validateTask()
                        showDateTime()
                        viewBinding.textTime.text = it
                    }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                customReminderTime.filter { it > 0 }
                    .combine(customReminderTimeUnit) { time, unit ->
                        if (time > 0) "${time.toString()} ${
                            resources.getString(unit.getStringid()).lowercase()
                        } ${resources.getString(R.string.before).lowercase()}" else ""
                    }.filter { it.isNotEmpty() }
                    .combine(selectedReminderTime.filter { it != ReminderTimeEnum.NONE }) { customValue, slReminder ->
                        if (slReminder == ReminderTimeEnum.CUSTOM_DAY_BEFORE) customValue else resources.getString(
                            slReminder.getStringid()
                        )
                    }.collect {
                        viewBinding.textReminderTime.text = it
                    }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectedReminderTime.filter { it != ReminderTimeEnum.NONE }
//                    .filter { viewModel.isCheckedReminder.value == true }
                    .collect {
                        val text =
                            if (it == ReminderTimeEnum.CUSTOM_DAY_BEFORE) "${customReminderTime.value} ${
                                resources.getString(customReminderTimeUnit.value.getStringid())
                                    .lowercase()
                            } ${
                                resources.getString(R.string.before).lowercase()
                            }" else resources.getString(it.getStringid())
                        viewBinding.apply {
                            textReminderTime.text = text
                        }

                    }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                isCheckedReminder.collect {
                    viewBinding.switchReminder.isChecked = it
                    if (it) viewBinding.textReminderTime.show()
                    if (!it) {
                        viewBinding.textReminderTime.gone()
//                        viewBinding.textRepeatTime.gone()
//                        viewModel.onCheckChangeRepeat(false)
//                        viewModel.resetRepeatDefault()
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectedRepeatAt.filter { it != RepeatAtEnum.NONE }
                    .collect {
                        viewBinding.textRepeatTime.text = resources.getString(it.getStringid())
                    }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                isCheckedRepeat.collect {
                    viewBinding.switchRepeat.isChecked = it

                    if (it) {
                        viewBinding.textRepeatTime.show()
                    } else {
                        viewBinding.switchRepeat.isChecked = false
                        viewBinding.textRepeatTime.gone()
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                validated.collect {
                    viewBinding.buttonCreateTask.isEnabled = it
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                attachments.collect {
                    attachmentAdapter.submitList(it)
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                isPressedCreateTask.collect {
                    if (it) {
                        loadInterCreate()
                        setIsPressCreateTask(false)
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                NetworkState.isHasInternet.collect {
//                    loadBannerAds()
                    loadAds()
                }
            }
        }
    }

    private fun createNewCategory() {
        findNavController().navigate(R.id.toCreateCat)
    }

    private fun setupCatsPopup(cats: List<CategoryEntity>) {
        val inflater = requireContext().getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.layout_select_category, null)
        popupView.elevation = 12f

        val recyclerCats = popupView.findViewById<RecyclerView>(R.id.recyclerCategory)
        recyclerCats.adapter = categoryAdapter
        categoriesPopup =
            PopupWindow(
                popupView,
                480,
                if (cats.size <= 5) WRAP_CONTENT else 600,
                true
            )
    }

    private fun showDateTime() = with(viewBinding) {
        imageTime.show()
        textTime.show()
        imageReminder.show()
        textReminder.show()
        switchReminder.show()
        imageRepeat.show()
        textRepeat.show()
        switchRepeat.show()
        buttonAddCalendar.setTextColor(Color.BLACK)
    }

    private fun hideDateTime() = with(viewBinding) {
        imageTime.gone()
        textTime.gone()
        imageReminder.gone()
        textReminder.gone()
        textReminderTime.gone()
        switchReminder.gone()
        imageRepeat.gone()
        textRepeat.gone()
        textRepeatTime.gone()
        switchRepeat.gone()
        buttonAddCalendar.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_text_secondary_dark
            )
        )
    }

    private fun onCheckChangeReminder() = with(viewBinding) {
        if (switchReminder.isChecked) {
            switchReminder.isChecked = false
            viewModel.onCheckChangeReminder(false)
            onClickReminder()
        } else {
            textReminderTime.gone()
            viewModel.onCheckChangeReminder(false)
        }
    }

    private fun onClickReminder() {
        findNavController().navigate(R.id.toAddReminderOut)
    }

    private fun onCheckChangeRepeat() = with(viewBinding) {
        if (switchRepeat.isChecked) {
//            if (!viewModel.isCheckedReminder.value) {
//                switchRepeat.isChecked = false
//                textRepeatTime.gone()
////                viewModel.resetRepeatDefault()
//                return@with
//            }
            switchRepeat.isChecked = false
            viewModel.onCheckChangeRepeat(false)
            onClickRepeat()
        } else {
            textRepeatTime.gone()
            viewModel.onCheckChangeRepeat(false)
//            viewModel.resetRepeatDefault()
        }
    }

    private fun onClickRepeat() {
        findNavController().navigate(R.id.toAddRepeatOut)
    }

    private fun isAllowInterNewTaskAds(): Boolean {
        context.let {
            if (it != null) {
                val numberOfNewTask = SPUtils.getNumberNewTask(it)
                val newNumberOfTask = numberOfNewTask + 1
                SPUtils.setNumberNewTask(it, newNumberOfTask)
                return newNumberOfTask % 2 == 0
            }
        }
        return false
    }

    private fun createTaskCallback() = with(viewBinding) {
        viewModel.createTask(
            requireContext(),
            editTaskName.text.toString().trim(),
            editNote.text.toString().trim()
        )
    }

    private fun prepareInterCreate() {

//        isLoadingAds = true
        Admod.getInstance().getInterstitalAds(
            activity,
            getString(R.string.inter_insert_ads_id),
            object : AdCallback() {
                override fun onInterstitialLoad(interstitialAd: InterstitialAd) {
                    interstitialCreateAd = interstitialAd
                }
            })
    }

    private fun loadInterCreate() {
        if (!Firebase.remoteConfig.getBoolean(SPUtils.KEY_INTER_INSERT)) {
            isLoadingAds = false
            createTaskCallback()
            return
        }
        if (!isAllowInterNewTaskAds()) {
            isLoadingAds = false
            createTaskCallback()
            return
        }

        if (context?.isInternetAvailable() == false) {
            isLoadingAds = false
            createTaskCallback()
            return
        }

        Admod.getInstance().setOpenActivityAfterShowInterAds(true)
        Admod.getInstance()
            .forceShowInterstitial(
                activity,
                interstitialCreateAd,
                object : AdCallback() {
                    override fun onAdClosed() {
                        isLoadingAds = false
                        createTaskCallback()
                    }

                    override fun onAdLeftApplication() {
                        super.onAdLeftApplication()
                        isLoadingAds = false
                        createTaskCallback()

                    }

                    override fun onAdFailedToLoad(i: LoadAdError?) {
                        super.onAdFailedToLoad(i)
                        isLoadingAds = false
                        createTaskCallback()
                    }
                })
    }

    private fun loadAds() = with(viewBinding) {
        if (!FirebaseRemoteConfig.getInstance().getBoolean(SPUtils.KEY_NATIVE_CREATE_TASK)) {
            layoutAds.hide()
            return@with
        }
        if (!context?.isInternetAvailable()!!) return@with
        skeletonLayout.showSkeleton()
        Admod.getInstance()
            .loadNativeAd(
                activity,
                getString(R.string.native_create_task_ads_id),
                object : AdCallback() {
                    override fun onUnifiedNativeAdLoaded(unifiedNativeAd: NativeAd) {
                        nativeAds = unifiedNativeAd
                        skeletonLayout.showOriginal()
                        imageAdDescLoading.gone()
                        Admod.getInstance().populateUnifiedNativeAdView(unifiedNativeAd, adView)
                        imageIcon.setImageDrawable(unifiedNativeAd.icon?.drawable)
                        imageContent.setImageDrawable(unifiedNativeAd.mediaContent?.mainImage)
                    }
                })
    }
}
