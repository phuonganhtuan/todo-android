package com.example.todo.screens.home.calendar

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.todo.base.BaseFragment
import com.example.todo.databinding.FragmentCalendarBinding
import com.example.todo.utils.DateTimeUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CalendarFragment : BaseFragment<FragmentCalendarBinding>() {

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentCalendarBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEvents()
    }

    private fun setupEvents() = with(viewBinding) {
        calendarView.onDateSelectedListener = {
            Toast.makeText(
                requireContext(),
                DateTimeUtils.getComparableDateString(it),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
