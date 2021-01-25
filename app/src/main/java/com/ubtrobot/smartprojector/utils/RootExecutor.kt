package com.ubtrobot.smartprojector.utils

import java.io.DataOutputStream

class RootExecutor {
    companion object {
        fun exec(
            cmd: String = "mkdir /sdcard/testdir\nexit\n",
            success: () -> Unit,
            failure: () -> Unit
        ) {
            try {
                val p = Runtime.getRuntime().exec("su")

                val dos = DataOutputStream(p.outputStream)
                dos.writeBytes(cmd)
                dos.flush()
                dos.close()

                try {
                    p.waitFor()
                    if (p.exitValue() != 255) {
                        success()
                    } else {
                        failure()
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

class RootCommand {
    companion object {
        fun stopApp(pkgName: String) : String = "am force-stop ${pkgName}\n"

        fun grantPermission(permission: String)= "pm grant com.ubtrobot.smartprojector $permission\n"
    }
}