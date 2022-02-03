package com.example.todo.screens.newtask.calendar

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.todo.R
import com.example.todo.base.BaseDialogFragment
import com.example.todo.databinding.FragmentSetRepeatBinding
import com.example.todo.screens.newtask.NewTaskViewModel
import com.example.todo.screens.newtask.RepeatAtEnum
import kotlinx.coroutines.flow.collect

class SetRepeatAtDialog : BaseDialogFragment<FragmentSetRepeatBinding>() {
    private val viewModel: NewTaskViewModel by activityViewModels()
    private var selRepeatAtItem: MenuItem? = null

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentSetRepeatBinding {
        val rootView = FragmentSetRepeatBinding.inflate(layoutInflater, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEvents()
        observeData()
    }

    private fun setEvents() = with(viewBinding) {
        tvRepeatAtValue.setOnClickListener { showMenu(it, R.menu.repeat_at_menu) }

        btnCancel.setOnClickListener { onClickCancel(it) }
        btnDone.setOnClickListener { onClickDone(it) }
    }

    private fun observeData() = with(viewModel) {
        lifecycleScope.launchWhenStarted {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                selectedRepeatAt.collect {
                    viewBinding.tvRepeatAtValue.setText(resources.getString(it.getStringid()))
                }
            }
        }
    }

    private fun onClickCancel(view: View) {
        dismiss()
    }

    private fun onClickDone(view: View) {
        when (selRepeatAtItem?.itemId) {
            R.id.option_daily -> viewModel.selectRepeatAt(RepeatAtEnum.DAILY)
            R.id.option_hour -> viewModel.selectRepeatAt(RepeatAtEnum.HOUR)
            R.id.option_weekly -> viewModel.selectRepeatAt(RepeatAtEnum.WEEKLY)
            R.id.option_monthly -> viewModel.selectRepeatAt(RepeatAtEnum.MONTHLY)
            R.id.option_yearly -> viewModel.selectRepeatAt(RepeatAtEnum.YEARLY)
        }
        viewModel.onCheckChangeRepeat(true)
        dismiss()
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) = with(viewBinding) {
        val popup = PopupMenu(context!!, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
            when (menuRes) {
                R.menu.repeat_at_menu -> {
                    selRepeatAtItem = item
                    tvRepeatAtValue.setText(item?.title)
                }
                else -> {

                }
            }

            true
        })
        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        try {
            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(popup)
            mPopup.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(mPopup, true)

        } catch (e: Exception) {
            Log.e("SetReminderDialog", "Error show popup", e)
        } finally {
            popup.show()
        }
    }
}