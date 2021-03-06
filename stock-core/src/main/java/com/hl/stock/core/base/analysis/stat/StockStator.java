package com.hl.stock.core.base.analysis.stat;

import com.hl.stock.core.base.model.StockData;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 统计器
 */
@Component
public class StockStator {
    private static final int WAN = 10000;
    private static final int SHOU = 100;

    /**
     * 获取某种指标下的价格
     *
     * @param data  数据
     * @param index 指标
     * @return
     */
    private static double getPrice(StockStatIndex index, StockData data) {
        switch (index) {
            case OpenPrice:
                return data.getOpenPrice();
            case ClosePrice:
                return data.getClosePrice();
        }
        // 默认使用开盘价
        return data.getOpenPrice();
    }

    /**
     * 统计数据
     *
     * @param data  股票数据
     * @param index 统计指标
     * @return 统计结果
     */
    public StockStat stat(StockStatIndex index, List<StockData> data) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        StockData firstData = data.get(0);
        double totalAmountMoney = 0;
        double totalAmount = 0;
        StockData earlist = firstData;
        StockData latest = firstData;
        StockData high = firstData;
        StockData low = firstData;
        double highPrice = getPrice(index, firstData);
        double lowPrice = getPrice(index, firstData);

        for (StockData d : data) {
            totalAmountMoney += d.getAmountMoney();
            totalAmount += d.getAmount();

            Date date = d.getDate();
            if (date.before(earlist.getDate())) {
                earlist = d;
            }
            if (date.after(latest.getDate())) {
                latest = d;
            }

            double price = getPrice(index, d);
            if (price > highPrice) {
                high = d;
                highPrice = price;
            }
            if (price < lowPrice) {
                low = d;
                lowPrice = price;
            }

        }
        double avgPrice = (totalAmountMoney * WAN) / (totalAmount * SHOU);

        StockStat stockStat = new StockStat();
        stockStat.setIndex(index);
        stockStat.setAvgPrice(avgPrice);
        stockStat.setEarliest(earlist);
        stockStat.setLatest(latest);
        stockStat.setHigh(high);
        stockStat.setLow(low);
        stockStat.setHighPrice(highPrice);
        stockStat.setLowPrice(lowPrice);

        return stockStat;
    }


}
