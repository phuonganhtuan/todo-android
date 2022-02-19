package com.trustedapp.todolist.planner.reminders.screens.taskdetail

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
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
import com.trustedapp.todolist.planner.reminders.databinding.FragmentTaskDetailBinding
import com.trustedapp.todolist.planner.reminders.screens.newtask.NewTaskViewModel
import com.trustedapp.todolist.planner.reminders.screens.newtask.ReminderTimeEnum
import com.trustedapp.todolist.planner.reminders.screens.newtask.RepeatAtEnum
import com.trustedapp.todolist.planner.reminders.screens.newtask.category.OnCatInteractListener
import com.trustedapp.todolist.planner.reminders.screens.newtask.category.SelectCategoryAdapter
import com.trustedapp.todolist.planner.reminders.screens.taskdetail.attachment.AttachmentAdapter
import com.trustedapp.todolist.planner.reminders.screens.taskdetail.subtasks.ItemMoveCallback
import com.trustedapp.todolist.planner.reminders.screens.taskdetail.subtasks.OnSubTaskDetailInteract
import com.trustedapp.todolist.planner.reminders.screens.taskdetail.subtasks.SubTaskDetailAdapter
import com.trustedapp.todolist.planner.reminders.utils.*
import com.trustedapp.todolist.planner.reminders.utils.helper.getCategoryColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import javax.inject.Inject


@AndroidEntryPoint
class TaskDetailFragment : BaseFragment<FragmentTaskDetailBinding>() {

    private val viewModel: NewTaskViewModel by activityViewModels()

    @Inject
    lateinit var subTaskAdapter: SubTaskDetailAdapter

    private var categoriesPopup: PopupWindow? = null

    @Inject
    lateinit var categoryAdapter: SelectCategoryAdapter

    @Inject
    lateinit var attachmentAdapter: AttachmentAdapter

//    private var listAnimator: RecyclerView.ItemAnimator? = null

    var touchHelper: ItemTouchHelper? = null

    private var isLoadingAds: Boolean = false
    private var interstitialCreateAd: InterstitialAd? = null

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentTaskDetailBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initData()
        setupEvents()
        observeData()
        prepareInterUpdate()
    }

    private fun initViews() = with(viewBinding) {
        recyclerSubTasks.adapter = subTaskAdapter
        recyclerAttachment.adapter = attachmentAdapter
//        listAnimator = recyclerSubTasks.itemAnimator
        recyclerSubTasks.itemAnimator = null
        recyclerAttachment.itemAnimator = null
        editNote.boldWhenFocus()
        hideDateTime()

    }

    private fun initData() {

    }

    private fun validateTask() {
        viewModel.validate(viewBinding.textTaskName.text.toString().trim())
    }

    private fun setupEvents() = with(viewBinding) {
        touchHelper = ItemTouchHelper(ItemMoveCallback(subTaskAdapter))
        touchHelper?.attachToRecyclerView(recyclerSubTasks)
        textCategory.setOnClickListener {
            categoriesPopup?.showAsDropDown(it, -50, -20)
        }
        buttonAttachment.setOnClickListener {
            findNavController().navigate(R.id.toSelectAttachment)
        }
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
        attachmentAdapter.onAttachmentRemoveListener = {
            viewModel.removeAttachment(it)
        }
        subTaskAdapter.setOnTaskListener(object : OnSubTaskDetailInteract {
            override fun onStateChange(index: Int, state: Boolean) {
                viewModel.updateSubTaskState(index, state)
            }

            override fun onTitleChanged(index: Int, title: String) {
                viewModel.updateSubTaskTitle(index, title)
            }

            override fun startDrag(viewHolder: RecyclerView.ViewHolder) {
//                recyclerSubTasks.itemAnimator = listAnimator
                touchHelper?.startDrag(viewHolder)
            }

            override fun endDrag() {
//                recyclerSubTasks.itemAnimator = null
                viewModel.setSubTasks(subTaskAdapter.newOrders)
            }
        })
        buttonNewSubTask.setOnClickListener {
            layoutDetail.clearFocus()
            viewModel.addSubTask()
        }
        switchRepeat.setOnClickListener {
            onCheckChangeRepeat()
        }
        switchReminder.setOnClickListener {
            onCheckChangeReminder()
        }
        buttonAddCalendar.setOnClickListener {
            findNavController().navigate(R.id.toAddCalendar)
        }
        textTaskName.addTextChangedListener {
            validateTask()
        }
        attachmentAdapter.onAttachmentClickListener = {
            val attachment = viewModel.attachments.value[it]
            FileUtils.openAttachment(requireContext(), attachment)
        }
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                task.collect {
                    viewBinding.apply {
                        textTaskName.setText(it.task.title)
                        editNote.setText(it.detail.note)
                        if (it.category == null) {
                            textCategory.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.color_text_secondary_dark
                                )
                            )
                            textCategory.text = getString(R.string.no_category)
                        } else {
                            val catColor = getCategoryColor(requireContext(), it.category)
                            textCategory.setTextColor(catColor)
                            textCategory.text = it.category?.name
                        }
                        if (it.task.isDone) imageDone.show() else imageDone.gone()
                    }
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
                subtasks.collect {
                    viewBinding.layoutDetail.clearFocus()
                    subTaskAdapter.newOrders = it.toMutableList()
                    subTaskAdapter.submitList(it)
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                isEditing.collect {
                    if (it) this@TaskDetailFragment.toEditMode() else this@TaskDetailFragment.toViewMode()
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectedCatIndex.collect {
                    viewBinding.apply {
                        if (it == -1) {
                            return@apply
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
                    val catIdSum = it.map { cat -> cat.id }.sum()
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
                    viewBinding.buttonAddCalendar.text = DateTimeUtils.getComparableDateString(it)
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
                        validateTask()
                        showDateTime()
                        viewBinding.textTime.text = it
                    }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectedReminderTime.filter { it != ReminderTimeEnum.NONE }.collect {
                    val text =
                        if (it == ReminderTimeEnum.CUSTOM_DAY_BEFORE) "${customReminderTime.value.toString()} ${
                            resources.getString(customReminderTimeUnit.value.getStringid())
                                .lowercase()
                        } ${
                            resources.getString(R.string.before).lowercase()
                        }" else resources.getString(it.getStringid())
                    viewBinding.textReminderTime.text = text
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
                    }else{
                        viewBinding.switchRepeat.isChecked = false
                        viewBinding.textRepeatTime.gone()
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                isSaving.collect {
                    if (it) loadInterUpdate()
                }
            }
        }
    }

    private fun toViewMode() = with(viewBinding) {
        subTaskAdapter.isEditing = false
        attachmentAdapter.isEditing = false
        textTaskName.isEnabled = false
        attachmentAdapter.notifyDataSetChanged()
        subTaskAdapter.notifyDataSetChanged()
        editNote.isEnabled = false
        editNote.background.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_bg_main_grey
            ), PorterDuff.Mode.SRC_ATOP
        )
        textCategory.apply {
            isClickable = false
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
        buttonAttachment.isEnabled = false
        buttonNewSubTask.gone()
        buttonAddCalendar.isEnabled = false
        switchReminder.isEnabled = false
        switchRepeat.isEnabled = false
        if (viewModel.task.value.detail.note.isEmpty()) {
            editNote.gone()
        } else {
            editNote.show()
        }
        if (viewModel.task.value.attachments.isNullOrEmpty()) {
            imageAttachment.gone()
            buttonAttachment.gone()
        } else {
            imageAttachment.show()
            buttonAttachment.show()
        }
    }

    private fun toEditMode() = with(viewBinding) {
        validateTask()
        imageAttachment.show()
        buttonAttachment.show()
        subTaskAdapter.isEditing = true
        attachmentAdapter.isEditing = true
        textTaskName.isEnabled = true
        attachmentAdapter.notifyDataSetChanged()
        subTaskAdapter.notifyDataSetChanged()
        editNote.background.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_border_grey
            ), PorterDuff.Mode.SRC_ATOP
        )
        editNote.show()
        editNote.isEnabled = true
        textCategory.apply {
            isClickable = true
            setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_down_16),
                null
            )
        }
        buttonAttachment.isEnabled = true
        buttonNewSubTask.show()
        buttonAddCalendar.isEnabled = true
        switchReminder.isEnabled = true
        switchRepeat.isEnabled = true
    }

    private fun createNewCategory() {
        findNavController().navigate(R.id.toCreateCat)
    }

    private fun setupCatsPopup(cats: List<CategoryEntity>) {
        val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.layout_select_category, null)
        popupView.elevation = 12f

        val recyclerCats = popupView.findViewById<RecyclerView>(R.id.recyclerCategory)
        recyclerCats.adapter = categoryAdapter
        categoriesPopup =
            PopupWindow(
                popupView,
                460,
                if (cats.size <= 5) ViewGroup.LayoutParams.WRAP_CONTENT else 400,
                true
            )
    }

    private fun showDateTime() = with(viewBinding) {
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
//            viewModel.resetRepeatDefault()
//            viewModel.resetReminderDefault()
        }
    }

    private fun onClickReminder() {
        findNavController().navigate(R.id.toAddReminderDetail)
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
        findNavController().navigate(R.id.toAddRepeatDetail)
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

    private fun prepareInterUpdate() {

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

    private fun loadInterUpdate(){
        if (!Firebase.remoteConfig.getBoolean(SPUtils.KEY_INTER_INSERT)) {
            isLoadingAds = false
            updateTaskCallback()
            return
        }
        if (!isAllowInterNewTaskAds()) {
            isLoadingAds = false
            updateTaskCallback()
            return
        }

        if (context?.isInternetAvailable() == false) {
            isLoadingAds = false
            updateTaskCallback()
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
                        updateTaskCallback()
                    }
                    override fun onAdLeftApplication() {
                        super.onAdLeftApplication()
                        isLoadingAds = false
                        updateTaskCallback()

                    }

                    override fun onAdFailedToLoad(i: LoadAdError?) {
                        super.onAdFailedToLoad(i)
                        isLoadingAds = false
                        updateTaskCallback()
                    }
                })
    }

    private fun updateTaskCallback() = with(viewBinding){
        viewModel.updateTask(
            requireContext(),
            textTaskName.text.toString().trim(),
            editNote.text.toString().trim()
        )
    }
}
