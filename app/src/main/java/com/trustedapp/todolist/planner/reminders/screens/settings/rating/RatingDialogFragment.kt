package com.trustedapp.todolist.planner.reminders.screens.settings.rating

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseDialogFragment
import com.trustedapp.todolist.planner.reminders.databinding.FragmentRatingBinding
import com.trustedapp.todolist.planner.reminders.utils.getColorFromAttr
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RatingDialogFragment : BaseDialogFragment<FragmentRatingBinding>() {

    private var stars = listOf<ImageView>()

    var currentRating = 5

    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentRatingBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupEvents()
    }

    private fun setupEvents() = with(viewBinding) {
        buttonRate.setOnClickListener {
            rate()
            dismiss()
        }
        star1.setOnClickListener(::beginInputRating)
        star2.setOnClickListener(::beginInputRating)
        star3.setOnClickListener(::beginInputRating)
        star4.setOnClickListener(::beginInputRating)
        star5.setOnClickListener(::beginInputRating)
    }

    private fun beginInputRating(view: View) = with(viewBinding) {

        currentRating = view.tag.toString().toInt()
        if (currentRating > 5 || currentRating < 1) return@with
        if (currentRating < 4) {
            dismiss()
        }

        for (i in 0 until currentRating) {
            stars[i].setImageResource(R.drawable.ic_star_filled)
        }
        for (i in currentRating until 5) {
            stars[i].setImageResource(R.drawable.ic_star_outline)
        }
    }

    private fun rate() = lifecycleScope.launch {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + requireContext().packageName)
                )
            )
        } catch (e1: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + requireContext().packageName)
                )
            )
        }
    }

    private fun initViews() = with(viewBinding) {
        stars = listOf(star1, star2, star3, star4, star5)
        val suggestString =
            SpannableString(getString(R.string.please_rate_us_5_stars_if_you_enjoy_our_app))

        suggestString.setSpan(
            ForegroundColorSpan(requireContext().getColorFromAttr(R.attr.colorPrimary)),
            15,
            22,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textSuggest.text = suggestString
    }
}
