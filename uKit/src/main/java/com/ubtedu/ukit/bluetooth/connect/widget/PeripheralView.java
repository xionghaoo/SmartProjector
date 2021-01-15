/**
 * @2018 UBTECH Robotics Corp. All rights reserved (C)
 **/
package com.ubtedu.ukit.bluetooth.connect.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ubtedu.alpha1x.ui.annotation.Visibility;
import com.ubtedu.deviceconnect.libs.base.model.URoComponentType;
import com.ubtedu.ukit.R;
import com.ubtedu.ukit.application.UKitApplication;
import com.ubtedu.ukit.bluetooth.BluetoothHelper;

import java.io.File;
import java.util.Locale;

import static com.ubtedu.deviceconnect.libs.base.model.URoComponentType.LED_STRIP;

/**
 * @Author naOKi
 * @Date 2018/11/22
 **/
public class PeripheralView extends FrameLayout {

    private static final String FONT_PATH = "font" + File.separator + "BebasNeue-Regular.otf";
    private static final Typeface TYPEFACE_FONT;

    static {
        TYPEFACE_FONT = Typeface.createFromAsset(UKitApplication.getInstance().getAssets(), FONT_PATH);
    }

    private TextView mIdTv;
    private ImageView mIconIv;
    private ImageView mMarkIv;
    private TextView mUpgradeTv;
    private TextView mIdNumberTv;

    private boolean isMotherBoard;
    private boolean isSteeringGear;
    private int steeringGearMode;
    private URoComponentType sensorType;
    private int peripheralParentId;
    private int peripheralId;
    private int resId;
    private int resUpgradeMaskId;
    private int resUpgradeFrontId;
    private boolean isUpgradeable;
    private boolean isUpgradeSuccess;
    private boolean isAbnormal;
    private boolean isConflict;
    private boolean isUnmatch;
    private String name;
    private String idNumber;
    private int sizePixel;

    public static final int STEERINGGEAR_MODE_NONE = 0;
    public static final int STEERINGGEAR_MODE_WHEEL = 1;
    public static final int STEERINGGEAR_MODE_ANGLE = 2;

    public PeripheralView(Context context) {
        this(context, null);
    }

    public PeripheralView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_bluetooth_connect, null);
        mIdTv = view.findViewById(R.id.item_bluetooth_peripheral_id_tv);
        mIconIv = view.findViewById(R.id.item_bluetooth_peripheral_icon_iv);
        mMarkIv = view.findViewById(R.id.item_bluetooth_peripheral_mark_iv);
        mUpgradeTv = view.findViewById(R.id.item_bluetooth_peripheral_upgrade_tv);
        mUpgradeTv.setTypeface(TYPEFACE_FONT);
        mIdNumberTv = view.findViewById(R.id.item_bluetooth_peripheral_idnumber_tv);
        addView(view, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    public void setIdVisible(@Visibility int visible){
        mIdTv.setVisibility(visible);
    }

    public void setAsMotherBoard(String name) {
        isMotherBoard = true;
        isSteeringGear = false;
        steeringGearMode = STEERINGGEAR_MODE_NONE;
        sensorType = null;
        peripheralId = -1;
        resId = R.drawable.controller;
        resUpgradeMaskId = R.drawable.controller_upgrade;
        resUpgradeFrontId = R.drawable.controller_upgrading;
        sizePixel = getContext().getResources().getDimensionPixelSize(R.dimen.ubt_dimen_225px);
//      this.name = getContext().getResources().getString(R.string.bluetooth_connect_motherboard);
        this.name = name;
        idNumber = null;
        applyChange();
    }

    public void setAsSteeringGear(int _id) {
        setAsSteeringGear(_id, STEERINGGEAR_MODE_NONE);
    }

    public void setAsSteeringGear(int _id, int _steeringGearMode) {
        isMotherBoard = false;
        isSteeringGear = true;
        steeringGearMode = _steeringGearMode;
        sensorType = null;
        peripheralId = _id;
        resId = R.drawable.controller;
        switch (_steeringGearMode) {
            case STEERINGGEAR_MODE_WHEEL:
                resId = R.drawable.servo2;
                resUpgradeMaskId = R.drawable.servo_upgrade;
                resUpgradeFrontId = R.drawable.servo_upgrading;
                break;
            default:
                resId = R.drawable.servo;
                resUpgradeMaskId = R.drawable.servo_upgrade;
                resUpgradeFrontId = R.drawable.servo_upgrading;
                break;
        }
        sizePixel = getContext().getResources().getDimensionPixelSize(R.dimen.ubt_dimen_160px);
        name = String.format(Locale.US, "ID-%s", String.valueOf(peripheralId));
        idNumber = String.format(Locale.US, "%s", String.valueOf(peripheralId));
        applyChange();
    }

    public void setAsSensor(URoComponentType _sensorType, int _id){
        setAsSensor(_sensorType,_id,-1);
    }

    public void setAsSensor(URoComponentType _sensorType, int _id,int parentId) {
        isMotherBoard = false;
        isSteeringGear = false;
        steeringGearMode = STEERINGGEAR_MODE_NONE;
        sensorType = _sensorType;
        peripheralId = _id;
        peripheralParentId=parentId;
        switch (_sensorType) {
            case INFRAREDSENSOR:
                resId = R.drawable.sensor_infrared;
                resUpgradeMaskId = R.drawable.sensor_infrared_upgrade;
                resUpgradeFrontId = R.drawable.sensor_infrared_upgrading;
                break;
            case TOUCHSENSOR:
                resId = R.drawable.sensor_touch;
                resUpgradeMaskId = R.drawable.sensor_touch_upgrade;
                resUpgradeFrontId = R.drawable.sensor_touch_upgrading;
                break;
            case LED:
                resId = R.drawable.sensor_light;
                resUpgradeMaskId = R.drawable.sensor_light_upgrade;
                resUpgradeFrontId = R.drawable.sensor_light_upgrading;
                break;
            case ULTRASOUNDSENSOR:
                resId = R.drawable.sound;
                resUpgradeMaskId = R.drawable.sound_upgrade;
                resUpgradeFrontId = R.drawable.sound_upgrading;
                break;
            case SPEAKER:
                resId = R.drawable.sensor_speaker;
                resUpgradeMaskId = R.drawable.sensor_speaker_upgrade;
                resUpgradeFrontId = R.drawable.sensor_speaker_upgrading;
                break;
            case COLORSENSOR:
                resId = R.drawable.sensor_color;
                resUpgradeMaskId = R.drawable.sensor_color_upgrade;
                resUpgradeFrontId = R.drawable.sensor_color_upgrading;
                break;
            case ENVIRONMENTSENSOR:
                resId = R.drawable.sensor_sensirion;
                resUpgradeMaskId = R.drawable.sensor_sensirion_upgrade;
                resUpgradeFrontId = R.drawable.sensor_sensirion_upgrading;
                break;
            case BRIGHTNESSSENSOR:
                resId = R.drawable.sensor_luminance;
                resUpgradeMaskId = R.drawable.sensor_luminance_upgrade;
                resUpgradeFrontId = R.drawable.sensor_luminance_upgrading;
                break;
            case SOUNDSENSOR:
                resId = R.drawable.sensor_sound;
                resUpgradeMaskId = R.drawable.sensor_sound_upgrade;
                resUpgradeFrontId = R.drawable.sensor_sound_upgrading;
                break;
            case MOTOR:
                resId = R.drawable.motor1;
                resUpgradeMaskId = R.drawable.motor_upgrade;
                resUpgradeFrontId = R.drawable.motor_upgrading;
                break;
            case LED_BELT:
                resId = R.drawable.ic_lightbox1;
                resUpgradeMaskId = R.drawable.ic_lightbox_black;
                resUpgradeFrontId = R.drawable.ic_lightbox_green;
                break;
            case LED_STRIP:
                resId = R.drawable.ic_light1;
                resUpgradeMaskId = R.drawable.ic_light1_black;
                resUpgradeFrontId = R.drawable.ic_light1_green;
                break;
            default:
                resId = R.drawable.bluetooth_abnormal_icon;
                break;
        }
        sizePixel = getContext().getResources().getDimensionPixelSize(R.dimen.ubt_dimen_160px);
        if (peripheralParentId!=-1){
            name=String.format("ID-%s-%s", String.valueOf(peripheralParentId),String.valueOf(peripheralId));
        }else {
            name = String.format("ID-%s", String.valueOf(peripheralId));
        }
        idNumber = null;
        applyChange();
    }

    public void updateUpgradeResult(boolean _isUpgradeSuccess) {
        mIconIv.setImageResource(resId);
        markAsFlag(!_isUpgradeSuccess && isUpgradeable, _isUpgradeSuccess, isAbnormal, isConflict);
    }

    public void markAsFlag(boolean _isUpgradeable, boolean _isUpgradeSuccess, boolean _isAbnormal, boolean _isConflict) {
        markAsFlag(_isUpgradeable, _isUpgradeSuccess, _isAbnormal, _isConflict, isUnmatch);
    }

    public void markAsFlag(boolean _isUpgradeable, boolean _isUpgradeSuccess, boolean _isAbnormal, boolean _isConflict, boolean _isUnmatch) {
        isUpgradeable = _isUpgradeable;
        isUpgradeSuccess = _isUpgradeSuccess;
        isAbnormal = _isAbnormal;
        isConflict = _isConflict;
        isUnmatch = _isUnmatch;
        mUpgradeTv.setVisibility(GONE);
        mIdNumberTv.setVisibility(VISIBLE);
        if (isAbnormal || isUnmatch) {
            mMarkIv.setVisibility(VISIBLE);
            mMarkIv.setImageResource(R.drawable.bluetooth_abnormal_icon);
        } else if (isUpgradeable || isConflict) {
            mMarkIv.setVisibility(VISIBLE);
            mMarkIv.setImageResource(R.drawable.bluetooth_upgrade);
        } else if (isUpgradeSuccess) {
            //产品同意去掉星星
//            mMarkIv.setVisibility(VISIBLE);
//            mMarkIv.setImageResource(R.drawable.bluetooth_upgrade_ok);
            mMarkIv.setVisibility(GONE);
        } else {
            mMarkIv.setVisibility(GONE);
        }
    }

    public void updateUpgradeProcess(int percent) {
        percent = Math.max(0, percent);
        percent = Math.min(100, percent);
        Drawable drawable = mIconIv.getDrawable();
        final int clipDrawableID = 0x9969;
        LayerDrawable layerDrawable;
        if (!(drawable instanceof LayerDrawable)
                || !(((LayerDrawable) drawable).findDrawableByLayerId(clipDrawableID) instanceof ClipDrawable)) {
            final int maskAlpha = 64;//64%透明
            Drawable bgDrawable = getResources().getDrawable(resId);
            Drawable maskDrawable = getResources().getDrawable(resUpgradeMaskId);
            maskDrawable.setAlpha((255 * maskAlpha) / 100);
            Drawable fgDrawable = getResources().getDrawable(resUpgradeFrontId);
            ClipDrawable fgClipDrawable = new ClipDrawable(fgDrawable, Gravity.BOTTOM, ClipDrawable.VERTICAL);
            layerDrawable = new LayerDrawable(new Drawable[]{bgDrawable, maskDrawable, fgClipDrawable});
            layerDrawable.setId(2, clipDrawableID);
        } else {
            layerDrawable = (LayerDrawable) drawable;
        }
        ClipDrawable clipDrawable = (ClipDrawable) layerDrawable.findDrawableByLayerId(clipDrawableID);
        clipDrawable.setLevel(100 * percent);
        mIconIv.setImageDrawable(layerDrawable);
        mUpgradeTv.setText(String.format(Locale.US, "%s%%", String.valueOf(Math.min(99, percent))));//100%不展示给用户
        mUpgradeTv.setVisibility(VISIBLE);
        mMarkIv.setVisibility(GONE);
        mIdNumberTv.setVisibility(GONE);
    }

    private void applyChange() {
        mIconIv.setImageResource(resId);
        mIdTv.setText(name);
        mIdNumberTv.setText(idNumber);
        ViewGroup.LayoutParams layoutParams = mIconIv.getLayoutParams();
        layoutParams.width = sizePixel;
        layoutParams.height = sizePixel;
        mIconIv.setLayoutParams(layoutParams);
        requestLayout();
    }

    public View getIconView(){
        return mIconIv;
    }

    public boolean isMotherBoard() {
        return isMotherBoard;
    }

    public boolean isSteeringGear() {
        return isSteeringGear;
    }

    public int getSteeringGearMode() {
        return steeringGearMode;
    }

    public URoComponentType getSensorType() {
        return sensorType;
    }

    public int getPeripheraId() {
        return peripheralId;
    }

    public boolean isUpgradeable() {
        return isUpgradeable;
    }

    public boolean isUpgradeSuccess() {
        return isUpgradeSuccess;
    }

    public boolean isAbnormal() {
        return isAbnormal;
    }

    public boolean isConflict() {
        return isConflict;
    }

    public boolean isUnmatch() {
        return isUnmatch;
    }
}
