package com.example.todo.screens.newtask

import android.os.Bundle
import android.view.ViewGroup
import com.example.todo.base.BaseFragment
import com.example.todo.databinding.FragmentNewTaskBinding
import com.example.todo.databinding.FragmentTaskDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewTaskFragment : BaseFragment<FragmentNewTaskBinding>() {

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentNewTaskBinding.inflate(layoutInflater, container, false)
}
