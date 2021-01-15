package com.ubtedu.ukit.project.blockly;

import com.ubtedu.ukit.common.view.UKitCharsInputFilter;

class UkitAudioNameInputFilter extends UKitCharsInputFilter {
    public UkitAudioNameInputFilter(int max) {
        super(max);
    }

    @Override
    protected void initRequirements() {
        super.initRequirements();
        addFilterChar("(");
        addFilterChar(")");
        addFilterChar("\\");
        addFilterChar("/");
        addFilterChar("#");
        addFilterChar("\"");
        addFilterChar(":");
        addFilterChar("|");
        addFilterChar("*");
        addFilterChar("?");
    }
}
