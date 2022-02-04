package com.example.todo.screens.taskdetail

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.base.BaseFragment
import com.example.todo.data.models.entity.CategoryEntity
import com.example.todo.databinding.FragmentTaskDetailBinding
import com.example.todo.screens.newtask.category.OnCatInteractListener
import com.example.todo.screens.newtask.category.SelectCategoryAdapter
import com.example.todo.screens.taskdetail.attachment.AttachmentAdapter
import com.example.todo.screens.taskdetail.subtasks.ItemMoveCallback
import com.example.todo.screens.taskdetail.subtasks.OnSubTaskDetailInteract
import com.example.todo.screens.taskdetail.subtasks.SubTaskDetailAdapter
import com.example.todo.utils.boldWhenFocus
import com.example.todo.utils.gone
import com.example.todo.utils.helper.getCategoryColor
import com.example.todo.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject


@AndroidEntryPoint
class TaskDetailFragment : BaseFragment<FragmentTaskDetailBinding>() {

    private val viewModel: TaskDetailViewModel by activityViewModels()

    @Inject
    lateinit var subTaskAdapter: SubTaskDetailAdapter

    private var categoriesPopup: PopupWindow? = null

    @Inject
    lateinit var categoryAdapter: SelectCategoryAdapter

    @Inject
    lateinit var attachmentAdapter: AttachmentAdapter

    private var listAnimator: RecyclerView.ItemAnimator? = null

    var touchHelper: ItemTouchHelper? = null

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentTaskDetailBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupEvents()
        observeData()
    }

    private fun initViews() = with(viewBinding) {
        recyclerSubTasks.adapter = subTaskAdapter
        recyclerAttachment.adapter = attachmentAdapter
        listAnimator = recyclerSubTasks.itemAnimator
        recyclerSubTasks.itemAnimator = null
        recyclerAttachment.itemAnimator = null
        editNote.boldWhenFocus()
    }

    private fun setupEvents() = with(viewBinding) {
        touchHelper = ItemTouchHelper(ItemMoveCallback(subTaskAdapter))
        touchHelper?.attachToRecyclerView(recyclerSubTasks)
        textCategory.setOnClickListener {
            categoriesPopup?.showAsDropDown(it, 0, 12)
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
                recyclerSubTasks.itemAnimator = listAnimator
                touchHelper?.startDrag(viewHolder)
            }

            override fun endDrag() {
                recyclerSubTasks.itemAnimator = null
                viewModel.setSubTasks(subTaskAdapter.newOrders)
            }
        })
        buttonNewSubTask.setOnClickListener {
            viewModel.addSubTask()
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
    }

    private fun toEditMode() = with(viewBinding) {
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
                350,
                if (cats.size <= 5) ViewGroup.LayoutParams.WRAP_CONTENT else 600,
                true
            )
    }
}
