package com.trustedapp.todolist.planner.reminders.screens.newtask.calendar

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseDialogFragment
import com.trustedapp.todolist.planner.reminders.databinding.FragmentSetRepeatBinding
import com.trustedapp.todolist.planner.reminders.screens.newtask.NewTaskViewModel
import com.trustedapp.todolist.planner.reminders.screens.newtask.RepeatAtEnum
import kotlinx.coroutines.flow.collect

class SetRepeatAtDialog : BaseDialogFragment<FragmentSetRepeatBinding>() {
    private val viewModel: NewTaskViewModel by activityViewModels()
    private var selRepeatAtItem: MenuItem? = null

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSetRepeatBinding.inflate(layoutInflater, container, false)

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
                    viewBinding.tvRepeatAtValue.text = resources.getString(it.getStringid())

                    val popup = PopupMenu(requireContext(), viewBinding.tvRepeatAtValue)
                    popup.menuInflater.inflate(R.menu.repeat_at_menu, popup.menu)
                    selRepeatAtItem = popup.menu.findItem(it.getItemMenuId())
                }
            }
        }
    }

    private fun onClickCancel(view: View) {
        dismiss()
    }

    private fun onClickDone(view: View) {
        viewModel.onCheckChangeRepeat(true)

        // Default value
        viewModel.selectRepeatAt(RepeatAtEnum.HOUR)

        when (selRepeatAtItem?.itemId) {
            R.id.option_daily -> viewModel.selectRepeatAt(RepeatAtEnum.DAILY)
            R.id.option_hour -> viewModel.selectRepeatAt(RepeatAtEnum.HOUR)
            R.id.option_weekly -> viewModel.selectRepeatAt(RepeatAtEnum.WEEKLY)
            R.id.option_monthly -> viewModel.selectRepeatAt(RepeatAtEnum.MONTHLY)
            R.id.option_yearly -> viewModel.selectRepeatAt(RepeatAtEnum.YEARLY)
        }
        dismiss()
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) = with(viewBinding) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        when (menuRes) {
            R.menu.repeat_at_menu -> {
                val menuItem = selRepeatAtItem?.let { popup.menu.findItem(it.itemId) }
                menuItem?.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_checked_primary)
            }
            else -> {}
        }

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
            when (menuRes) {
                R.menu.repeat_at_menu -> {
                    selRepeatAtItem = item
                    tvRepeatAtValue.text = item?.title
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