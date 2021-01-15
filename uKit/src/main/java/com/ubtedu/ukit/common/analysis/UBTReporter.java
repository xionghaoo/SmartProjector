package com.ubtedu.ukit.common.analysis;

import android.content.Context;

import androidx.annotation.Keep;

import com.ubtedu.alpha1x.core.base.delegate.EventDelegate;
import com.ubtedu.ukit.common.flavor.Channel;
import com.ubtedu.ukit.common.flavor.Flavor;

import java.util.Map;

/**
 * @author qinicy
 * @data 2019/02/20
 */
@Keep
public class UBTReporter {
    private static Context sContext;
    private static EventDelegate sEventDelegate;

    public static void init(Context ctx, EventDelegate d) {
        sContext = ctx.getApplicationContext();
        sEventDelegate = d;
    }

    public static void reportSerialNumber(String serialNumber) {
        EventDelegate.Event event = new EventDelegate.Event();
        event.type = Events.TYPE_COUNT;
        event.id = "uKitActivation";
        event.keyStrings.put("serialNumber", serialNumber);
        sEventDelegate.onEvent(sContext, event);
    }



    public static void onEvent(String eventId, Map<String, String> args) {
        if (!canReport()) {
            return;
        }
        EventDelegate.Event event = new EventDelegate.Event();
        event.type = Events.TYPE_COUNT;
        event.id = eventId;
        if (args != null) {
            event.keyStrings = args;
        }
        sEventDelegate.onEvent(sContext, event);
    }

    public static void onSessionEvent(String eventId, boolean sessionStart, Map<String, String> args) {
        if (!canReport()) {
            return;
        }
        EventDelegate.Event event = new EventDelegate.Event();
        event.type = Events.TYPE_CALCULATE;
        event.id = eventId;
        event.sessionStart = sessionStart;
        if (args != null) {
            event.keyStrings = args;
        }
        sEventDelegate.onEvent(sContext, event);
    }

    public static void onPageStart(String pageName) {
        sEventDelegate.onPageStart(pageName);
    }

    public static void onPageEnd(String pageName) {
        sEventDelegate.onPageEnd(pageName);
    }

    private static boolean canReport() {
        //北美不上报
        boolean isNA = Flavor.getChannel().getId() == Channel.NA.getId();
        return !isNA && sContext != null && sEventDelegate != null && sEventDelegate.isDelegate();
    }
}
