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

public class DepthAdapterCpy extends AbsAdapter<DepthEntry, NormalBuildConfig> {
    public final static int BID = 0;//买单类型
    public final static int ASK = 1;//卖单类型

    public DepthAdapterCpy(int baseScale, int quoteScale, String baseUnit, String quoteUnit) {
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
//
        Collections.sort(bids, new Comparator<DepthEntry>() {
            @Override
            public int compare(DepthEntry t0, DepthEntry t1) {
                return new BigDecimal(t0.getPrice().result).compareTo(new BigDecimal(t1.getPrice().result));
            }
        });

        for (DepthEntry entry : bids) {
            Log.e("DepthAdapter", "sort, bids, enter.price: " + entry.getPrice().result);
        }
        for (DepthEntry entry : asks) {
            Log.e("DepthAdapter", "asks, enter.price: " + entry.getPrice().result);
        }

        if ((Utils.listIsEmpty(bids) && Utils.listIsEmpty(asks))) {
            return;
        }
        if (Utils.listIsEmpty(bids)) {
            Log.e("DepthAdapter", "listIsEmpty(bids), asks.get(0).getPrice().result: " + asks.get(0).getPrice().result);


            DepthEntry bidBean = new DepthEntry(getScale(), asks.get(0).getPrice().result, 0L, 0L, BID, new Date());
            bids.add(bidBean);
        } else if (Utils.listIsEmpty(asks)) {
            if (bids.size() == 1) {
                DepthEntry bidBean = new DepthEntry(getScale(), 0L, 0L, bids.get(0).getTotalAmount().result, BID, new Date());
                bids.add(bidBean);
            }
            //如果卖单为空，添加数据
            Log.e("DepthAdapter", "listIsEmpty(asks), bids.get(0).getPrice().result + (bids.get(0).getPrice().result - bids.get(bids.size() - 1).getPrice().result): " + (bids.get(0).getPrice().result + (bids.get(0).getPrice().result - bids.get(bids.size() - 1).getPrice().result)));
            long asksEmptySpace = bids.get(0).getPrice().result;
            DepthEntry askBean = new DepthEntry(getScale(), asksEmptySpace, 0L, 0L, ASK, new Date());
            asks.add(askBean);

            asksEmptySpace = bids.get(0).getPrice().result + (bids.get(0).getPrice().result - bids.get(bids.size() - 1).getPrice().result);
            askBean = new DepthEntry(getScale(), asksEmptySpace, 0L, 0L, ASK, new Date());
            asks.add(askBean);
        }

        Log.e("DepthAdapter", "asks.size: " + asks.size() + ", bids.size: " + bids.size());

        if (bids.size() == 1) {
            DepthEntry bidBean = new DepthEntry(getScale(), 0L, 0L, bids.get(0).getTotalAmount().result, BID, new Date());
            bids.add(bidBean);
        }
        if (asks.size() == 1) {
            long priceResult = asks.get(0).getPrice().result + (bids.get(0).getPrice().result - bids.get(bids.size() - 1).getPrice().result);
            DepthEntry askBean = new DepthEntry(getScale(), priceResult, 0L, asks.get(0).getTotalAmount().result, ASK, new Date());
            asks.add(askBean);

            //计算差值
            if (asks.get(0).getPrice().result != bids.get(0).getPrice().result) {
                long diff = Math.abs(asks.get(0).getPrice().result - bids.get(0).getPrice().result) / 2;
//                bids.get(0).getPrice().result += diff;
//                asks.get(0).getPrice().result -= diff;

                Log.e("DepthAdapter", "=== asks[0]: " + asks.get(0).getPrice().result + ", bids[0]: " + bids.get(0).getPrice().result + ", diff: " + diff);
                askBean = new DepthEntry(getScale(), asks.get(0).getPrice().result - diff, asks.get(0).getAmount().result, asks.get(0).getTotalAmount().result, ASK, new Date());
                asks.add(0, askBean);

                Log.e("DepthAdapter", "=== asks[0]: " + asks.get(0).getPrice().result + ", bids[0]: " + bids.get(0).getPrice().result + ", diff: " + diff);

                askBean = new DepthEntry(getScale(), bids.get(0).getPrice().result + diff, bids.get(0).getAmount().result, bids.get(0).getTotalAmount().result, BID, new Date());
                bids.add(askBean);

                Log.e("DepthAdapter", "asks[0]: " + asks.get(0).getPrice().result + ", bids[0]: " + bids.get(0).getPrice().result + ", diff: " + diff);
            }
        }
        Log.e("DepthAdapter", "asks.size: " + asks.size() + ", bids.size: " + bids.size());

        for (DepthEntry entry : bids) {
            Log.e("DepthAdapter", "bids, enter.price: " + entry.getPrice().result);
        }
        for (DepthEntry entry : asks) {
            Log.e("DepthAdapter", "asks, enter.price: " + entry.getPrice().result);
        }

        //保持买单/卖单数据的价格跨度值一致
        long bidsDiff = bids.get(0).getPrice().result - bids.get(bids.size() - 1).getPrice().result;//买单数据的价格跨度值
        long asksDiff = asks.get(asks.size() - 1).getPrice().result - asks.get(0).getPrice().result;//卖单数据的价格跨度值

//        Log.e("DepthAdapter", "bids.get(0).getPrice().result: " + bids.get(0).getPrice().result);
//        Log.e("DepthAdapter", "bids.get(bids.size() - 1).getPrice().result: " + bids.get(bids.size() - 1).getPrice().result);
//
//        Log.e("DepthAdapter", "asks.get(asks.size() - 1).getPrice().result: " + asks.get(asks.size() - 1).getPrice().result);
//        Log.e("DepthAdapter", "asks.get(0).getPrice().result: " + asks.get(0).getPrice().result);

        Log.e("DepthAdapter", "bidsDiff: " + bidsDiff + ", asksDiff: " + asksDiff + ", space: " + (bidsDiff - asksDiff));

        long space = Math.abs(bidsDiff - asksDiff) / 2;

        if (bidsDiff > asksDiff) {
            //补齐最低值
            long minPrice = bids.get(0).getPrice().result - asksDiff;
            bids = bids.subList(0, indexOfDiff(minPrice, 0, bids.size() - 1, bids, 1));//剔除不在跨度范围内的数据
            DepthEntry minBean = new DepthEntry(getScale(), minPrice, 0L,
                    bids.get(bids.size() - 1).getTotalAmount().result+space, BID, new Date());
            bids.add(minBean);
        } else if (bidsDiff < asksDiff) {
            //补齐最高值
            long maxPrice = asks.get(0).getPrice().result + bidsDiff;
            asks = asks.subList(0, indexOfDiff(maxPrice, 0, asks.size() - 1, asks, 2));//剔除不在跨度范围内的数据
            DepthEntry maxBean = new DepthEntry(getScale(), maxPrice, 0L,
                    asks.get(asks.size() - 1).getTotalAmount().result-space, ASK, new Date());
            asks.add(maxBean);
        } else {
//            space = asks.get(0).getPrice().result - bids.get(0).getPrice().result;
//            Log.e("DepthAdapter", "=====asks.get(0).getPrice().result: " + asks.get(0).getPrice().result);
//            Log.e("DepthAdapter", "=====bids.get(0).getPrice().result: " + bids.get(0).getPrice().result);
//            if (space > 0) {
//                asks.get(0).getPrice().result = asks.get(0).getPrice().result + space / 2;
//                bids.get(0).getPrice().result = bids.get(0).getPrice().result + space / 2;
//            }
//            Log.e("DepthAdapter", "=====asks.get(0).getPrice().result: " + asks.get(0).getPrice().result);
//            Log.e("DepthAdapter", "=====bids.get(0).getPrice().result: " + bids.get(0).getPrice().result);
        }


//        Collections.sort(bids, new Comparator<DepthEntry>() {
//            @Override
//            public int compare(DepthEntry t0, DepthEntry t1) {
//                return new BigDecimal(t1.getPrice().result).compareTo(new BigDecimal(t0.getPrice().result));
//            }
//        });
        for (DepthEntry entry : bids) {
            Log.e("DepthAdapter", "bids, enter.price: " + entry.getPrice().result);
        }
        data.addAll(bids);
        data.addAll(asks);
    }

    /**
     * 二分查找当前值的index
     */
    private int indexOfDiff(long value, int start, int end, List<DepthEntry> data,
                            int type) {
        int count = data.size();
        if (count == 0) {
            return 0;
        } else if (end == start) {
            return end + 1;
        } else if (end - start == 1) {
            return end;
        }
        int mid = start + (end - start) / 2;
        long midValue = data.get(mid).getPrice().result;
        switch (type) {
            case 1://反向查找
                if (value < midValue) {
                    return indexOfDiff(value, mid, end, data, type);
                } else if (value > midValue) {
                    return indexOfDiff(value, start, mid, data, type);
                } else {
                    return mid + 1;
                }
            case 2://正向查找
                if (value < midValue) {
                    return indexOfDiff(value, start, mid, data, type);
                } else if (value > midValue) {
                    return indexOfDiff(value, mid, end, data, type);
                } else {
                    return mid + 1;
                }
        }
        return 0;
    }

}
