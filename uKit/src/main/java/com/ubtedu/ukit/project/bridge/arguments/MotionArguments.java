package com.ubtedu.ukit.project.bridge.arguments;

import com.google.gson.annotations.SerializedName;
import com.ubtedu.ukit.common.vo.SerializablePOJO;
import com.ubtedu.ukit.project.vo.Motion;

public class MotionArguments extends SerializablePOJO {
    @SerializedName("id")
    private String id;
    @SerializedName("content")
    private Motion[] content;
    @SerializedName("name")
    private String name;
    @SerializedName("totalTime")
    private long totalTime;

    public MotionArguments(Motion motion) {
        id = motion.id;
        content = new Motion[]{motion};
        name = motion.name;
        totalTime = motion.totaltime;
    }
}
