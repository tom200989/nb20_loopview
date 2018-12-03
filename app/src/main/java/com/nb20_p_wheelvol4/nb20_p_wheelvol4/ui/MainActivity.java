package com.nb20_p_wheelvol4.nb20_p_wheelvol4.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.nb20_p_wheelvol4.nb20_p_wheelvol4.R;
import com.qianli.listener.OnLoopSelecteListener;
import com.qianli.loopgroup.LoopGroup;
import com.qianli.tools.LoopTools;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private LoopGroup lgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // session1();
        session2();
    }

    private void session2() {
        // 获取数据
        List<String> months = getMonths();
        List<String> days = getDays();
        List<String> years = getYears();
        // 设置参数监听器等
        lgs = findViewById(R.id.lgs);
        lgs.setCenterColor("#434343");
        lgs.setDividerColor("#cccccc");
        lgs.setOuterTextColor("#cccccc");
        lgs.setInitPosition(0, 0, 0, 0);
        lgs.setDatas(months, days, years, null);
        lgs.setItemsVisibleCount(9);
        lgs.setLineSpace(2.8f);
        lgs.setNotLoop();
        lgs.setTextScaleX(1.0f);
        lgs.setScrollSpeeds(10);
        lgs.setTextSize(18, true);
        lgs.setListener(0, new OnLoopSelecteListener() {
            @Override
            public void onLoopSelected(int index, String content) {
                toastInfo(0, index, content);
            }
        });
        lgs.setListener(1, new OnLoopSelecteListener() {
            @Override
            public void onLoopSelected(int index, String content) {
                toastInfo(1, index, content);
            }
        });
        lgs.setListener(2, new OnLoopSelecteListener() {
            @Override
            public void onLoopSelected(int index, String content) {
                toastInfo(2, index, content);
            }
        });
    }

    private List<String> getYears() {
        List<String> years = new ArrayList<>();
        years.add("2018");
        years.add("2017");
        return years;
    }

    private List<String> getDays() {
        return LoopTools.getDaysFromMonths(4, 2018);
    }

    private List<String> getMonths() {
        return LoopTools.getMonthsEnglish();
    }


    /* -------------------------------------------- session1 -------------------------------------------- */

    /**
     * 场景1
     */
    private void session1() {
        // 获取数据
        List<String> hours = getHours();
        List<String> hourText = getHourText();
        List<String> mins = getMins();
        List<String> minText = getMinText();
        // 设置参数监听器等
        lgs = findViewById(R.id.lgs);
        lgs.setCenterColor("#434343");
        lgs.setDividerColor("#cccccc");
        lgs.setOuterTextColor("#cccccc");
        lgs.setInitPosition(0, 0, 0, 0);
        lgs.setDatas(hours, hourText, mins, minText);
        lgs.setItemsVisibleCount(9);
        lgs.setLineSpace(2.8f);
        lgs.setNotLoop();
        lgs.setTextScaleX(1.0f);
        lgs.setScrollSpeeds(10);
        lgs.setTextSize(18, true);
        lgs.setNotFling(1, 3);
        lgs.setListener(0, new OnLoopSelecteListener() {
            @Override
            public void onLoopSelected(int index, String content) {
                toastInfo(0, index, content);
            }
        });
        lgs.setListener(2, new OnLoopSelecteListener() {
            @Override
            public void onLoopSelected(int index, String content) {
                toastInfo(2, index, content);
            }
        });
    }

    private void toastInfo(int item, int index, String content) {
        Toast.makeText(MainActivity.this,// 
                "current item is : " + item +//
                        "; had choice: " + index +//
                        "; content is: " + content//
                , Toast.LENGTH_SHORT).show();
    }

    private List<String> getMinText() {
        List<String> minTexts = new ArrayList<>();
        minTexts.add("Mins");
        return minTexts;
    }

    private List<String> getMins() {
        List<String> mins = new ArrayList<>();
        for (int i = 0; i < 59; i++) {
            mins.add(String.valueOf(i));
        }
        return mins;
    }

    private List<String> getHourText() {
        List<String> hourTexts = new ArrayList<>();
        hourTexts.add("Hours");
        return hourTexts;
    }

    private List<String> getHours() {
        List<String> hours = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hours.add(String.valueOf(i));
        }
        return hours;
    }
}
