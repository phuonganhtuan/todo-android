package com.example.todo.screens.taskdetail

import android.os.Bundle
import android.view.ViewGroup
import com.example.todo.base.BaseFragment
import com.example.todo.databinding.FragmentTaskDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskDetailFragment : BaseFragment<FragmentTaskDetailBinding>() {

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentTaskDetailBinding.inflate(layoutInflater, container, false)
}
