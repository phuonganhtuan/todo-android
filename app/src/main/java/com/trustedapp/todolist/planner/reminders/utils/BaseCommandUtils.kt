package com.trustedapp.todolist.planner.reminders.utils

import java.io.DataOutputStream
import java.io.IOException


/**
 * Created by Ghost on 3/23/2015.
 */
object BaseCommandUtil {
    fun runCommandWithRoot(command: String?) {
        try {
            val p = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(p.outputStream)
            os.writeBytes(command)
            os.writeBytes("exit\n")
            os.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun requireRoot() {
        runCommandWithRoot("")
    }
}