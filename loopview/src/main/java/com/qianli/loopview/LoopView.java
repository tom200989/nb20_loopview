package com.qianli.loopview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class LoopView extends View {

    private float scaleX = 1.0f;// 横向缩放倍率(数字越小, 字符间距越小)
    private static final int DEFAULT_TEXT_SIZE = (int) (Resources.getSystem().getDisplayMetrics().density * 15);
    private static final float DEFAULT_LINE_SPACE = 2f;// 默认文本行距
    private static final int DEFAULT_VISIBIE_ITEMS = 9;// 默认item显示个数
    private int textSrollSpeed = 10;// 文字滚动速度
    private boolean isfling = true;// 默认触摸滚动

    private Context context;
    Handler handler;
    private GestureDetector flingGestureDetector;
    public OnItemSelectedListener onItemSelectedListener;
    private ScheduledExecutorService mExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> mFuture;

    private Paint paintOuterText;
    private Paint paintCenterText;
    private Paint paintIndicator;

    public List<IndexString> items;
    private int textSize;
    public int maxTextHeight;
    private int outerTextColor;
    private int centerTextColor;
    private int dividerColor;
    public float lineSpacingMultiplier;
    public boolean isLoop;
    private float firstLineY;
    private float secondLineY;
    public int totalScrollY;
    public int initPosition;
    private int selectedItem;
    private int preCurrentIndex;
    private int change;
    private int itemsVisibleCount;
    private HashMap<Integer, IndexString> drawingStrings;
    private int measuredHeight;
    private int measuredWidth;
    private int halfCircumference;
    private int radius;
    private int mOffset = 0;
    private float previousY;
    private long startTime = 0;
    private Rect tempRect = new Rect();
    private int paddingLeft;
    private int paddingRight;

    public enum ACTION {
        CLICK, FLING, DAGGLE
    }

    /* -------------------------------------------- public -------------------------------------------- */

    /**
     * 是否允许触摸滚动
     *
     * @param isFling T:允许滚动
     */
    public void setFling(boolean isFling) {
        isfling = isFling;
    }

    /**
     * 设置监听器
     *
     * @param OnItemSelectedListener 监听器
     */
    public final void setListener(OnItemSelectedListener OnItemSelectedListener) {
        onItemSelectedListener = OnItemSelectedListener;
    }

    /**
     * 设置item集合
     *
     * @param items item集合
     */
    public final void setItems(List<String> items) {
        this.items = convertData(items);
        remeasure();
        invalidate();
    }

    /**
     * 设置横向文本倍率
     * link https://github.com/weidongjian/androidWheelView/issues/10
     * 以上连接用于解决文字经过分割线时出现错乱
     *
     * @param scaleX 横向文本倍率
     */
    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    /**
     * 设置当前item位置
     *
     * @param position 当前item位置
     */
    public void setCurrentPosition(int position) {
        if (items == null || items.isEmpty()) {
            return;
        }
        int size = items.size();
        if (position >= 0 && position < size && position != selectedItem) {
            initPosition = position;
            totalScrollY = 0;
            mOffset = 0;
            invalidate();
        }
    }

    /**
     * 设置文本滚动速度
     *
     * @param speed default == 10
     */
    public void setScrollSpeeds(int speed) {
        textSrollSpeed = speed;
    }

    /**
     * 设置文本行距
     *
     * @param lineSpacingMultiplier 必须传入 >= 1.0 参数
     */
    public void setLineSpacingMultiplier(float lineSpacingMultiplier) {
        if (lineSpacingMultiplier > 1.0f) {
            this.lineSpacingMultiplier = lineSpacingMultiplier;
        }
    }

    /**
     * 设置中心文本颜色
     *
     * @param centerTextColor 十六进制颜色值
     */
    public void setCenterTextColor(int centerTextColor) {
        this.centerTextColor = centerTextColor;
        paintCenterText.setColor(centerTextColor);
    }

    /**
     * 设置外部文本颜色
     *
     * @param outerTextColor 十六进制颜色值
     */
    public void setOuterTextColor(int outerTextColor) {
        this.outerTextColor = outerTextColor;
        paintOuterText.setColor(outerTextColor);
    }

    /**
     * 设置分割线颜色
     *
     * @param dividerColor 十六进制颜色值
     */
    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        paintIndicator.setColor(dividerColor);
    }

    /**
     * 设置item可见个数
     *
     * @param visibleNumber item可见个数
     */
    @SuppressLint("UseSparseArrays")
    public void setItemsVisibleCount(int visibleNumber) {
        if (visibleNumber % 2 == 0) {
            return;
        }
        if (visibleNumber != itemsVisibleCount) {
            itemsVisibleCount = visibleNumber;
            drawingStrings = new HashMap<>();
        }
    }

    /**
     * 设置不重复滚动, 即滚动到最后一个自动滚向第一个
     */
    public void setNotLoop() {
        isLoop = false;
    }

    /**
     * 设置文本大小
     *
     * @param size 文本大小
     */
    public final void setTextSize(float size, boolean isBold) {
        if (size > 0.0F) {
            textSize = (int) (context.getResources().getDisplayMetrics().density * size);
            paintOuterText.setTextSize(textSize);
            paintCenterText.setTextSize(textSize);
        }
        paintCenterText.setFakeBoldText(isBold);
    }

    /**
     * 设置起始选中位置
     *
     * @param initPosition 起始选中位置
     */
    public final void setInitPosition(int initPosition) {
        if (initPosition < 0) {
            this.initPosition = 0;
        } else {
            if (items != null && items.size() > initPosition) {
                this.initPosition = initPosition;
            }
        }
    }

    /* -------------------------------------------- code -------------------------------------------- */

    public LoopView(Context context) {
        super(context);
        initLoopView(context, null);
    }

    public LoopView(Context context, AttributeSet attributeset) {
        super(context, attributeset);
        initLoopView(context, attributeset);
    }

    public LoopView(Context context, AttributeSet attributeset, int defStyleAttr) {
        super(context, attributeset, defStyleAttr);
        initLoopView(context, attributeset);
    }

    @SuppressLint({"CustomViewStyleable", "UseSparseArrays"})
    private void initLoopView(Context context, AttributeSet attributeset) {
        this.context = context;
        handler = new MessageHandler(this);
        flingGestureDetector = new GestureDetector(context, new LoopViewGestureListener(this));
        flingGestureDetector.setIsLongpressEnabled(false);

        TypedArray typedArray = context.obtainStyledAttributes(attributeset, R.styleable.androidWheelView);
        textSize = typedArray.getInteger(R.styleable.androidWheelView_awv_textsize, DEFAULT_TEXT_SIZE);
        textSize = (int) (Resources.getSystem().getDisplayMetrics().density * textSize);
        lineSpacingMultiplier = typedArray.getFloat(R.styleable.androidWheelView_awv_lineSpace, DEFAULT_LINE_SPACE);
        centerTextColor = typedArray.getInteger(R.styleable.androidWheelView_awv_centerTextColor, 0xff313131);
        outerTextColor = typedArray.getInteger(R.styleable.androidWheelView_awv_outerTextColor, 0xffafafaf);
        dividerColor = typedArray.getInteger(R.styleable.androidWheelView_awv_dividerTextColor, 0xffc5c5c5);
        itemsVisibleCount = typedArray.getInteger(R.styleable.androidWheelView_awv_itemsVisibleCount, DEFAULT_VISIBIE_ITEMS);
        if (itemsVisibleCount % 2 == 0) {
            itemsVisibleCount = DEFAULT_VISIBIE_ITEMS;
        }
        isLoop = typedArray.getBoolean(R.styleable.androidWheelView_awv_isLoop, true);
        typedArray.recycle();

        drawingStrings = new HashMap<>();
        totalScrollY = 0;
        initPosition = -1;

        initPaints();
    }

    /**
     * 初始化画笔
     */
    private void initPaints() {
        paintOuterText = new Paint();
        paintOuterText.setColor(outerTextColor);
        paintOuterText.setAntiAlias(true);
        paintOuterText.setTypeface(Typeface.MONOSPACE);
        paintOuterText.setTextSize(textSize);

        paintCenterText = new Paint();
        paintCenterText.setColor(centerTextColor);
        paintCenterText.setAntiAlias(true);
        paintCenterText.setTextScaleX(scaleX);
        paintCenterText.setTypeface(Typeface.MONOSPACE);
        paintCenterText.setTextSize(textSize);

        paintIndicator = new Paint();
        paintIndicator.setColor(dividerColor);
        paintIndicator.setAntiAlias(true);

    }

    /**
     * 测量
     */
    private void remeasure() {
        if (items == null) {
            return;
        }
        measuredWidth = getMeasuredWidth();
        measuredHeight = getMeasuredHeight();
        if (measuredWidth == 0 || measuredHeight == 0) {
            return;
        }
        paddingLeft = getPaddingLeft();
        paddingRight = getPaddingRight();
        measuredWidth = measuredWidth - paddingRight;
        paintCenterText.getTextBounds("\u661F\u671F", 0, 2, tempRect); // 星期
        maxTextHeight = tempRect.height();
        halfCircumference = (int) (measuredHeight * Math.PI / 2);
        maxTextHeight = (int) (halfCircumference / (lineSpacingMultiplier * (itemsVisibleCount - 1)));
        radius = measuredHeight / 2;
        firstLineY = ((measuredHeight - lineSpacingMultiplier * maxTextHeight) / 2F);
        secondLineY = ((measuredHeight + lineSpacingMultiplier * maxTextHeight) / 2F);
        if (initPosition == -1) {
            if (isLoop) {
                initPosition = (items.size() + 1) / 2;
            } else {
                initPosition = 0;
            }
        }
        preCurrentIndex = initPosition;
    }

    void smoothScroll(ACTION action) {
        cancelFuture();
        if (action == ACTION.FLING || action == ACTION.DAGGLE) {
            float itemHeight = lineSpacingMultiplier * maxTextHeight;
            mOffset = (int) ((totalScrollY % itemHeight + itemHeight) % itemHeight);
            if ((float) mOffset > itemHeight / 2.0F) {
                mOffset = (int) (itemHeight - (float) mOffset);
            } else {
                mOffset = -mOffset;
            }
        }
        mFuture = mExecutor.scheduleWithFixedDelay(new SmoothScrollTimerTask(this, mOffset), 0, 10, TimeUnit.MILLISECONDS);
    }

    protected final void scrollBy(float velocityY) {
        cancelFuture();
        // 改变这个数值可以改变滚动速度
        int velocityFling = 10;
        if (textSrollSpeed != -1) {
            velocityFling = textSrollSpeed;
        }
        mFuture = mExecutor.scheduleWithFixedDelay(new InertiaTimerTask(this, velocityY), 0, velocityFling, TimeUnit.MILLISECONDS);
    }

    public void cancelFuture() {
        if (mFuture != null && !mFuture.isCancelled()) {
            mFuture.cancel(true);
            mFuture = null;
        }
    }

    public List<IndexString> convertData(List<String> items) {
        List<IndexString> data = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            data.add(new IndexString(i, items.get(i)));
        }
        return data;
    }

    public final int getSelectedItem() {
        return selectedItem;
    }

    protected final void onItemSelected() {
        if (onItemSelectedListener != null) {
            postDelayed(new OnItemSelectedRunnable(this), 200L);
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        if (items == null) {
            return;
        }

        change = (int) (totalScrollY / (lineSpacingMultiplier * maxTextHeight));
        preCurrentIndex = initPosition + change % items.size();

        if (!isLoop) {
            if (preCurrentIndex < 0) {
                preCurrentIndex = 0;
            }
            if (preCurrentIndex > items.size() - 1) {
                preCurrentIndex = items.size() - 1;
            }
        } else {
            if (preCurrentIndex < 0) {
                preCurrentIndex = items.size() + preCurrentIndex;
            }
            if (preCurrentIndex > items.size() - 1) {
                preCurrentIndex = preCurrentIndex - items.size();
            }
        }

        int j2 = (int) (totalScrollY % (lineSpacingMultiplier * maxTextHeight));
        // put value to drawingString
        int k1 = 0;
        while (k1 < itemsVisibleCount) {
            int l1 = preCurrentIndex - (itemsVisibleCount / 2 - k1);
            if (isLoop) {
                while (l1 < 0) {
                    l1 = l1 + items.size();
                }
                while (l1 > items.size() - 1) {
                    l1 = l1 - items.size();
                }
                drawingStrings.put(k1, items.get(l1));
            } else if (l1 < 0) {
                drawingStrings.put(k1, new IndexString());
            } else if (l1 > items.size() - 1) {
                drawingStrings.put(k1, new IndexString());
            } else {
                drawingStrings.put(k1, items.get(l1));
            }
            k1++;
        }
        canvas.drawLine(paddingLeft, firstLineY, measuredWidth, firstLineY, paintIndicator);
        canvas.drawLine(paddingLeft, secondLineY, measuredWidth, secondLineY, paintIndicator);

        int i = 0;
        while (i < itemsVisibleCount) {
            canvas.save();
            float itemHeight = maxTextHeight * lineSpacingMultiplier;
            double radian = ((itemHeight * i - j2) * Math.PI) / halfCircumference;
            if (radian >= Math.PI || radian <= 0) {
                canvas.restore();
            } else {
                int translateY = (int) (radius - Math.cos(radian) * radius - (Math.sin(radian) * maxTextHeight) / 2D);
                canvas.translate(0.0F, translateY);
                canvas.scale(1.0F, (float) Math.sin(radian));
                IndexString indexString = drawingStrings.get(i);
                if (indexString != null) {
                    if (translateY <= firstLineY && maxTextHeight + translateY >= firstLineY) {
                        // first divider
                        canvas.save();
                        canvas.clipRect(0, 0, measuredWidth, firstLineY - translateY);
                        canvas.drawText(indexString.string, getTextX(indexString.string, paintOuterText, tempRect), maxTextHeight, paintOuterText);
                        canvas.restore();
                        canvas.save();
                        canvas.clipRect(0, firstLineY - translateY, measuredWidth, (int) (itemHeight));
                        canvas.drawText(indexString.string, getTextX(indexString.string, paintCenterText, tempRect), maxTextHeight, paintCenterText);
                        canvas.restore();
                    } else if (translateY <= secondLineY && maxTextHeight + translateY >= secondLineY) {
                        // second divider
                        canvas.save();
                        canvas.clipRect(0, 0, measuredWidth, secondLineY - translateY);
                        canvas.drawText(indexString.string, getTextX(indexString.string, paintCenterText, tempRect), maxTextHeight, paintCenterText);
                        canvas.restore();
                        canvas.save();
                        canvas.clipRect(0, secondLineY - translateY, measuredWidth, (int) (itemHeight));
                        canvas.drawText(indexString.string, getTextX(indexString.string, paintOuterText, tempRect), maxTextHeight, paintOuterText);
                        canvas.restore();
                    } else if (translateY >= firstLineY && maxTextHeight + translateY <= secondLineY) {
                        // center item
                        canvas.clipRect(0, 0, measuredWidth, (int) (itemHeight));
                        canvas.drawText(indexString.string, getTextX(indexString.string, paintCenterText, tempRect), maxTextHeight, paintCenterText);
                        selectedItem = items.indexOf(indexString);
                    } else {
                        // other item
                        canvas.clipRect(0, 0, measuredWidth, (int) (itemHeight));
                        canvas.drawText(indexString.string, getTextX(indexString.string, paintOuterText, tempRect), maxTextHeight, paintOuterText);
                    }
                    canvas.restore();
                }
            }
            i++;
        }
    }

    // text start drawing position
    private int getTextX(String a, Paint paint, Rect rect) {
        paint.getTextBounds(a, 0, a.length(), rect);
        int textWidth = rect.width();
        textWidth *= scaleX;
        return (measuredWidth - paddingLeft - textWidth) / 2 + paddingLeft;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        remeasure();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isfling) {
            return super.onTouchEvent(event);
        }

        boolean eventConsumed = flingGestureDetector.onTouchEvent(event);
        float itemHeight = lineSpacingMultiplier * maxTextHeight;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTime = System.currentTimeMillis();
                cancelFuture();
                previousY = event.getRawY();
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                float dy = previousY - event.getRawY();
                previousY = event.getRawY();

                totalScrollY = (int) (totalScrollY + dy);

                if (!isLoop) {
                    float top = -initPosition * itemHeight;
                    float bottom = (items.size() - 1 - initPosition) * itemHeight;

                    if (totalScrollY < top) {
                        totalScrollY = (int) top;
                    } else if (totalScrollY > bottom) {
                        totalScrollY = (int) bottom;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            default:
                if (!eventConsumed) {
                    float y = event.getY();
                    double l = Math.acos((radius - y) / radius) * radius;
                    int circlePosition = (int) ((l + itemHeight / 2) / itemHeight);

                    float extraOffset = (totalScrollY % itemHeight + itemHeight) % itemHeight;
                    mOffset = (int) ((circlePosition - itemsVisibleCount / 2) * itemHeight - extraOffset);

                    if ((System.currentTimeMillis() - startTime) > 120) {
                        smoothScroll(ACTION.DAGGLE);
                    } else {
                        smoothScroll(ACTION.CLICK);
                    }
                }
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
        }

        invalidate();
        return true;
    }

    private class IndexString {

        private String string;
        private int index;

        private IndexString() {
            this.string = "";
        }

        private IndexString(int index, String str) {
            this.index = index;
            this.string = str;
        }
    }
}
