package com.ubtedu.ukit.project.controller.model.item;

import android.util.Size;

import com.ubtedu.ukit.project.controller.model.define.ToolbarType;
import com.ubtedu.ukit.project.controller.model.define.WidgetProperty;
import com.ubtedu.ukit.project.controller.model.define.WidgetType;
import com.ubtedu.ukit.project.controller.utils.ConfigLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @Author naOKi
 * @Date 2018/11/17
 **/
public class WidgetItem {
    private WidgetType widgetType;
    private ToolbarType group;
    private WidgetProperty[] widgetProperties;
    private int spanV;
    private int spanH;

    public WidgetItem(WidgetType widgetType, ToolbarType group, WidgetProperty[] widgetProperties, int spanV, int spanH) {
        this.widgetType = widgetType;
        this.group = group;
        this.widgetProperties = widgetProperties;
        this.spanV = spanV;
        this.spanH = spanH;
    }

    public WidgetType getWidgetType() {
        return widgetType;
    }

    public ToolbarType getGroup() {
        return group;
    }

    public WidgetProperty[] getWidgetProperties() {
        return widgetProperties;
    }

    public int getSpanV() {
        return spanV;
    }

    public int getSpanH() {
        return spanH;
    }

    private static WidgetItem newInstance(JSONObject jsonObject) {
        try {
            WidgetType widgetType = WidgetType.findWidgetTypeByWidgetName(jsonObject.getString("widgetName"));
            ToolbarType group = ToolbarItem.findToolbarTypeByWidgetType(widgetType);
            int spanV = jsonObject.getInt("spanV");
            int spanH = jsonObject.getInt("spanH");
            JSONArray property = jsonObject.getJSONArray("property");
            ArrayList<WidgetProperty> list = new ArrayList<>();
            for(int i = 0; i < property.length(); i++) {
                list.add(WidgetProperty.findWidgetPropertyByPropertyName(property.getString(i)));
            }
            WidgetProperty[] widgetProperties = list.toArray(new WidgetProperty[list.size()]);
            return new WidgetItem(widgetType, group, widgetProperties, spanV, spanH);
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    private static WidgetItem[] cacheWidgetConfig = null;

    public static WidgetItem[] loadFromConfig() {
        synchronized (ToolbarItem.class) {
            if (cacheWidgetConfig == null) {
                try {
                    ArrayList<WidgetItem> result = new ArrayList<>();
                    JSONObject jsonObject = ConfigLoader.loadFromAssets("controller/UKWidget.json");
                    JSONArray jsonArray = jsonObject.getJSONArray("UKWidget");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        WidgetItem widgetItem = newInstance(item);
                        if (widgetItem != null) {
                            result.add(widgetItem);
                        }
                    }
                    cacheWidgetConfig = result.toArray(new WidgetItem[result.size()]);
                } catch (Exception e) {
                    // ignore
                }
            }
            return cacheWidgetConfig;
        }
    }
    
    public static WidgetItem findWidgetItemByWidgetType(WidgetType widgetType) {
        WidgetItem[] widgetItems = loadFromConfig();
        for(WidgetItem widgetItem : widgetItems) {
            if(widgetItem.widgetType.equals(widgetType)) {
                return widgetItem;
            }
        }
        return null;
    }

    public static int findMaxSpanH() {
        WidgetItem[] widgetItems = loadFromConfig();
        int maxSpanH = -1;
        for(WidgetItem widgetItem : widgetItems) {
            if(widgetItem.spanH > maxSpanH) {
                maxSpanH = widgetItem.spanH;
            }
        }
        return maxSpanH;
    }

    public static Size getSpanSizeByWidgetType(WidgetType type) {
        Size result = null;
        WidgetItem[] widgetItems = loadFromConfig();
        for(WidgetItem widgetItem : widgetItems) {
            if(widgetItem.widgetType.equals(type)) {
                result = new Size(widgetItem.spanH, widgetItem.spanV);
                break;
            }
        }
        return result;
    }
}
