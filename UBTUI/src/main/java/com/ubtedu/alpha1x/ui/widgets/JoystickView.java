package com.ubtedu.alpha1x.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.ubtedu.base.ui.R;

import java.util.ArrayList;
import java.util.List;

public class JoystickView extends View {
    private static final String TAG = "JoystickView";

    private static final int DEFAULT_SIZE = 400;
    private static final int DEFAULT_ROCKER_RADIUS = DEFAULT_SIZE / 8;

    private Paint mAreaBackgroundPaint;
    private Paint mJoystickPaint;

    private Point mJoystickPosition;
    private Point mCenterPoint;

    private int mAreaRadius;
    private int mcenterRadius;

    private CallBackMode mCallBackMode = CallBackMode.CALL_BACK_MODE_MOVE;
    private OnAngleChangeListener mOnAngleChangeListener;
    private OnShakeListener mOnShakeListener;
    private OnRotateListener mOnRotateListener;

    private DirectionMode mDirectionMode;
    private Direction tempDirection = Direction.DIRECTION_CENTER;
    // 角度
    private static final double ANGLE_0 = 0;
    private static final double ANGLE_360 = 360;
    // 360°水平方向平分2份的边缘角度
    private static final double ANGLE_HORIZONTAL_2D_OF_0P = 90;
    private static final double ANGLE_HORIZONTAL_2D_OF_1P = 270;
    // 360°垂直方向平分2份的边缘角度
    private static final double ANGLE_VERTICAL_2D_OF_0P = 0;
    private static final double ANGLE_VERTICAL_2D_OF_1P = 180;
    // 360°平分4份的边缘角度
    private static final double ANGLE_4D_OF_0P = 0;
    private static final double ANGLE_4D_OF_1P = 90;
    private static final double ANGLE_4D_OF_2P = 180;
    private static final double ANGLE_4D_OF_3P = 270;
    // 360°平分4份的边缘角度(旋转45度)
    private static final double ANGLE_ROTATE45_4D_OF_0P = 45;
    private static final double ANGLE_ROTATE45_4D_OF_1P = 135;
    private static final double ANGLE_ROTATE45_4D_OF_2P = 225;
    private static final double ANGLE_ROTATE45_4D_OF_3P = 315;

    // 360°平分8份的边缘角度
    private static final double ANGLE_8D_OF_0P = 22.5;
    private static final double ANGLE_8D_OF_1P = 67.5;
    private static final double ANGLE_8D_OF_2P = 112.5;
    private static final double ANGLE_8D_OF_3P = 157.5;
    private static final double ANGLE_8D_OF_4P = 202.5;
    private static final double ANGLE_8D_OF_5P = 247.5;
    private static final double ANGLE_8D_OF_6P = 292.5;
    private static final double ANGLE_8D_OF_7P = 337.5;

    // 摇杆可移动区域背景
    private static final int AREA_BACKGROUND_MODE_PIC = 0;
    private static final int AREA_BACKGROUND_MODE_COLOR = 1;
    private static final int AREA_BACKGROUND_MODE_XML = 2;
    private static final int AREA_BACKGROUND_MODE_DEFAULT = 3;
    private int mAreaBackgroundMode = AREA_BACKGROUND_MODE_DEFAULT;
    private Bitmap mAreaBitmap;
    private int mAreaColor;
    // 摇杆背景
    private static final int JOYSTICK_BACKGROUND_MODE_PIC = 4;
    private static final int JOYSTICK_BACKGROUND_MODE_COLOR = 5;
    private static final int JOYSTICK_BACKGROUND_MODE_XML = 6;
    private static final int JOYSTICK_BACKGROUND_MODE_DEFAULT = 7;
    private int mJoystickBackgroundMode = JOYSTICK_BACKGROUND_MODE_DEFAULT;
    private Bitmap mJoystickBitmap;
    private int mJoystickColor;

    // 收集历史手势方向的时间段
    private final int DELAY_INTERVAL = 200;
    // 处理角度集，判断顺逆时针
    private Handler mRotateHandler = null;
    private boolean isRotating = false;
    private List<Direction> mLastDirections = new ArrayList<>();

    // 背景矩形
    private Rect bgSrcRect, bgDstRect;
    // 摇杆矩形
    private Rect joystickSrcRect, joystickDstRect;

    // 摇杆控件样式相关变量
    private Bitmap mNormalAreaBitmap, mNormalJoystickBitmap;
    private Bitmap mPressedAreaBitmap, mPressedJoystickBitmap;
    private int mPressedAreaColor, mPressedJoystickColor;
    private int mNormalAreaColor, mNormalJoystickColor;
    private int mAreaPressedBGMode = AREA_BACKGROUND_MODE_DEFAULT;
    private int mJoystickPressedBGMode = JOYSTICK_BACKGROUND_MODE_DEFAULT;
    private int mAreaNormalBGMode = AREA_BACKGROUND_MODE_DEFAULT;
    private int mJoystickNormalBGMode = JOYSTICK_BACKGROUND_MODE_DEFAULT;
    private int measuredWidth = 0;
    private int measuredHeight = 0;

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 获取自定义属性
        initAttribute(context, attrs);

        if (isInEditMode()) {
            Log.i(TAG, "JoystickView: isInEditMode");
        }
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        // 移动区域画笔
        mAreaBackgroundPaint = new Paint();
//        mAreaBackgroundPaint.setAntiAlias(true);

        // 摇杆画笔
        mJoystickPaint = new Paint();
        mJoystickPaint.setAntiAlias(true);

        // 中心点
        mCenterPoint = new Point();
        // 摇杆位置
        mJoystickPosition = new Point();
    }

    /**
     * 获取属性
     *
     * @param context context
     * @param attrs   attrs
     */
    private void initAttribute(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JoystickView);

        // 背景
        Drawable areaBackground = typedArray.getDrawable(R.styleable.JoystickView_areaBackground);
        if (null != areaBackground) {
            // 设置了背景
            if (areaBackground instanceof BitmapDrawable) {
                // 设置了一张图片
                mAreaBitmap = ((BitmapDrawable) areaBackground).getBitmap();
                mAreaBackgroundMode = AREA_BACKGROUND_MODE_PIC;
                mNormalAreaBitmap = mAreaBitmap;
            } else if (areaBackground instanceof GradientDrawable) {
                // XML
                mAreaBitmap = drawable2Bitmap(areaBackground);
                mAreaBackgroundMode = AREA_BACKGROUND_MODE_XML;
                mNormalAreaBitmap = mAreaBitmap;
            } else if (areaBackground instanceof ColorDrawable) {
                // 色值
                mAreaColor = ((ColorDrawable) areaBackground).getColor();
                mAreaBackgroundMode = AREA_BACKGROUND_MODE_COLOR;
                mNormalAreaColor = mAreaColor;
            } else {
                // 其他形式
                mAreaBackgroundMode = AREA_BACKGROUND_MODE_DEFAULT;
            }
        } else {
            // 没有设置背景
            mAreaBackgroundMode = AREA_BACKGROUND_MODE_DEFAULT;
        }
        mAreaNormalBGMode = mAreaBackgroundMode;
        // 摇杆圆圈
        Drawable joystickBackground = typedArray.getDrawable(R.styleable.JoystickView_joystickBackground);
        if (null != joystickBackground) {
            // 设置了摇杆圆圈图
            if (joystickBackground instanceof BitmapDrawable) {
                // 图片
                mJoystickBitmap = ((BitmapDrawable) joystickBackground).getBitmap();
                mJoystickBackgroundMode = JOYSTICK_BACKGROUND_MODE_PIC;
                mNormalJoystickBitmap = mJoystickBitmap;
            } else if (joystickBackground instanceof GradientDrawable) {
                // XML
                mJoystickBitmap = drawable2Bitmap(joystickBackground);
                mJoystickBackgroundMode = JOYSTICK_BACKGROUND_MODE_XML;
                mNormalJoystickBitmap = mJoystickBitmap;
            } else if (joystickBackground instanceof ColorDrawable) {
                // 色值
                mJoystickColor = ((ColorDrawable) joystickBackground).getColor();
                mJoystickBackgroundMode = JOYSTICK_BACKGROUND_MODE_COLOR;
                mNormalJoystickColor = mJoystickColor;
            } else {
                // 其他形式
                mJoystickBackgroundMode = JOYSTICK_BACKGROUND_MODE_DEFAULT;
            }
        } else {
            // 没有设置摇杆背景
            mJoystickBackgroundMode = JOYSTICK_BACKGROUND_MODE_DEFAULT;
        }
        mJoystickNormalBGMode = mJoystickBackgroundMode;

        // 背景
        Drawable areaPressedBackground = typedArray.getDrawable(R.styleable.JoystickView_areaPressedBackground);
        if (null != areaPressedBackground) {
            // 按下后可移动区域背景
            if (areaPressedBackground instanceof BitmapDrawable) {
                // 图片
                mPressedAreaBitmap = ((BitmapDrawable) areaPressedBackground).getBitmap();
                mAreaPressedBGMode = AREA_BACKGROUND_MODE_PIC;
            } else if (areaPressedBackground instanceof GradientDrawable) {
                // XML
                mPressedAreaBitmap = drawable2Bitmap(areaPressedBackground);
                mAreaPressedBGMode = AREA_BACKGROUND_MODE_XML;
            } else if (areaPressedBackground instanceof ColorDrawable) {
                // 色值
                mPressedAreaColor = ((ColorDrawable) areaPressedBackground).getColor();
                mAreaPressedBGMode = AREA_BACKGROUND_MODE_COLOR;
            } else {
                mPressedAreaBitmap = null;
                mPressedAreaColor = 0;
                mAreaPressedBGMode = AREA_BACKGROUND_MODE_DEFAULT;
            }
        } else {
            // 没有设置按下后可移动区域背景
            mPressedAreaBitmap = null;
            mPressedAreaColor = 0;
            mAreaPressedBGMode = AREA_BACKGROUND_MODE_DEFAULT;
        }
        // 按下后摇杆的背景
        Drawable joystickPressedBackground = typedArray.getDrawable(R.styleable.JoystickView_joystickPressedBackground);
        if (null != joystickPressedBackground) {
            // 按下后可移动区域背景
            if (joystickPressedBackground instanceof BitmapDrawable) {
                // 图片
                mPressedJoystickBitmap = ((BitmapDrawable) joystickPressedBackground).getBitmap();
                mJoystickPressedBGMode = JOYSTICK_BACKGROUND_MODE_PIC;
            } else if (joystickPressedBackground instanceof GradientDrawable) {
                // XML
                mPressedJoystickBitmap = drawable2Bitmap(joystickPressedBackground);
                mJoystickPressedBGMode = JOYSTICK_BACKGROUND_MODE_XML;
            } else if (joystickPressedBackground instanceof ColorDrawable) {
                // 色值
                mPressedJoystickColor = ((ColorDrawable) joystickPressedBackground).getColor();
                mJoystickPressedBGMode = JOYSTICK_BACKGROUND_MODE_COLOR;
            } else {
                mPressedJoystickBitmap = null;
                mPressedJoystickColor = 0;
                mJoystickPressedBGMode = JOYSTICK_BACKGROUND_MODE_DEFAULT;
            }
        } else {
            // 没有设置按下后可移动区域背景
            mPressedJoystickBitmap = null;
            mPressedJoystickColor = 0;
            mJoystickPressedBGMode = JOYSTICK_BACKGROUND_MODE_DEFAULT;
        }

        // 摇杆半径
        mcenterRadius = typedArray.getDimensionPixelOffset(R.styleable.JoystickView_centerRadius,
                DEFAULT_ROCKER_RADIUS);

        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth, measureHeight;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY) {
            // 具体的值和match_parent
            measureWidth = widthSize;
        } else {
            // wrap_content
            measureWidth = DEFAULT_SIZE;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            measureHeight = heightSize;
        } else {
            measureHeight = DEFAULT_SIZE;
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        measuredWidth = measuredWidth == 0 ? getMeasuredWidth() : measuredWidth;
        measuredHeight = measuredHeight == 0 ? getMeasuredHeight() : measuredHeight;

        // 中心点
        mCenterPoint.set(measuredWidth / 2, measuredHeight / 2);
        // 背景的半径
        mAreaRadius = (measuredWidth <= measuredHeight) ? measuredWidth / 2 : measuredHeight / 2;

        // 摇杆位置
        if (0 == mJoystickPosition.x || 0 == mJoystickPosition.y) {
            mJoystickPosition.set(mCenterPoint.x, mCenterPoint.y);
        }

        // 画背景
        mJoystickPaint.setAlpha(0x7f);
        if (AREA_BACKGROUND_MODE_PIC == mAreaBackgroundMode ||
                AREA_BACKGROUND_MODE_XML == mAreaBackgroundMode) {
            // 图片
            if (bgSrcRect == null) {
                bgSrcRect = new Rect();
            }
            if (bgDstRect == null) {
                bgDstRect = new Rect();
            }
            bgSrcRect.set(0, 0, mAreaBitmap.getWidth(), mAreaBitmap.getHeight());
            bgDstRect.set(mCenterPoint.x - mAreaRadius, mCenterPoint.y - mAreaRadius,
                    mCenterPoint.x + mAreaRadius, mCenterPoint.y + mAreaRadius);
            canvas.drawBitmap(mAreaBitmap, bgSrcRect, bgDstRect, mAreaBackgroundPaint);
        } else if (AREA_BACKGROUND_MODE_COLOR == mAreaBackgroundMode) {
            // 色值
            mAreaBackgroundPaint.setColor(mAreaColor);
            canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mAreaRadius, mAreaBackgroundPaint);
        } else {
            // 其他或者未设置
            mAreaBackgroundPaint.setColor(Color.GRAY);
            canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mAreaRadius, mAreaBackgroundPaint);
        }

        // 画摇杆
        mJoystickPaint.setAlpha(0xff);
        if (JOYSTICK_BACKGROUND_MODE_PIC == mJoystickBackgroundMode || JOYSTICK_BACKGROUND_MODE_XML == mJoystickBackgroundMode) {
            // 图片
            if (joystickSrcRect == null) {
                joystickSrcRect = new Rect();
            }
            if (joystickDstRect == null) {
                joystickDstRect = new Rect();
            }
            joystickSrcRect.set(0, 0, mJoystickBitmap.getWidth(), mJoystickBitmap.getHeight());
            joystickDstRect.set(mJoystickPosition.x - mcenterRadius, mJoystickPosition.y - mcenterRadius,
                    mJoystickPosition.x + mcenterRadius, mJoystickPosition.y + mcenterRadius);
            canvas.drawBitmap(mJoystickBitmap, joystickSrcRect, joystickDstRect, mJoystickPaint);
        } else if (JOYSTICK_BACKGROUND_MODE_COLOR == mJoystickBackgroundMode) {
            // 色值
            mJoystickPaint.setColor(mJoystickColor);
            canvas.drawCircle(mJoystickPosition.x, mJoystickPosition.y, mcenterRadius, mJoystickPaint);
        } else {
            // 其他或者未设置
            mJoystickPaint.setColor(Color.RED);
            canvas.drawCircle(mJoystickPosition.x, mJoystickPosition.y, mcenterRadius, mJoystickPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:// 按下
                changeViewBitmaps(true);
                // 回调 开始
                callBackStart();
                return true;

            case MotionEvent.ACTION_MOVE:// 移动
                float moveX = event.getX();
                float moveY = event.getY();
                mJoystickPosition = getJoystickPositionPoint(mCenterPoint, new Point((int) moveX, (int) moveY), mAreaRadius, mcenterRadius);
                moveJoystick(mJoystickPosition.x, mJoystickPosition.y);
                break;

            case MotionEvent.ACTION_UP:// 抬起
            case MotionEvent.ACTION_CANCEL:// 移出区域
                changeViewBitmaps(false);
                // 回调 结束
                callBackFinish();
                float upX = event.getX();
                float upY = event.getY();
                moveJoystick(mCenterPoint.x, mCenterPoint.y);
                break;
        }
        return false;
    }

    /**
     * 改变控件背景图片：背景区域和触点的图片
     *
     * @param isPressed 是否按下
     */
    private void changeViewBitmaps(boolean isPressed) {
        if (isPressed) {
            // 按下状态
            mAreaBackgroundMode = mAreaPressedBGMode;
            switch (mAreaBackgroundMode) {
                case AREA_BACKGROUND_MODE_PIC:
                case AREA_BACKGROUND_MODE_XML:
                    if (mPressedAreaBitmap != null) {
                        mAreaBitmap = mPressedAreaBitmap;
                    } else {
                        mAreaBackgroundMode = AREA_BACKGROUND_MODE_DEFAULT;
                    }
                    break;

                case AREA_BACKGROUND_MODE_COLOR:
                    mAreaColor = mPressedAreaColor;
                    break;

                default:
                    mAreaBackgroundMode = AREA_BACKGROUND_MODE_DEFAULT;
            }
            mJoystickBackgroundMode = mJoystickPressedBGMode;
            switch (mJoystickBackgroundMode) {
                case JOYSTICK_BACKGROUND_MODE_PIC:
                case JOYSTICK_BACKGROUND_MODE_XML:
                    if (mPressedJoystickBitmap != null) {
                        mJoystickBitmap = mPressedJoystickBitmap;
                    } else {
                        mJoystickBackgroundMode = JOYSTICK_BACKGROUND_MODE_DEFAULT;
                    }
                    break;

                case JOYSTICK_BACKGROUND_MODE_COLOR:
                    mJoystickColor = mPressedJoystickColor;
                    break;

                default:
                    mJoystickBackgroundMode = JOYSTICK_BACKGROUND_MODE_DEFAULT;
            }

        } else {
            // 未按下状态
            mAreaBackgroundMode = mAreaNormalBGMode;
            switch (mAreaBackgroundMode) {
                case AREA_BACKGROUND_MODE_PIC:
                case AREA_BACKGROUND_MODE_XML:
                    if (mNormalAreaBitmap != null) {
                        mAreaBitmap = mNormalAreaBitmap;
                    } else {
                        mAreaBackgroundMode = AREA_BACKGROUND_MODE_DEFAULT;
                    }
                    break;

                case AREA_BACKGROUND_MODE_COLOR:
                    mAreaColor = mNormalAreaColor;
                    break;

                default:
                    mAreaBackgroundMode = AREA_BACKGROUND_MODE_DEFAULT;
            }

            mJoystickBackgroundMode = mJoystickNormalBGMode;
            switch (mJoystickBackgroundMode) {
                case JOYSTICK_BACKGROUND_MODE_PIC:
                case JOYSTICK_BACKGROUND_MODE_XML:
                    if (mNormalJoystickBitmap != null) {
                        mJoystickBitmap = mNormalJoystickBitmap;
                    } else {
                        mJoystickBackgroundMode = JOYSTICK_BACKGROUND_MODE_DEFAULT;
                    }
                    break;

                case JOYSTICK_BACKGROUND_MODE_COLOR:
                    mJoystickColor = mNormalJoystickColor;
                    break;

                default:
                    mJoystickBackgroundMode = JOYSTICK_BACKGROUND_MODE_DEFAULT;
            }
        }
        postInvalidate();
    }

    /**
     * 获取摇杆实际要显示的位置（点）
     *
     * @param centerPoint  中心点
     * @param touchPoint   触摸点
     * @param regionRadius 摇杆可活动区域半径
     * @param rockerRadius 摇杆半径
     * @return 摇杆实际显示的位置（点）
     */
    private Point getJoystickPositionPoint(Point centerPoint, Point touchPoint, float regionRadius, float rockerRadius) {
        // 两点在X轴的距离
        float lenX = (float) (touchPoint.x - centerPoint.x);
        // 两点在Y轴距离
        float lenY = (float) (touchPoint.y - centerPoint.y);
        // 两点距离
        float lenXY = (float) Math.sqrt((double) (lenX * lenX + lenY * lenY));
        // 计算弧度
        double radian = Math.acos(lenX / lenXY) * (touchPoint.y < centerPoint.y ? -1 : 1);
        // 计算角度
        double angle = radian2Angle(radian);
        if (Math.abs(touchPoint.x - mCenterPoint.x) > getResources().getDimension(R.dimen.ubt_dimen_10px) &&
                Math.abs(touchPoint.y - mCenterPoint.y) > getResources().getDimension(R.dimen.ubt_dimen_10px)) {
            // 到达方向热区，响应四个方向，中心点附近不响应，避免手指小距离误滑动到其他非期望位置
            callBack(angle);
        }

        if (lenXY + rockerRadius <= regionRadius) { // 触摸位置在可活动范围内
            return touchPoint;
        } else { // 触摸位置在可活动范围以外
            // 计算要显示的位置
            int showPointX = (int) (centerPoint.x + (regionRadius - rockerRadius) * Math.cos(radian));
            int showPointY = (int) (centerPoint.y + (regionRadius - rockerRadius) * Math.sin(radian));
            return new Point(showPointX, showPointY);
        }
    }

    /**
     * 移动摇杆到指定位置
     *
     * @param x x坐标
     * @param y y坐标
     */
    private void moveJoystick(float x, float y) {
        mJoystickPosition.set((int) x, (int) y);
        invalidate();
    }

    /**
     * 弧度转角度
     *
     * @param radian 弧度
     * @return 角度[0, 360)
     */
    private double radian2Angle(double radian) {
        double tmp = Math.round(radian / Math.PI * 180);
        return tmp >= 0 ? tmp : 360 + tmp;
    }

    /**
     * Drawable 转 Bitmap
     *
     * @param drawable Drawable
     * @return Bitmap
     */
    private Bitmap drawable2Bitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * callback -- start
     */
    private void callBackStart() {
        tempDirection = Direction.DIRECTION_CENTER;
        if (null != mOnAngleChangeListener) {
            mOnAngleChangeListener.onStart();
        }
        if (null != mOnShakeListener) {
            mOnShakeListener.onStart();
        }
        if (null != mOnRotateListener) {
            mOnRotateListener.onStart();
        }
    }

    /**
     * 回调
     * 返回参数
     *
     * @param angle 摇动角度
     */
    private void callBack(double angle) {
        if (null != mOnAngleChangeListener) {
            mOnAngleChangeListener.angle(angle);
        }
        Direction direction = Direction.DIRECTION_CENTER;

        switch (mDirectionMode) {
            case DIRECTION_2_HORIZONTAL:// 左右方向
                if (ANGLE_0 <= angle && ANGLE_HORIZONTAL_2D_OF_0P > angle || ANGLE_HORIZONTAL_2D_OF_1P <= angle && ANGLE_360 > angle) {
                    // 右
                    direction = Direction.DIRECTION_RIGHT;
                } else if (ANGLE_HORIZONTAL_2D_OF_0P <= angle && ANGLE_HORIZONTAL_2D_OF_1P > angle) {
                    // 左
                    direction = Direction.DIRECTION_LEFT;
                }
                break;
            case DIRECTION_2_VERTICAL:// 上下方向
                if (ANGLE_VERTICAL_2D_OF_0P <= angle && ANGLE_VERTICAL_2D_OF_1P > angle) {
                    // 下
                    direction = Direction.DIRECTION_DOWN;
                } else if (ANGLE_VERTICAL_2D_OF_1P <= angle && ANGLE_360 > angle) {
                    // 上
                    direction = Direction.DIRECTION_UP;
                }
                break;
            case DIRECTION_4_ROTATE_0:// 四个方向
                if (ANGLE_4D_OF_0P <= angle && ANGLE_4D_OF_1P > angle) {
                    // 右下
                    direction = Direction.DIRECTION_DOWN_RIGHT;
                } else if (ANGLE_4D_OF_1P <= angle && ANGLE_4D_OF_2P > angle) {
                    // 左下
                    direction = Direction.DIRECTION_DOWN_LEFT;
                } else if (ANGLE_4D_OF_2P <= angle && ANGLE_4D_OF_3P > angle) {
                    // 左上
                    direction = Direction.DIRECTION_UP_LEFT;
                } else if (ANGLE_4D_OF_3P <= angle && ANGLE_360 > angle) {
                    // 右上
                    direction = Direction.DIRECTION_UP_RIGHT;
                }
                break;
            case DIRECTION_4_ROTATE_45:// 四个方向 旋转45度
                if (ANGLE_0 <= angle && ANGLE_ROTATE45_4D_OF_0P > angle || ANGLE_ROTATE45_4D_OF_3P <= angle && ANGLE_360 > angle) {
                    // 右
                    direction = Direction.DIRECTION_RIGHT;
                } else if (ANGLE_ROTATE45_4D_OF_0P <= angle && ANGLE_ROTATE45_4D_OF_1P > angle) {
                    // 下
                    direction = Direction.DIRECTION_DOWN;
                } else if (ANGLE_ROTATE45_4D_OF_1P <= angle && ANGLE_ROTATE45_4D_OF_2P > angle) {
                    // 左
                    direction = Direction.DIRECTION_LEFT;
                } else if (ANGLE_ROTATE45_4D_OF_2P <= angle && ANGLE_ROTATE45_4D_OF_3P > angle) {
                    // 上
                    direction = Direction.DIRECTION_UP;
                }
                break;
            case DIRECTION_8:// 八个方向
                if (ANGLE_0 <= angle && ANGLE_8D_OF_0P > angle || ANGLE_8D_OF_7P <= angle && ANGLE_360 > angle) {
                    // 右
                    direction = Direction.DIRECTION_RIGHT;
                } else if (ANGLE_8D_OF_0P <= angle && ANGLE_8D_OF_1P > angle) {
                    // 右下
                    direction = Direction.DIRECTION_DOWN_RIGHT;
                } else if (ANGLE_8D_OF_1P <= angle && ANGLE_8D_OF_2P > angle) {
                    // 下
                    direction = Direction.DIRECTION_DOWN;
                } else if (ANGLE_8D_OF_2P <= angle && ANGLE_8D_OF_3P > angle) {
                    // 左下
                    direction = Direction.DIRECTION_DOWN_LEFT;
                } else if (ANGLE_8D_OF_3P <= angle && ANGLE_8D_OF_4P > angle) {
                    // 左
                    direction = Direction.DIRECTION_LEFT;
                } else if (ANGLE_8D_OF_4P <= angle && ANGLE_8D_OF_5P > angle) {
                    // 左上
                    direction = Direction.DIRECTION_UP_LEFT;
                } else if (ANGLE_8D_OF_5P <= angle && ANGLE_8D_OF_6P > angle) {
                    // 上
                    direction = Direction.DIRECTION_UP;
                } else if (ANGLE_8D_OF_6P <= angle && ANGLE_8D_OF_7P > angle) {
                    // 右上
                    direction = Direction.DIRECTION_UP_RIGHT;
                }
                break;
            default:
                break;
        }

        if (mCallBackMode == CallBackMode.CALL_BACK_MODE_STATE_CHANGE && tempDirection == direction) {
            return;
        }

        tempDirection = direction;
//        analiseRotate(direction);
        if (null != mOnShakeListener) {
            mOnShakeListener.direction(direction, !isRotating);
        }
    }

    /**
     * 根据手势移动的历史方向，判断是旋转还是平移，是顺时针还是逆时针
     */
    private void analiseRotate(final Direction direction) {
        if (mLastDirections.size() >= 1 && mLastDirections.get(mLastDirections.size() - 1) != direction
                || mLastDirections.size() == 0) {
            mLastDirections.add(direction);
        }
        if (mRotateHandler == null) {
            mRotateHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 0) {
                        Direction last, next;
                        int cwCount = 0;  // 顺时针次数
                        for (Direction dir : mLastDirections) {
                        }
                        for (int i = 0; i < mLastDirections.size() - 1; i++) {
                            last = mLastDirections.get(i);
                            next = mLastDirections.get(i + 1);
                            if (last == Direction.DIRECTION_LEFT) {
                                if (next == Direction.DIRECTION_DOWN) {
                                    cwCount--;
                                } else if (next == Direction.DIRECTION_UP) {
                                    cwCount++;
                                }
                            } else if (last.ordinal() < next.ordinal()) {
                                cwCount++;
                            } else if (last.ordinal() > next.ordinal()) {
                                cwCount--;
                            }
                        }

                        if (cwCount != 0) {
                            isRotating = true;
                            if (null != mOnRotateListener) {
                                mOnRotateListener.onClockWise(cwCount > 0);
                            }
                        } else {
                            isRotating = false;
                        }
                    }
                }
            };
            mRotateHandler.sendEmptyMessageDelayed(0, DELAY_INTERVAL);
        }
    }

    /**
     * 回调结束状态
     */
    private void callBackFinish() {
        tempDirection = Direction.DIRECTION_CENTER;
        if (null != mOnAngleChangeListener) {
            mOnAngleChangeListener.onFinish();
        }
        if (null != mOnShakeListener) {
            mOnShakeListener.onFinish();
        }
        if (null != mOnRotateListener) {
            mOnRotateListener.onFinish();
        }
        resetRotateVars();
    }

    /**
     * 重置角度、旋转相关的变量
     */
    private void resetRotateVars() {
        mLastDirections.clear();
        if (null != mRotateHandler) {
            mRotateHandler.removeMessages(0);
            mRotateHandler = null;
        }
        isRotating = false;
    }

    /**
     * 回调模式
     */
    public enum CallBackMode {
        // 有移动就立刻回调
        CALL_BACK_MODE_MOVE,
        // 只有状态变化的时候才回调
        CALL_BACK_MODE_STATE_CHANGE
    }

    /**
     * 设置回调模式
     *
     * @param mode 回调模式
     */
    public void setCallBackMode(CallBackMode mode) {
        mCallBackMode = mode;
    }

    /**
     * 设置摇杆普通状态背景
     *
     * @param drawable
     */
    public void setNormalAreaBackground(Drawable drawable) {
        if (drawable != null && drawable instanceof BitmapDrawable) {
            mNormalAreaBitmap = ((BitmapDrawable) drawable).getBitmap();
            mAreaBackgroundMode = AREA_BACKGROUND_MODE_PIC;
        }
    }

    /**
     * 设置摇杆普通状态背景
     *
     * @param resID
     */
    public void setNormalAreaBackground(int resID) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), resID);
        if (drawable != null) {
            mNormalAreaBitmap = ((BitmapDrawable) drawable).getBitmap();
            mAreaBackgroundMode = AREA_BACKGROUND_MODE_PIC;
        }
    }

    /**
     * 设置摇杆触摸后的背景
     */
    public void setPressedAreaBackground(Drawable drawable) {
        if (drawable != null && drawable instanceof BitmapDrawable) {
            mPressedAreaBitmap = ((BitmapDrawable) drawable).getBitmap();
            mAreaBackgroundMode = AREA_BACKGROUND_MODE_PIC;
        }
    }

    /**
     * 设置摇杆触摸后的背景
     */
    public void setPressedAreaBackground(int resID) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), resID);
        if (drawable != null) {
            mPressedAreaBitmap = ((BitmapDrawable) drawable).getBitmap();
            mAreaBackgroundMode = AREA_BACKGROUND_MODE_PIC;
        }
    }

    /**
     * 设置摇杆控件触摸点背景
     */
    public void setNormalJoystickDrawable(Drawable drawable) {
        if (drawable != null && drawable instanceof BitmapDrawable) {
            mNormalJoystickBitmap = ((BitmapDrawable) drawable).getBitmap();
            mJoystickBackgroundMode = JOYSTICK_BACKGROUND_MODE_PIC;
        }
    }

    /**
     * 设置摇杆控件触摸点背景
     */
    public void setNormalJoystickDrawable(int resID) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), resID);
        if (drawable != null) {
            mNormalJoystickBitmap = ((BitmapDrawable) drawable).getBitmap();
            mJoystickBackgroundMode = JOYSTICK_BACKGROUND_MODE_PIC;
        }
    }

    /**
     * 设置摇杆控件触摸点按下后的背景
     */
    public void setPressedJoystickDrawable(Drawable drawable) {
        if (drawable != null && drawable instanceof BitmapDrawable) {
            mPressedJoystickBitmap = ((BitmapDrawable) drawable).getBitmap();
            mJoystickBackgroundMode = JOYSTICK_BACKGROUND_MODE_PIC;
        }
    }

    /**
     * 设置摇杆控件触摸点按下后的背景
     */
    public void setPressedJoystickDrawable(int resID) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), resID);
        if (drawable != null) {
            mPressedJoystickBitmap = ((BitmapDrawable) drawable).getBitmap();
            mJoystickBackgroundMode = JOYSTICK_BACKGROUND_MODE_PIC;
        }
    }

    /**
     * 摇杆支持几个方向
     */
    public enum DirectionMode {
        DIRECTION_2_HORIZONTAL,// 横向 左右两个方向
        DIRECTION_2_VERTICAL, // 纵向 上下两个方向
        DIRECTION_4_ROTATE_0, // 四个方向
        DIRECTION_4_ROTATE_45, // 四个方向 旋转45度
        DIRECTION_8 // 八个方向
    }

    /**
     * 方向
     */
    public enum Direction {
        DIRECTION_LEFT, // 左
        DIRECTION_UP, // 上
        DIRECTION_RIGHT, // 右
        DIRECTION_DOWN, // 下
        DIRECTION_UP_LEFT, // 左上
        DIRECTION_UP_RIGHT, // 右上
        DIRECTION_DOWN_RIGHT, // 右下
        DIRECTION_DOWN_LEFT, // 左下
        DIRECTION_CENTER // 中间
    }

    /**
     * 添加摇杆摇动角度的监听
     *
     * @param listener 回调接口
     */
    public void setOnAngleChangeListener(OnAngleChangeListener listener) {
        mOnAngleChangeListener = listener;
    }

    /**
     * 添加摇动的监听
     *
     * @param directionMode 监听的方向
     * @param listener      回调
     */
    public void setOnShakeListener(DirectionMode directionMode, OnShakeListener listener) {
        mDirectionMode = directionMode;
        mOnShakeListener = listener;
    }

    /**
     * 添加摇杆旋转的监听
     *
     * @param listener 回调
     */
    public void setOnRotateListener(OnRotateListener listener) {
        mOnRotateListener = listener;
    }

    /**
     * 摇动方向监听接口
     */
    public interface OnShakeListener {
        // 开始
        void onStart();

        /**
         * 摇动方向
         *
         * @param direction 方向
         * @param isValid   true: 有效的，已经判断不是旋转摇杆； false: 还在判断是否在旋转。
         *                  isValid 为false时，可以开始更新UI，短暂延时后确定平移时会变为true。
         */
        void direction(Direction direction, boolean isValid);

        // 结束
        void onFinish();
    }

    /**
     * 摇动角度的监听接口
     */
    public interface OnAngleChangeListener {
        // 开始
        void onStart();

        /**
         * 摇杆角度变化
         *
         * @param angle 角度[0,360)
         */
        void angle(double angle);

        // 结束
        void onFinish();
    }

    /**
     * 摇杆旋转监听接口，顺、逆时针
     */
    public interface OnRotateListener {
        // 开始
        void onStart();

        /**
         * 摇杆角度变化
         *
         * @param isClockWise true 顺时针， false 逆时针
         */
        void onClockWise(boolean isClockWise);

        // 结束
        void onFinish();
    }

}
