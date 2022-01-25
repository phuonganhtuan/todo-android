package com.example.todo.screens.newtask

import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.todo.R
import com.example.todo.base.BaseFragment
import com.example.todo.data.models.entity.CategoryEntity
import com.example.todo.databinding.FragmentNewTaskBinding
import com.example.todo.screens.newtask.category.OnCatInteractListener
import com.example.todo.screens.newtask.category.SelectCategoryAdapter
import com.example.todo.screens.newtask.subtask.OnSubTaskInteract
import com.example.todo.screens.newtask.subtask.SubTaskAdapter
import com.example.todo.utils.boldWhenFocus
import com.example.todo.utils.gone
import com.example.todo.utils.helper.getCategoryColor
import com.example.todo.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class NewTaskFragment : BaseFragment<FragmentNewTaskBinding>() {

    private val viewModel: NewTaskViewModel by activityViewModels()

    private var categoriesPopup: PopupWindow? = null

    @Inject
    lateinit var subTaskAdapter: SubTaskAdapter

    @Inject
    lateinit var categoryAdapter: SelectCategoryAdapter

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

    }

    private fun initViews() = with(viewBinding) {
        recyclerSubTasks.adapter = subTaskAdapter
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
            categoriesPopup?.showAsDropDown(it, 0, 12)
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
    }

    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                subTasks.collect {
                    subTaskAdapter.submitList(it)
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                canAddSubTask.collect {
                    if (it) viewBinding.buttonNewSubTask.show() else viewBinding.buttonNewSubTask.gone()
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectedCatIndex.collect {
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
                350,
                if (cats.size <= 5) WRAP_CONTENT else 600,
                true
            )
    }
}
