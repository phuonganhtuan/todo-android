package com.example.todo.utils

import android.content.Context

object SPUtils {

    private const val TODO_SP_KEY = "TodoSP"
    private const val RECENT_SEARCH_KEY = "RecentSearchKey"
    private const val RECENT_JOIN = "-.,*"
    private const val FIRST_TIME_KEY = "IsFirstTime"

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
}
