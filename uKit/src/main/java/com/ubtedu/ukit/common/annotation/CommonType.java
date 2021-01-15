/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.common.annotation;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

/**
 * @Author qinicy
 * @Date 2018/11/6
 **/
public interface CommonType {
    /// @{ 帖子图片类型
    int TOPIC_COVER_ICON_PIC = 1;
    int TOPIC_COVER_ICON_ANIM = 2;
    int TOPIC_COVER_ICON_VIDEO = 3;
    int TOPIC_COVER_ICON_TEXT = 4;

    @IntDef({TOPIC_COVER_ICON_PIC, TOPIC_COVER_ICON_ANIM, TOPIC_COVER_ICON_VIDEO, TOPIC_COVER_ICON_TEXT})
    @interface CoverType {
    }
    /// @}

    /// @{ 帖子文件上传类型
    int TOPIC_FILE_TYPE_PIC = 1;
    int TOPIC_FILE_TYPE_ANIM = 2;
    int TOPIC_FILE_TYPE_VIDEO = 3;
    int TOPIC_FILE_TYPE_TEXT = 4;
    int TOPIC_FILE_TYPE_PRP = 5;
    int TOPIC_FILE_TYPE_BLOCKLY = 6;

    @IntDef({TOPIC_FILE_TYPE_PIC, TOPIC_FILE_TYPE_ANIM, TOPIC_FILE_TYPE_VIDEO, TOPIC_FILE_TYPE_TEXT,
            TOPIC_FILE_TYPE_PRP, TOPIC_FILE_TYPE_BLOCKLY
    })
    @interface TopicFileType {
    }
    /// @}


    /// @{ 语言代码
    // zh-hans:简体中文;
    String LANG_ZH_HANS = "zh-hans";
    // en:英文;
    String LANG_ENGLISH = "en";
    // fr:法语;
    String LANG_FRENCH = "fr";
    // de:德语;
    String LANG_GERMAN = "de";
    // it:意大利语;
    String LANG_ITALY = "it";
    // es:西班牙语;
    String LANG_SPAIN = "es";
    // ja:日语;
    String LANG_JAPAN = "ja";
    // pt:葡萄牙;
    String LANG_PORTUGAL = "pt";
    // zh-hant:繁体中文;
    String LANG_ZH_HANT = "zh-hant";
    // ar:阿拉伯语;
    String LANG_ARAB = "ar";
    // ko:韩语;
    String LANG_KOREA = "ko";
    // ru:俄语;
    String LANG_RUSSIA = "ru";
    // pl:波兰语;
    String LANG_POLAND = "pl";
    // tr:土耳其语;
    String LANG_TURKEY = "tr";
    // da:丹麦语;
    String LANG_DANISH = "da";

    @StringDef({LANG_ZH_HANS, LANG_ENGLISH, LANG_FRENCH, LANG_GERMAN, LANG_ITALY,
            LANG_SPAIN, LANG_JAPAN, LANG_PORTUGAL, LANG_ZH_HANT, LANG_ARAB,
            LANG_KOREA, LANG_RUSSIA, LANG_POLAND, LANG_TURKEY, LANG_DANISH
    })
    @interface LangCode {
    }
    /// @}

    /// @{ 获取验证码的目的
    int CAPTCHA_FOR_ALL= 0;
    int CAPTCHA_FOR_REG_OR_BIND = 1;
    int CAPTCHA_FOR_RESET_PASSWORD = 2;
    int CAPTCHA_FOR_LOGIN = 3;
    int CAPTCHA_FOR_BIND = 4;
    int CAPTCHA_FOR_DEVICE_VERIFY= 5;

    @IntDef({CAPTCHA_FOR_REG_OR_BIND, CAPTCHA_FOR_RESET_PASSWORD,
            CAPTCHA_FOR_LOGIN, CAPTCHA_FOR_BIND,CAPTCHA_FOR_DEVICE_VERIFY
    })
    @interface CaptchaPurpose {
    }
    /// @}


    /// @{ 获取验证码的目的 [帖子:post, 提问:question, 用户: user, 动作: action, 程序: program 所有:all]
    String SEARCH_TYPE_POST = "post";
    String SEARCH_TYPE_QUESTION = "question";
    String SEARCH_TYPE_USER = "user";
    String SEARCH_TYPE_PRP = "action";
    String SEARCH_TYPE_BLOCKLY = "program";
    String SEARCH_TYPE_ALL = "all";

    @StringDef({SEARCH_TYPE_POST, SEARCH_TYPE_QUESTION, SEARCH_TYPE_USER,
            SEARCH_TYPE_PRP, SEARCH_TYPE_BLOCKLY, SEARCH_TYPE_ALL})
    @interface SearchType {
    }
    /// @}

    /// @{ 获取验证码的目的
    int COMMENT_ONLY = 1;
    int COMMENT_REPLAY = 2;
    @IntDef({COMMENT_ONLY, COMMENT_REPLAY})
    @interface CommentType {
    }
    /// @}

    /// @{ 七牛云存储类型
    int STORAGE_PUBLIC = 1;
    int STORAGE_PRIVATE = 2;
    @IntDef({STORAGE_PUBLIC, STORAGE_PRIVATE})
    @interface StorageType {
    }
    /// @}
}
