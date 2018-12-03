package com.nb20_p_wheelvol4.nb20_p_wheelvol4.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.nb20_p_wheelvol4.nb20_p_wheelvol4.R;
import com.weigan.loopgroup.LoopGroup;
import com.weigan.loopview.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private LoopGroup lgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        lgs.setListener(0, new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                toastInfo(0, index);
            }
        });
        lgs.setListener(2, new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                toastInfo(2, index);
            }
        });
    }

    private void toastInfo(int item, int index) {
        Toast.makeText(MainActivity.this, "current item is : " + item + "; had choice: " + index, Toast.LENGTH_SHORT).show();
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
