package com.hl.stock.core.base.analysis;

import com.hl.stock.core.base.analysis.advice.StockAdvice;
import com.hl.stock.core.base.analysis.advice.StockAdviceStrategy;
import com.hl.stock.core.base.analysis.advice.StockAdvisor;
import com.hl.stock.core.base.analysis.stat.StockStat;
import com.hl.stock.core.base.analysis.stat.StockStatIndex;
import com.hl.stock.core.base.analysis.stat.StockStator;
import com.hl.stock.core.base.analysis.validate.StockValidator;
import com.hl.stock.core.base.data.StockDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 分析模块的总服务，提供分析功能对外接口
 */
@Service
public class StockAnalysis {

    @Autowired
    private StockDao stockDao;

    // 推荐器
    @Autowired
    private StockAdvisor stockAdvisor;

    // 统计器
    @Autowired
    private StockStator stockStator;

    // 验证器
    @Autowired
    private StockValidator stockValidator;

    private StockStrategyEmulator stockStrategyEmulator;

    // 最优选股策略
    private StockAdviceStrategy bestStrategy;

    @Autowired
    public void setStockStrategyEmulator(StockStrategyEmulator stockStrategyEmulator) {
        this.stockStrategyEmulator = stockStrategyEmulator;
        bestStrategy = stockStrategyEmulator.findBestStrategy();
    }

    /**
     * 统计数据
     *
     * @param code      股票代码
     * @param startDate 起始时间
     * @param endDate   结束时间
     * @param index     统计指标
     * @return 统计结果
     */
    public StockStat stat(StockStatIndex index, String code, Date startDate, Date endDate) {
        return stockStator.stat(index, stockDao.loadData(code, startDate, endDate));
    }

    /**
     * 使用最优策略，给出建议是否应该在当天买这支股票
     *
     * @param code    股票
     * @param buyDate 购买日期
     * @return 建议
     */
    public StockAdvice advice(String code, Date buyDate) {
        return bestStrategy.advice(code, buyDate);
    }

    /**
     * 使用最优策略，推荐可以购买的股票
     *
     * @param buyDate 买入日期
     * @return 推荐股票清单，按照利润率从高到底排序
     */
    public List<StockAdvice> suggestStocks(Date buyDate) {
        return stockAdvisor.suggestStocks(buyDate, bestStrategy);
    }

}
