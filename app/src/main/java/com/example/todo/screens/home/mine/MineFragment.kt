package com.example.todo.screens.home.mine

import android.os.Bundle
import android.view.ViewGroup
import com.example.todo.base.BaseFragment
import com.example.todo.databinding.FragmentMineBinding

class MineFragment : BaseFragment<FragmentMineBinding>() {

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMineBinding.inflate(layoutInflater, container, false)
}
