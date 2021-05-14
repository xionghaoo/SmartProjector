package xh.zero.voice

import android.content.Context
import com.tencent.ai.tvs.LoginProxy

class VoiceManager {
    companion object {
        fun initial(context: Context, appKey: String) {
            LoginProxy.getInstance().registerApp(context, appKey)
        }
    }
}