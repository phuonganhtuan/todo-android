package com.trustedapp.todolist.planner.reminders.screens.settings.rating

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.trustedapp.todolist.planner.reminders.R
import com.trustedapp.todolist.planner.reminders.base.BaseDialogFragment
import com.trustedapp.todolist.planner.reminders.databinding.FragmentRatingBinding
import com.trustedapp.todolist.planner.reminders.utils.SPUtils
import com.trustedapp.todolist.planner.reminders.utils.getColorFromAttr
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RatingDialogFragment : BaseDialogFragment<FragmentRatingBinding>() {

    private var stars = listOf<ImageView>()

    var currentRating = 5

    var callBackWhenRate: (() -> Unit)? = null

    private var manager: ReviewManager? = null

    //    private val manager = activity?.let { FakeReviewManager(it) }
    override fun inflateViewBinding(
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentRatingBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initData()
        setupEvents()
    }

    private fun initData() = with(viewBinding) {
        manager = activity?.let { ReviewManagerFactory.create(it) }
    }

    private fun setupEvents() = with(viewBinding) {
        buttonRate.setOnClickListener {
            rate()
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

        for (i in 0 until currentRating) {
            stars[i].setImageResource(R.drawable.ic_star_filled)
        }
        for (i in currentRating until 5) {
            stars[i].setImageResource(R.drawable.ic_star_outline)
        }
    }

    private fun rate() = lifecycleScope.launch {
        if (currentRating < 4) {
            context?.let { SPUtils.setIsRate(it, true) }
            callBackWhenRate?.let { it1 -> it1() }
        } else {
            try {
                Log.e("rate - manager", manager.toString())
                val request = manager?.requestReviewFlow()
                Log.e("rate - request", request.toString())
                request?.addOnCompleteListener { task ->
                    Log.e("rate - task", task.toString())
                    if (task.isSuccessful) {
                        // We got the ReviewInfo object
                        val reviewInfo = task.result
                        if (reviewInfo != null) {
                            val flow = activity?.let { manager?.launchReviewFlow(it, reviewInfo) }
                            flow?.addOnCompleteListener { _ ->
                                // The flow has finished. The API does not indicate whether the user
                                // reviewed or not, or even whether the review dialog was shown. Thus, no
                                // matter the result, we continue our app flow.
                                context?.let { SPUtils.setIsRate(it, true) }
                                callBackWhenRate?.let { it1 -> it1() }
                            }
                        } else {
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
                            context?.let { SPUtils.setIsRate(it, true) }
                            callBackWhenRate?.let { it1 -> it1() }
                        }
                    } else {
                        try {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("market://details?id=" + requireContext().packageName)
                                )
                            )
                        } catch (e1: ActivityNotFoundException) {
                            Log.e("rate - ActivityNotFoundException", e1.message.toString())
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=" + requireContext().packageName)
                                )
                            )
                        }
                        context?.let { SPUtils.setIsRate(it, true) }
                        callBackWhenRate?.let { it1 -> it1() }
                    }
                }
            } catch (ex: Exception) {
                Log.e("rate", ex.message.toString())
            }

        }
    }

    private fun initViews() = with(viewBinding) {
        stars = listOf(star1, star2, star3, star4, star5)
        val suggestString =
            SpannableString(getString(R.string.please_rate_us_5_stars_if_you_enjoy_our_app))

        if ((SPUtils.getCurrentLang(requireContext()) ?: "en") == "en") {
            suggestString.setSpan(
                ForegroundColorSpan(requireContext().getColorFromAttr(R.attr.colorPrimary)),
                15,
                22,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        textSuggest.text = suggestString
    }
}
