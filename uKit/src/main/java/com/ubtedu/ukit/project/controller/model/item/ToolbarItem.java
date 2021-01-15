package com.ubtedu.ukit.project.controller.model.item;

import com.ubtedu.ukit.project.controller.model.define.ToolbarType;
import com.ubtedu.ukit.project.controller.model.define.WidgetType;
import com.ubtedu.ukit.project.controller.utils.ConfigLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @Author naOKi
 * @Date 2018/11/17
 **/
public class ToolbarItem {

    private ToolbarType type;
    private WidgetType[] widgetItems;

    public ToolbarItem(ToolbarType type, WidgetType[] widgetItems) {
        this.type = type;
        this.widgetItems = widgetItems;
    }

    public ToolbarType getType() {
        return type;
    }

    public WidgetType[] getWidgetItems() {
        return widgetItems;
    }

    private static ToolbarItem newInstance(JSONObject jsonObject) {
        try {
            ToolbarType toolbarType = ToolbarType.findToolbarTypeByCategoryName(jsonObject.getString("categoryName"));
            JSONArray widgetList = jsonObject.getJSONArray("widgetList");
            ArrayList<WidgetType> list = new ArrayList<>();
            for(int i = 0; i < widgetList.length(); i++) {
                list.add(WidgetType.findWidgetTypeByWidgetName(widgetList.getString(i)));
            }
            WidgetType[] widgetItems = list.toArray(new WidgetType[list.size()]);
            return new ToolbarItem(toolbarType, widgetItems);
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    private static ToolbarItem[] cacheToolbarConfig = null;

    public static ToolbarType findToolbarTypeByWidgetType(WidgetType widgetType) {
        ToolbarItem[] toolbarItems = loadFromConfig();
        for(ToolbarItem toolbarItem : toolbarItems) {
            for(WidgetType widgetItem : toolbarItem.widgetItems) {
                if(widgetItem.equals(widgetType)) {
                    return toolbarItem.type;
                }
            }
        }
        return null;
    }

    public static ToolbarItem[] loadFromConfig() {
        synchronized (ToolbarItem.class) {
            if (cacheToolbarConfig == null) {
                try {
                    ArrayList<ToolbarItem> result = new ArrayList<>();
                    JSONObject jsonObject = ConfigLoader.loadFromAssets("controller/UKToolbox.json");
                    JSONArray jsonArray = jsonObject.getJSONArray("UKit");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        ToolbarItem toolbarItem = newInstance(item);
                        if (toolbarItem != null) {
                            result.add(toolbarItem);
                        }
                    }
                    cacheToolbarConfig = result.toArray(new ToolbarItem[result.size()]);
                } catch (Exception e) {
                    // ignore
                }
            }
            return cacheToolbarConfig;
        }
    }

}
