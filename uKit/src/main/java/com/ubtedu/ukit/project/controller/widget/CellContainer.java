package com.ubtedu.ukit.project.controller.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ubtedu.ukit.R;
import com.ubtedu.ukit.project.controller.model.config.WidgetConfig;
import com.ubtedu.ukit.project.controller.model.item.WidgetItem;
import com.ubtedu.ukit.project.controller.utils.Tool;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class CellContainer extends ViewGroup {
    private int _cellHeight;
    private int _cellSpanH;
    private int _cellSpanV;
    private int _cellWidth;
    private Rect[][] _cells;
    private boolean[][] _occupied;
    @NonNull
    private final Rect _tempRect = new Rect();

    /**
     * BEGIN
     **/
//    private int bgColor = Color.WHITE;
    private final Paint _bgPaint = new Paint();
    private final Paint _dividerPaint = new Paint();
    private final Paint _dragPaint = new Paint();
    private final TextPaint _textPaint = new TextPaint();
    private boolean _dragMask = false;
    private int _dragCoordinateX = -1;
    private int _dragCoordinateY = -1;
    private int _dragCoordinateXspan = -1;
    private int _dragCoordinateYspan = -1;
    private boolean _showDivider = true;

    /**
     * END
     **/

    public static final class LayoutParams extends ViewGroup.LayoutParams {
        private int _x;
        private int _xSpan = 1;
        private int _y;
        private int _ySpan = 1;

        public final int getX() {
            return _x;
        }

        public final void setX(int v) {
            _x = v;
        }

        public final int getY() {
            return _y;
        }

        public final void setY(int v) {
            _y = v;
        }

        public final int getXSpan() {
            return _xSpan;
        }

        public final void setXSpan(int v) {
            _xSpan = v;
        }

        public final int getYSpan() {
            return _ySpan;
        }

        public final void setYSpan(int v) {
            _ySpan = v;
        }

        public LayoutParams(int w, int h, int x, int y) {
            super(w, h);
            _x = x;
            _y = y;
        }

        public LayoutParams(int w, int h, int x, int y, int xSpan, int ySpan) {
            super(w, h);
            _x = x;
            _y = y;
            _xSpan = xSpan;
            _ySpan = ySpan;
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }
    }

    public final int getCellWidth() {
        return _cellWidth;
    }

    public final int getCellHeight() {
        return _cellHeight;
    }

    public final int getCellSpanV() {
        return _cellSpanV;
    }

    public final int getCellSpanH() {
        return _cellSpanH;
    }

    @NonNull
    public final List<View> getAllCells() {
        ArrayList views = new ArrayList();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            views.add(getChildAt(i));
        }
        return views;
    }

    public CellContainer(Context context) {
        this(context, null);
    }

    public CellContainer(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }

    public final void setGridSize(int x, int y) {
        _cellSpanV = y;
        _cellSpanH = x;

        _occupied = new boolean[_cellSpanH][_cellSpanV];
        for (int i = 0; i < _cellSpanH; i++) {
            for (int j = 0; j < _cellSpanV; j++) {
                _occupied[i][j] = false;
            }
        }
        requestLayout();
    }

    public final void resetOccupiedSpace() {
        if (_cellSpanH > 0 && _cellSpanV > 0) {
            _occupied = new boolean[_cellSpanH][_cellSpanV];
        }
    }

    public void removeAllViews() {
        resetOccupiedSpace();
        super.removeAllViews();
    }

    public void init() {
        setWillNotDraw(false);
        _bgPaint.setStyle(Style.FILL);
        _bgPaint.setColor(getResources().getColor(R.color.controller_container_bg));
        _bgPaint.setAlpha(255);
        _dividerPaint.setStyle(Style.FILL);
        _dividerPaint.setFlags(_dividerPaint.getFlags() | Paint.ANTI_ALIAS_FLAG);
        _dividerPaint.setColor(getResources().getColor(R.color.controller_container_divider));
        _dividerPaint.setAlpha(255);
        _dragPaint.setStyle(Style.FILL);
        _dragPaint.setFlags(_dividerPaint.getFlags() | Paint.ANTI_ALIAS_FLAG);
        _dragPaint.setAlpha(255);
        _dragPaint.setColor(0xCC5BC75F);
        _textPaint.setColor(Color.RED);
        _textPaint.setTypeface(Typeface.DEFAULT);
        _textPaint.setFlags(_textPaint.getFlags() | Paint.ANTI_ALIAS_FLAG);
        _textPaint.setTextSize(30);
        _textPaint.setAlpha(255);
    }

    @Nullable
    public final Point findFreeSpace() {
        for (int y = 0; y < _occupied[0].length; y++) {
            for (int x = 0; x < _occupied.length; x++) {
                if (!_occupied[x][y]) {
                    return new Point(x, y);
                }
            }
        }
        return null;
    }

    @Nullable
    public final Point findFreeSpace(int spanX, int spanY) {
        for (int y = 0; y < _occupied[0].length; y++) {
            for (int x = 0; x < _occupied.length; x++) {
                if (!_occupied[x][y] && !checkOccupied(new Point(x, y), spanX, spanY)) {
                    return new Point(x, y);
                }
            }
        }
        return null;
    }

    public Point checkDragMask(int x, int y, int xSpan, int ySpan) {
        Point coordinate = positionToCoordinate(x - ((xSpan - 1) * _cellWidth / 2), y - ((ySpan - 1) * _cellHeight / 2));
        if (coordinate == null) {
            return null;
        }
        if (_dragCoordinateX == coordinate.x
                && _dragCoordinateY == coordinate.y
                && _dragCoordinateXspan == xSpan
                && _dragCoordinateYspan == ySpan) {
            return coordinate;
        }
        if (coordinate.x < 0 || coordinate.y < 0 || checkOccupied(coordinate, xSpan, ySpan)) {
            return null;
        }
        return coordinate;
    }

    public void updateDragMask(int x, int y, int xSpan, int ySpan) {
        if (_dragMask
                && _dragCoordinateX == x
                && _dragCoordinateY == y
                && _dragCoordinateXspan == xSpan
                && _dragCoordinateYspan == ySpan) {
            return;
        }
        _dragMask = true;
        _dragCoordinateX = x;
        _dragCoordinateY = y;
        _dragCoordinateXspan = xSpan;
        _dragCoordinateYspan = ySpan;
        invalidate();
    }

    public void setDragMask(boolean dragMask) {
        _dragMask = dragMask;
        invalidate();
    }

    public void resetDragMask() {
        _dragMask = false;
        _dragCoordinateX = -1;
        _dragCoordinateY = -1;
        _dragCoordinateXspan = -1;
        _dragCoordinateYspan = -1;
        invalidate();
    }

    public boolean isDragMask() {
        return _dragMask;
    }

    public final Point positionToCoordinate(int mX, int mY) {
        if (_cells == null) {
            return null;
        }
        Point coordinate = new Point();
        int x = mX / getCellWidth();
        int y = mY / getCellHeight();
        coordinate.set(x, y);
        return coordinate;
    }

    public void setShowDivider(boolean isShow) {
        _showDivider = isShow;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (_showDivider) {
            canvas.drawRect(0f, 0f, getWidth(), getHeight(), _bgPaint);
            if (_cells == null)
                return;

            if (_dragMask) {
                canvas.drawRect((float) _dragCoordinateX * (float) _cellWidth,
                        (float) _dragCoordinateY * (float) _cellHeight,
                        (_dragCoordinateX + _dragCoordinateXspan) * (float) _cellWidth,
                        (_dragCoordinateY + _dragCoordinateYspan) * (float) _cellHeight,
                        _dragPaint);
            }
            for (int x = 1; x <= _cellSpanH; x++) {
                int pos_x = _cellWidth * x;
                canvas.drawLine(pos_x - 0.5f, 0f, pos_x + 0.5f, getHeight(), _dividerPaint);
//                canvas.drawText(x + "", pos_x, 150, _textPaint);
            }
            for (int y = 1; y <= _cellSpanV; y++) {
                int pos_y = _cellHeight * y;
                canvas.drawLine(0f, pos_y - 0.5f, getWidth(), pos_y + 0.5f, _dividerPaint);
//                canvas.drawText(y + "", 150, pos_y, _textPaint);
            }
        } else {
            canvas.drawColor(0);
        }
    }

    public void addView(View view) {
        addView(view, true);
    }

    public void addView(View view, boolean occupied) {
        if (!(view.getLayoutParams() instanceof CellContainer.LayoutParams)) {
            return;
        }
        LayoutParams lp = (CellContainer.LayoutParams) view.getLayoutParams();
        if (occupied) {
            setOccupied(true, lp);
        }
        if (view instanceof ControllerWidgetView) {
            ControllerWidgetView controllerWidgetView = (ControllerWidgetView) view;
            WidgetConfig widgetConfig = controllerWidgetView.getWidgetConfig();
            widgetConfig.setX(lp.getX());
            widgetConfig.setY(lp.getY());
            controllerWidgetView.setWidgetConfig(widgetConfig, false);
        }
        super.addView(view);
    }

    public void removeView(View view) {
        removeView(view, true);
    }

    public void removeView(View view, boolean occupied) {
        if (occupied) {
            LayoutParams lp = (CellContainer.LayoutParams) view.getLayoutParams();
            setOccupied(false, lp);
        }
        super.removeView(view);
    }

    public void removeViewWithoutOccupied(View view) {
        super.removeView(view);
    }

    public final boolean addViewToPosition(@NonNull View view, int x, int y, int xSpan, int ySpan) {
        Point coordinate = positionToCoordinate(x - (xSpan * _cellWidth / 2), y - (ySpan * _cellHeight / 2));
        if (coordinate == null || checkOccupied(coordinate, xSpan, ySpan)) {
            return false;
        }
        view.setLayoutParams(new LayoutParams(WRAP_CONTENT, WRAP_CONTENT, coordinate.x, coordinate.y, xSpan, ySpan));
        addView(view);
        return true;
    }

    //public final void addViewToGrid(@NonNull View view, int x, int y, int xSpan, int ySpan) {
    //    view.setLayoutParams(new LayoutParams(WRAP_CONTENT, WRAP_CONTENT, x, y, xSpan, ySpan));
    //    addView(view);
    //}

    public final void addViewToGrid(@NonNull ControllerWidgetView view, int x, int y) {
        int xSpan, ySpan;
        WidgetItem widgetItem = view.getWidgetItem();
        xSpan = widgetItem.getSpanH();
        ySpan = widgetItem.getSpanV();
        view.setLayoutParams(new LayoutParams(WRAP_CONTENT, WRAP_CONTENT, x, y, xSpan, ySpan));
        addView(view);
    }

    public final void addViewToGrid(@NonNull ControllerWidgetView view) {
        addViewToGrid(view, true);
    }

    public final void addViewToGrid(@NonNull ControllerWidgetView view, boolean occupied) {
        int x, y, xSpan, ySpan;
        x = view.getWidgetConfig().getX();
        y = view.getWidgetConfig().getY();
        WidgetItem widgetItem = view.getWidgetItem();
        xSpan = widgetItem.getSpanH();
        ySpan = widgetItem.getSpanV();
        view.setLayoutParams(new LayoutParams(WRAP_CONTENT, WRAP_CONTENT, x, y, xSpan, ySpan));
        addView(view, occupied);
    }

    public final void setOccupied(boolean b, @NonNull LayoutParams lp) {
        int xSpan = lp.getX() + lp.getXSpan();
        for (int x = lp.getX(); x < xSpan; x++) {
            int ySpan = lp.getY() + lp.getYSpan();
            for (int y = lp.getY(); y < ySpan; y++) {
                if (x >= 0 && x < _cellSpanH && y >= 0 && y < _cellSpanV) {
                    _occupied[x][y] = b;
                }
            }
        }
    }

    public final boolean checkOccupied(Point start, int spanX, int spanY) {
        if (start.x < 0 || start.y < 0 || start.x >= _cellSpanH || start.y >= _cellSpanV) {
            return true;
        }
        int i = start.x + spanX;
        if (i <= _occupied.length) {
            i = start.y + spanY;
            if (i <= _occupied[0].length) {
                int i2 = start.y + spanY;
                for (i = start.y; i < i2; i++) {
                    int i3 = start.x + spanX;
                    for (int x = start.x; x < i3; x++) {
                        if (_occupied[x][i]) {
                            return true;
                        }
                    }
                }
                return false;
            }
        }
        return true;
    }

    public final View coordinateToChildView(Point pos) {
        if (pos == null) {
            return null;
        }
        for (int i = 0; i < getChildCount(); i++) {
            LayoutParams lp = (LayoutParams) getChildAt(i).getLayoutParams();
            if (pos.x >= lp._x && pos.y >= lp._y && pos.x < lp._x + lp._xSpan && pos.y < lp._y + lp._ySpan) {
                return getChildAt(i);
            }
        }
        return null;
    }

    public final LayoutParams coordinateToLayoutParams(int mX, int mY, int xSpan, int ySpan) {
        Point pos = new Point();
        touchPosToCoordinate(pos, mX, mY, xSpan, ySpan, true);
        return !pos.equals(-1, -1) ? new LayoutParams(WRAP_CONTENT, WRAP_CONTENT, pos.x, pos.y, xSpan, ySpan) : null;
    }

    public void touchPosToCoordinate(@NonNull Point coordinate, int mX, int mY, int xSpan, int ySpan, boolean checkAvailability) {
        touchPosToCoordinate(coordinate, mX, mY, xSpan, ySpan, checkAvailability, false);
    }

    public final void touchPosToCoordinate(Point coordinate, int mX, int mY, int xSpan, int ySpan, boolean checkAvailability, boolean checkBoundary) {
        if (_cells == null) {
            coordinate.set(-1, -1);
            return;
        }

        mX -= (xSpan - 1) * _cellWidth / 2f;
        mY -= (ySpan - 1) * _cellHeight / 2f;

        int x = 0;
        while (x < _cellSpanH) {
            int y = 0;
            while (y < _cellSpanV) {
                Rect cell = _cells[x][y];
                if (mY >= cell.top && mY <= cell.bottom && mX >= cell.left && mX <= cell.right) {
                    if (checkAvailability) {
                        if (_occupied[x][y]) {
                            coordinate.set(-1, -1);
                            return;
                        }

                        int dx = x + xSpan - 1;
                        int dy = y + ySpan - 1;

                        if (dx >= _cellSpanH - 1) {
                            dx = _cellSpanH - 1;
                            x = dx + 1 - xSpan;
                        }
                        if (dy >= _cellSpanV - 1) {
                            dy = _cellSpanV - 1;
                            y = dy + 1 - ySpan;
                        }

                        for (int x2 = x; x2 < x + xSpan; x2++) {
                            for (int y2 = y; y2 < y + ySpan; y2++) {
                                if (_occupied[x2][y2]) {
                                    coordinate.set(-1, -1);
                                    return;
                                }
                            }
                        }
                    }
                    if (checkBoundary) {
                        Rect offsetCell = new Rect(cell);
                        int dp2 = Tool.toPx(6);
                        offsetCell.inset(dp2, dp2);
                        if (mY >= offsetCell.top && mY <= offsetCell.bottom && mX >= offsetCell.left && mX <= offsetCell.right) {
                            coordinate.set(-1, -1);
                            return;
                        }
                    }
                    coordinate.set(x, y);
                    return;
                }
                y++;
            }
            x++;
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = ((r - l) - getPaddingLeft()) - getPaddingRight();
        int height = ((b - t) - getPaddingTop()) - getPaddingBottom();
        if (_cellSpanH == 0) {
            _cellSpanH = 1;
        }
        if (_cellSpanV == 0) {
            _cellSpanV = 1;
        }
        _cellWidth = width / _cellSpanH;
        _cellHeight = height / _cellSpanV;
        initCellInfo(getPaddingLeft(), getPaddingTop(), width - getPaddingRight(), height - getPaddingBottom());
        int count = getChildCount();
        if (_cells != null) {
            int i = 0;
            while (i < count) {
                View child = getChildAt(i);
                if (child.getVisibility() != View.GONE) {
                    LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    int childWidth = lp.getXSpan() * _cellWidth;
                    int childHeight = lp.getYSpan() * _cellHeight;
                    int childX = lp.getX() * _cellWidth;
                    int childY = lp.getY() * _cellHeight;
                    child.measure(MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY));
                    child.layout(childX, childY, childX + childWidth - 1, childY + childHeight - 1);
                }
                i++;
            }
        }
    }

    private void initCellInfo(int l, int t, int r, int b) {
        _cells = new Rect[_cellSpanH][_cellSpanV];

        int curLeft = l;
        int curTop = t;
        int curRight = l + _cellWidth;
        int curBottom = t + _cellHeight;

        for (int i = 0; i < _cellSpanH; i++) {
            if (i != 0) {
                curLeft += _cellWidth;
                curRight += _cellWidth;
            }
            for (int j = 0; j < _cellSpanV; j++) {
                if (j != 0) {
                    curTop += _cellHeight;
                    curBottom += _cellHeight;
                }

                Rect rect = new Rect(curLeft, curTop, curRight, curBottom);
                _cells[i][j] = rect;
            }
            curTop = t;
            curBottom = t + _cellHeight;
        }
    }

    public void cleanChildSelectFlag() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE && child instanceof ControllerWidgetView) {
                ((ControllerWidgetView) child).setSelectedFlag(false);
            }
        }
    }

    public ControllerWidgetView findSelectWidget() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE && child instanceof ControllerWidgetView && ((ControllerWidgetView) child).isSelected()) {
                return (ControllerWidgetView) child;
            }
        }
        return null;
    }

}

