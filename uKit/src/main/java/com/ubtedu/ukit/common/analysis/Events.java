package com.ubtedu.ukit.common.analysis;

/**
 * @author qinicy
 * @data 2017/12/11
 */

public class Events {
    /**
     * 计数事件
     */
    public static final int TYPE_COUNT = 10;
    /**
     * 计算事件
     */
    public static final int TYPE_CALCULATE = 11;


    public static final String CATEGORY_COMMOM = "category_commom";
    public static final String CATEGORY_CUSTOM = "category_custom";

    public static class Ids {
        public static final String EVENT_PAGE_START = "page_start";
        public static final String EVENT_PAGE_END = "page_end";

        public static final String CLICK = "onclick";
        public static final String SHARE = "share";
        /**
         * 主页
         */
        public static final String app_setting_btn_click = "app_setting_btn_click";
        public static final String app_home_tab_click = "app_home_tab_click";
        /**
         * 菜单页
         */
        public static final String app_menu_avatar_btn_click = "app_menu_avatar_btn_click";
        public static final String app_menu_nickname_modify_btn_click = "app_menu_nickname_modify_btn_click";
        public static final String app_menu_password_change_btn_click = "app_menu_password_change_btn_click";
        public static final String app_menu_logout_btn_click = "app_menu_logout_btn_click";
        public static final String app_menu_account_delete_btn_click = "app_menu_account_delete_btn_click";
        public static final String app_menu_charging_protection_btn_click = "app_menu_charging_protection_btn_click";
        public static final String app_menu_modify_id_btn_click = "app_menu_modify_id_btn_click";
        public static final String app_menu_tab_click = "app_menu_tab_click";
        public static final String app_menu_feedback_submit_btn_click = "app_menu_feedback_submit_btn_click";
        public static final String app_menu_privacy_policy_btn_click = "app_menu_privacy_policy_btn_click";
        public static final String app_menu_terms_of_usage_btn_click = "app_menu_terms_of_usage_btn_click";
        public static final String app_menu_contact_btn_click = "app_menu_contact_btn_click";
        public static final String app_menu_region_change = "app_menu_region_change";
        /**
         * 我的项目
         */
        public static final String app_project_create_btn_click = "app_project_create_btn_click";
        public static final String app_project_open_btn_click = "app_project_open_btn_click";
        public static final String app_project_edit_btn_click = "app_project_edit_btn_click";
        public static final String app_project_delete_btn_click = "app_project_delete_btn_click";
        public static final String app_project_rename_btn_click = "app_project_rename_btn_click";

        public static final String app_project_tab_click = "app_project_tab_click";
        public static final String app_project_sub_list_btn_click = "app_project_sub_list_btn_click";

        public static final String app_project_servo_btn_click = "app_project_servo_btn_click";
        public static final String app_project_servo_mode_btn_click = "app_project_servo_mode_btn_click";
        public static final String app_project_servo_model_angle_btn_click = "app_project_servo_model_angle_btn_click";
        public static final String app_project_servo_model_wheel_btn_click = "app_project_servo_model_wheel_btn_click";
        public static final String app_project_save_btn_click = "app_project_save_btn_click";
        public static final String app_project_bt_btn_click = "app_project_bt_btn_click";


        public static final String app_peripheral_abnormal = "app_peripheral_abnormal";
        public static final String app_peripheral_upgradle_result = "app_peripheral_upgradle_result";
        public static final String app_peripheral_upgradle_duration = "app_peripheral_upgradle_duration";
        public static final String app_servo_mode_conflict = "app_servo_mode_conflict";

        public static final String app_project_remote_play_widgets = "app_project_remote_play_widgets";
        public static final String app_launcher = "app_launcher";
        public static final String app_login = "app_login";
        public static final String app_user_active = "app_user_active";
    }


}
