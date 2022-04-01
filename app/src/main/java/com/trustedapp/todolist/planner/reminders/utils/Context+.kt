package com.trustedapp.todolist.planner.reminders.utils

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.TypedValue
import android.view.View
import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*

fun Context.isInternetAvailable(): Boolean {

    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivityManager.activeNetwork ?: return false
    val actNw =
        connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
    return when {
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}

class NetworkChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        NetworkState.isHasInternet.value = context.isInternetAvailable()
    }
}

object NetworkState {

    val isHasInternet = MutableStateFlow(true)
}

fun Context.applyLanguage(languageCode: String) {
    var langCode = languageCode
    if (langCode.isEmpty()) langCode = "en"
    val config = resources.configuration
    val locale = Locale(langCode)
    Locale.setDefault(locale)
    config.setLocale(locale)
    createConfigurationContext(config)
    resources.updateConfiguration(config, resources.displayMetrics)
    SPUtils.saveCurrentLang(this, langCode)
}

fun Context.getStringByLocale(@StringRes stringRes: Int): String {
    val configuration = Configuration(resources.configuration)
    configuration.setLocale(Locale(SPUtils.getCurrentLang(this) ?: "en"))
    return createConfigurationContext(configuration).resources.getString(stringRes)
}
