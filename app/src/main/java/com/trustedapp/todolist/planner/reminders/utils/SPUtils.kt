package com.trustedapp.todolist.planner.reminders.utils

import android.content.Context

object SPUtils {

    private const val TODO_SP_KEY = "TodoSP"
    private const val RECENT_SEARCH_KEY = "RecentSearchKey"
    private const val RECENT_JOIN = "-.,*"
    private const val FIRST_TIME_KEY = "IsFirstTime"
     const val KEY_INTER_SPLASH = "inter_splash"
     const val KEY_BANNER = "banner"
     const val KEY_NATIVE_LANGUAGE = "native_language"
     const val KEY_INTER_INSERT = "inter_insert"
     const val KEY_INTER_THEME = "inter_theme"
    private const val KEY_NUMBER_CREATE_TASK = "number_create_task"

    fun saveRecentSearch(context: Context, recents: List<String>) =
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .edit()
            .putString(RECENT_SEARCH_KEY, recents.joinToString(RECENT_JOIN))
            .apply()

    fun getRecentSearch(context: Context): List<String> {
        val result = context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .getString(RECENT_SEARCH_KEY, "")?.split(RECENT_JOIN) ?: emptyList()
        if (result.size == 1 && result[0].isEmpty()) return emptyList()
        return result
    }

    fun saveFirstTimeLaunched(context: Context) {
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(FIRST_TIME_KEY, false)
            .apply()
    }

    fun isFirstTime(context: Context) =
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .getBoolean(FIRST_TIME_KEY, true)

    fun getNumberNewTask(context: Context) =
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .getInt(KEY_NUMBER_CREATE_TASK, -1)

    fun setNumberNewTask(context: Context, numberOfTask: Int) =
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE).edit()
            .putInt(KEY_NUMBER_CREATE_TASK, numberOfTask).apply()

    fun getRemoteConfig(context: Context, name: String): Boolean {
        return context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE)
            .getBoolean(name, false)
    }

    fun setRemoteConfig(context: Context, name: String, value: Boolean) {
        context.getSharedPreferences(TODO_SP_KEY, Context.MODE_PRIVATE).edit()
            .putBoolean(name, value).apply()
    }
}
