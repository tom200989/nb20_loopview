package com.weigan.loopgroup;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;
import com.weigan.loopview.R;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by qianli.ma on 2018/11/30 0030.
 */
public class LoopGroup extends RelativeLayout {

    /*
     * 注意: 注有[TOAT:扩展]字样
     * 的方法或者属性在未来扩展需求的时候作为可能变动较大的因素
     * 未来扩展的时候特别注意这些地方
     * */

    private View inflate;
    // TOAT: 扩展
    private LoopView loopPart1;
    private LoopView loopPart2;
    private LoopView loopPart3;
    private LoopView loopPart4;
    private List<LoopView> loops = new ArrayList<>();

    public LoopGroup(Context context) {
        this(context, null, 0);
    }

    public LoopGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoopGroup(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflate = View.inflate(context, R.layout.widget_loopgroup, this);
        // TOAT: 扩展
        loopPart1 = inflate.findViewById(R.id.loop_part1);
        loopPart2 = inflate.findViewById(R.id.loop_part2);
        loopPart3 = inflate.findViewById(R.id.loop_part3);
        loopPart4 = inflate.findViewById(R.id.loop_part4);
        loops.add(loopPart1);
        loops.add(loopPart2);
        loops.add(loopPart3);
        loops.add(loopPart4);
    }

    /* -------------------------------------------- 扩展方法 -------------------------------------------- */

    /**
     * 强制设置不能滚动
     * TOAT:扩展
     *
     * @param indexs 不能滚动的索引数组(IntRange(0 ~ 3))
     */
    public void setNotFling(@IntRange(from = 0, to = 3) int... indexs) {
        for (int index : indexs) {
            loops.get(index).setFling(false);
        }
    }

    /**
     * 设置数据
     * TOAT: 扩展
     *
     * @param data1 数据1
     * @param data2 数据2
     * @param data3 数据3
     * @param data4 数据4
     */
    public void setDatas(@NonNull List<String> data1,// 数据1, 不可为null
                         @Nullable List<String> data2,// 数据2, 可为null
                         @Nullable List<String> data3,// 数据3, 可为null
                         @Nullable List<String> data4// 数据4, 可为null
    ) {
        loopPart1.setItems(data1);

        if (data2 != null) {
            loopPart2.setItems(data2);
        } else {
            loopPart2.setVisibility(GONE);
        }

        if (data3 != null) {
            loopPart3.setItems(data3);
        } else {
            loopPart3.setVisibility(GONE);
        }

        if (data4 != null) {
            loopPart4.setItems(data4);
        } else {
            loopPart4.setVisibility(GONE);
        }
    }

    /**
     * 设置初始位置
     * TOAT:扩展
     *
     * @param index1 初始1
     * @param index2 初始2
     * @param index3 初始3
     * @param index4 初始4
     */
    public void setInitPosition(int index1, int index2, int index3, int index4) {
        loopPart1.setInitPosition(index1 < 0 ? 0 : index1);
        loopPart2.setInitPosition(index2 < 0 ? 0 : index2);
        loopPart3.setInitPosition(index3 < 0 ? 0 : index3);
        loopPart4.setInitPosition(index4 < 0 ? 0 : index4);
    }

    /**
     * 设置当前位置
     * TOAT:扩展
     *
     * @param index1 当前1
     * @param index2 当前2
     * @param index3 当前3
     * @param index4 当前4
     */
    public void setCurrentPosition(int index1, int index2, int index3, int index4) {
        loopPart1.setCurrentPosition(index1 < 0 ? 0 : index1);
        loopPart2.setCurrentPosition(index2 < 0 ? 0 : index2);
        loopPart3.setCurrentPosition(index3 < 0 ? 0 : index3);
        loopPart4.setCurrentPosition(index4 < 0 ? 0 : index4);
    }

    /**
     * 获取指定Loopview
     * TOAT:扩展
     *
     * @param index 该Loopview的索引位置
     */
    public LoopView getLoopView(@IntRange(from = 0, to = 3) int index) {
        return loops.get(index);
    }

    /**
     * 设置item选中监听器
     * TOAT:扩展
     *
     * @param index                  需要监听的索引
     * @param onItemSelectedListener 由外部传入接收器listener
     */
    public void setListener(@IntRange(from = 0, to = 3) int index, OnItemSelectedListener onItemSelectedListener) {
        loops.get(index).setListener(onItemSelectedListener);
    }

    /* -------------------------------------------- 通用方法 -------------------------------------------- */

    /**
     * 设置选中文本颜色
     *
     * @param colorHex 十六进制色号(如: #000000, Null: #000000)
     */
    public void setCenterColor(@Nullable String colorHex) {
        if (colorHex == null) {
            colorHex = "#000000";
        }
        for (LoopView loop : loops) {
            loop.setCenterTextColor(Color.parseColor(colorHex));
        }
    }

    /**
     * 设置隔离线颜色
     *
     * @param colorHex 十六进制色号(如: #666666, Null: #666666)
     */
    public void setDividerColor(@Nullable String colorHex) {
        if (colorHex == null) {
            colorHex = "#666666";
        }
        for (LoopView loop : loops) {
            loop.setDividerColor(Color.parseColor(colorHex));
        }
    }

    /**
     * 设置非选中文本颜色
     *
     * @param colorHex 十六进制色号(如: #666666, Null: #666666)
     */
    public void setOuterTextColor(@Nullable String colorHex) {
        if (colorHex == null) {
            colorHex = "#666666";
        }
        for (LoopView loop : loops) {
            loop.setOuterTextColor(Color.parseColor(colorHex));
        }
    }

    /**
     * 设置可见item数量
     *
     * @param visibleCount 可见item数量
     */
    public void setItemsVisibleCount(int visibleCount) {
        for (LoopView loop : loops) {
            loop.setItemsVisibleCount(visibleCount < 0 ? 9 : visibleCount);
        }
    }

    /**
     * 设置行距
     *
     * @param lineSpace 行距
     */
    public void setLineSpace(float lineSpace) {
        for (LoopView loop : loops) {
            loop.setLineSpacingMultiplier(lineSpace < 0 ? 2.8f : lineSpace);
        }
    }

    /**
     * 设置不滚动
     */
    public void setNotLoop() {
        for (LoopView loop : loops) {
            loop.setNotLoop();
        }
    }

    /**
     * 设置文本横向缩放大小
     *
     * @param scaleX 倍率(至少1.0f)
     */
    public void setTextScaleX(@FloatRange(from = 1.0f) float scaleX) {
        for (LoopView loop : loops) {
            loop.setScaleX(scaleX);
        }
    }

    /**
     * 设置文本滚动速度
     *
     * @param speed 速度值(默认10)
     */
    public void setScrollSpeeds(int speed) {
        for (LoopView loop : loops) {
            loop.setScrollSpeeds(speed);
        }
    }

    /**
     * 设置文本大小和样式
     *
     * @param size   大小默认18
     * @param isBold 是否加粗(默认不加粗, 该参数只针对选中文本)
     */
    public void setTextSize(int size, boolean isBold) {
        for (LoopView loop : loops) {
            loop.setTextSize(size, isBold);
        }
    }
}
