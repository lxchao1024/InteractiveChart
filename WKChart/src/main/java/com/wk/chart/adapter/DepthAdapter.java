package com.wk.chart.adapter;

import android.util.Log;

import androidx.annotation.NonNull;

import com.wk.chart.compat.Utils;
import com.wk.chart.compat.config.NormalBuildConfig;
import com.wk.chart.entry.DepthEntry;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DepthAdapter extends AbsAdapter<DepthEntry, NormalBuildConfig> {
    public final static int BID = 0;//买单类型
    public final static int ASK = 1;//卖单类型

    public DepthAdapter(int baseScale, int quoteScale, String baseUnit, String quoteUnit) {
        super(new NormalBuildConfig());
        setScale(baseScale, quoteScale, baseUnit, quoteUnit);
    }

    @Override
    void buildData(@NonNull NormalBuildConfig buildConfig, @NonNull List<DepthEntry> data, int startPosition) {
        buildConfig.setInit(true);
        buildScaleValue(data, startPosition);
        computeData(data, startPosition);
    }

    /**
     * 在给定的范围内，计算最大值和最小值
     */
    @Override
    public void computeMinAndMax(int start, int end) {
    }

    /**
     * 构建精度值
     */
    private void buildScaleValue(@NonNull List<DepthEntry> data, int startPosition) {
        for (int i = startPosition, z = data.size(); i < z; i++) {
            data.get(i).buildScaleValue(getScale());
        }
    }

    /**
     * 数据计算
     */
    private void computeData(@NonNull List<DepthEntry> data, int startPosition) {
        if (Utils.listIsEmpty(data)) {
            return;
        }
        List<DepthEntry> bids = new ArrayList<>();//买单数据
        List<DepthEntry> asks = new ArrayList<>();//卖单数据
        for (int i = startPosition, z = data.size(); i < z; i++) {
            DepthEntry item = data.get(i);
            switch (item.getType()) {
                case BID://买单
                    bids.add(item);
                    break;
                case ASK://卖单
                    asks.add(item);
                    break;
            }
        }
        data.clear();
        if ((Utils.listIsEmpty(bids) && Utils.listIsEmpty(asks))) {
            return;
        }

//        Collections.sort(asks, new Comparator<DepthEntry>() {
//            @Override
//            public int compare(DepthEntry t0, DepthEntry t1) {
//                BigDecimal d1 = new BigDecimal(t0.getPrice().result);
//                BigDecimal d2 = new BigDecimal(t1.getPrice().result);
//                return d1.compareTo(d2);
//            }
//        });
//
//        Collections.sort(asks, new Comparator<DepthEntry>() {
//            @Override
//            public int compare(DepthEntry t0, DepthEntry t1) {
//                BigDecimal d1 = new BigDecimal(t0.getPrice().result);
//                BigDecimal d2 = new BigDecimal(t1.getPrice().result);
//                return d1.compareTo(d2);
//            }
//        });

        data.addAll(bids);
        data.addAll(asks);
    }
}