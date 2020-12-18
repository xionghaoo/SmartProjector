package com.ubtrobot.smartprojector.utils

import java.io.DataOutputStream

class RootPermissionUtil {
    companion object {
        fun request(success: () -> Unit, failure: () -> Unit) {
            try {
                val p = Runtime.getRuntime().exec("su")

                val dos = DataOutputStream(p.outputStream)
                dos.writeBytes("mkdir /sdcard/testdir\n")
                dos.writeBytes("exit\n")
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