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
import com.google.firebase.ktx.Firebase
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
import com.trustedapp.todolist.planner.reminders.utils.helper.getCategoryColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
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
    }

    private fun initData() {
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
        buttonCreateTask.setOnClickListener {
            viewModel.createTask(
                requireContext(),
                editTaskName.text.toString().trim(),
                editNote.text.toString().trim()
            )
            loadInterCreate()
        }
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
                            textCategory.text = categories.value[it].name
                        }
                    }
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
                        val hourValue = when (hour > 9) {
                            true -> "$hour"
                            else -> "0$hour"
                        }
                        val minuteValue = when (minute > 9) {
                            true -> "$minute"
                            else -> "0$minute"
                        }
                        "$hourValue:$minuteValue"
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
                selectedReminderTime.filter { it != ReminderTimeEnum.NONE }.collect {
                    viewBinding.textReminderTime.text = resources.getString(it.getStringid())
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
                        viewBinding.textRepeatTime.gone()
                        viewBinding.switchRepeat.isChecked = false
                        viewModel.resetRepeatDefault()
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
                    if (it) viewBinding.textRepeatTime.show()

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
    }

    private fun createNewCategory() {
        findNavController().navigate(R.id.toCreateCat)
    }

    private fun setupCatsPopup(cats: List<CategoryEntity>) {
        val inflater = activity?.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.layout_select_category, null)
        popupView.elevation = 12f

        val recyclerCats = popupView.findViewById<RecyclerView>(R.id.recyclerCategory)
        recyclerCats.adapter = categoryAdapter
        categoriesPopup =
            PopupWindow(
                popupView,
                460,
                if (cats.size <= 5) WRAP_CONTENT else 400,
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
            textRepeatTime.gone()
            switchRepeat.isChecked = false
            viewModel.resetRepeatDefault()
            viewModel.resetReminderDefault()
        }
    }

    private fun onClickReminder() {
        findNavController().navigate(R.id.toAddReminderOut)
    }

    private fun onCheckChangeRepeat() = with(viewBinding) {
        if (switchRepeat.isChecked) {
            if (!viewModel.isCheckedReminder.value) {
                switchRepeat.isChecked = false
                textRepeatTime.gone()
                viewModel.resetRepeatDefault()
                return@with
            }
            viewModel.onCheckChangeRepeat(false)
            onClickRepeat()
        } else {
            textRepeatTime.gone()
            viewModel.resetRepeatDefault()
        }
    }

    private fun onClickRepeat() {
        findNavController().navigate(R.id.toAddRepeatOut)
    }

    private fun isAllowInterNewTaskAds(): Boolean{
        context.let {
            if (it != null){
                val numberOfNewTask =  SPUtils.getNumberNewTask(it)
                val newNumberOfTask = numberOfNewTask + 1
                SPUtils.setNumberNewTask(it, newNumberOfTask)
                return newNumberOfTask % 2 == 0
            }
        }
        return false
    }

    private fun loadInterCreate() {
        if (Firebase.remoteConfig.getBoolean(SPUtils.KEY_INTER_INSERT)) return
        if (!isAllowInterNewTaskAds()) return

        if (context?.isInternetAvailable() == false) {
            isLoadingAds = false
            return
        }
        isLoadingAds = true
        Admod.getInstance().getInterstitalAds(
            activity,
            getString(R.string.inter_ads_id),
            object : AdCallback() {
                override fun onInterstitialLoad(interstitialAd: InterstitialAd) {
                    isLoadingAds = false
                    // callback
                    
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                    isLoadingAds = false

                }

                override fun onAdLeftApplication() {
                    super.onAdLeftApplication()
                    isLoadingAds = false

                }

                override fun onAdFailedToLoad(i: LoadAdError?) {
                    super.onAdFailedToLoad(i)
                    isLoadingAds = false

                }
            })
    }
}
