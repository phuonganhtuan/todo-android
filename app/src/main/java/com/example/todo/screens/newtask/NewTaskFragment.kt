package com.example.todo.screens.newtask

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.todo.R
import com.example.todo.base.BaseFragment
import com.example.todo.databinding.FragmentNewTaskBinding
import com.example.todo.screens.newtask.subtask.SubTaskAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class NewTaskFragment : BaseFragment<FragmentNewTaskBinding>() {

    private val viewModel: NewTaskViewModel by activityViewModels()

    @Inject
    lateinit var subTaskAdapter: SubTaskAdapter

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

        }
    }

    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                subTasks.collect {
                    subTaskAdapter.submitList(it)
                }
            }
        }
    }
}
