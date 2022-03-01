package com.trustedapp.todolist.planner.reminders.utils

import com.trustedapp.todolist.planner.reminders.utils.BaseCommandUtil
import com.trustedapp.todolist.planner.reminders.utils.BaseCommandUtil.runCommandWithRoot

class BatterySaverModeUtil {
    private val COMMAND_ENABLE = "settings put global low_power 1\n" +
            "am broadcast -a android.os.action.POWER_SAVE_MODE_CHANGED --ez mode true\n"
    private val COMMAND_DISABLE = "settings put global low_power 0\n" +
            "am broadcast -a android.os.action.POWER_SAVE_MODE_CHANGED --ez mode false\n"

    fun enable() {
        runCommandWithRoot(COMMAND_ENABLE)
    }

    fun disable() {
        runCommandWithRoot(COMMAND_DISABLE)
    }
}