# stock股票分析 README

## 功能
### 基本功能
* F1.1 保存上交所、深交所所有股票历史交易信息。
* F1.2 可以定义筛选条件，筛选查询符合条件的股票。
* F1.3 显示一只股票的历史走势。

### 分析
* A1.1 可以模拟在某一天购入某支股票，另一天售出，计算其年化收益率。
* A1.2 选股。预置几种策略(指数)，可根据策略选股。(列出指数TOP 10)

### 仿真交易
* E1.1 选择最优买入时间：
以1个月、3个月、6个月、1年为区间，分析选择一年的第几天作为开始买入日期，可以具有最大收益率。

* E1.2 寻找低风险组合：
给定的投入下，找到一种组合（两只股），历史上任何区间（一年期）的投资都稳赚不赔。（要求这两只股具有互补性，或者都是绩优股）

* E1.3 寻找绩优股
寻找发行至今年化收益率最高的股票


## 编译和打包

### 后端
用Idea打开，并使用gradle构建build.gradle。自动下载依赖等

### 前端
安装node.js
* 安装vscode javascript相关插件， beautify插件
* 用vscode打开resource/static目录
* 安装依赖包(webpack, babel, echarts, jquery等，详见package.bat): 进入resource/static目录后，执行 package.bat里的命令。
* 编译js：目录下执行：webpack。会将所有js合成为一个dist/bundle.js (webpack配置详见webpack.config.js)
* 各个html中引用的就是该bundle.js

### 数据库
* 安装mysql(5.6以上版本). root用户密码设置为root
* 初始化DB：执行initdb.bat

## 运行
* IDEA中调试启动本程序
* 登陆: 浏览器输入localhost:8080
* 如果数据库中无股票数据，进入数据菜单，执行数据补录


## 实现方法
### 基本功能
#### F1.1 保存上交所、深交所所有股票历史交易信息。
找到一个WEB API，获取到股票历史信息，包括：
时间范围：倒推20年-至今
精度：每日
每日价格、每日成交量
交易所、编码、名称

**所有股票代码**
http://quote.eastmoney.com/stock_list.html

**实时行情-sina**
http://hq.sinajs.cn/list=sh601006

var hq_str_sh601006="大秦铁路,8.370,8.380,8.520,8.530,8.350,8.510,8.520,38226381,322424158.000,410297,8.510,531500,8.500,555828,8.490,15100,8.480,162100,8.470,114600,8.520,1142150,8.530,656900,8.540,957400,8.550,202400,8.560,2019-04-30,15:00:00,00";

这个字符串由许多数据拼接在一起，不同含义的数据用逗号隔开了，按照程序员的思路，顺序号从0开始。
0：”大秦铁路”，股票名字；
1：”27.55″，今日开盘价；
2：”27.25″，昨日收盘价；
3：”26.91″，当前价格；
4：”27.55″，今日最高价；
5：”26.20″，今日最低价；
6：”26.91″，竞买价，即“买一”报价；
7：”26.92″，竞卖价，即“卖一”报价；
8：”22114263″，成交的股票数，由于股票交易以一百股为基本单位，所以在使用时，通常把该值除以一百；
9：”589824680″，成交金额，单位为“元”，为了一目了然，通常以“万元”为成交金额的单位，所以通常把该值除以一万；
10：”4695″，“买一”申请4695股，即47手；
11：”26.91″，“买一”报价；
12：”57590″，“买二”
13：”26.90″，“买二”
14：”14700″，“买三”
15：”26.89″，“买三”
16：”14300″，“买四”
17：”26.88″，“买四”
18：”15100″，“买五”
19：”26.87″，“买五”
20：”3100″，“卖一”申报3100股，即31手；
21：”26.92″，“卖一”报价
(22, 23), (24, 25), (26,27), (28, 29)分别为“卖二”至“卖四的情况”
30：”2008-01-11″，日期；
31：”15:05:32″，时间；



**历史数据-搜狐(推荐使用)**
http://q.stock.sohu.com/hisHq?code=zs_000001&start=20000504&end=20151215&stat=1&order=D&period=d

*返回值举例*
```
[{"status":0,"hq":[["2015-12-15","3518.13","3510.35","-10.31","-0.29%","3496.85","3529.96","200471344","27627494.00","-"],["2000-05-08","1848.17","1836.64","0.32","0.02%","1828.10","1852.90","8959131","96.44","-"]],"code":"zs_000001","stat":["累计:","2000-05-08至2015-12-15","1674.03","91.16%",998.23,6124.04,333257871767,35829038015.97,"-"]}]
```

|日期|开盘|收盘|涨跌额|涨跌幅|最低|最高|成交量(手)|成交金额(万)|换手率|
|----|----|----|------|------|----|----|----------|------------|----|
|累计：|2018-12-17至2019-04-30||1.11|30.16%|3.36|5.35|6635125|294961.05|90.61%|
|2019-04-30|4.40|4.79|0.44|10.11%|4.40|4.79|282533|13438.48|3.86%|

**历史数据-163**
举例：002566益盛药业，起始2015年1月4日，终止2016年1月8日。
http://quotes.money.163.com/service/chddata.html?code=1002566&start=20150104&end=20160108

如果不加时间参数，则下载所有历史数据。
http://quotes.money.163.com/service/chddata.html?code=1002566

*备注：下载为CSV*

#### F1.2 可以定义筛选条件，筛选查询符合条件的股票。
筛选条件组合定义为模板。
stockCondition = {
  编码：模糊匹配字符串
  名称：模糊匹配字符串
  价格：>=, <=, 区间
  成交量：>=, <=, 区间
  日期：查询所依据的日期。若不填，默认为昨日。
}

将模板保存为模板文件。*.condition
读取模板文件，转换为模板。
通过模板查询符合条件的股票集。

#### F1.3显示一只股票的历史走势。
寻找一个图表库(baidu)显示股票图标。


### 分析
#### A1.1 可以模拟在某一天购入某支股票，另一天售出，计算其年化收益率。
选定股票代码，买入日期，卖出日期，投入成本。
买入量 = (int)投入成本 / 买入价
利润 = (卖出价-买入价)*买入量 - 交易税
利润率 = 利润/投入成本

### A1.2 选股。预置几种策略(指数)，可根据策略选股。(列出指数TOP 10)

### 仿真交易
#### E1.1 选择最优买入时间：
以1个月、3个月、6个月、1年为区间，分析选择一年的第几天作为开始买入日期，可以具有最大收益率。

将总体大盘当作一只股票，以历史20年为迭代。
设定几个时间周期：1个月、3个月、6个月、1年为区间。
找出每年的最佳买入时间。

#### E1.2 寻找低风险组合：
给定的投入下，找到一种组合（两只股），历史上任何区间（一年期）的投资都稳赚不赔。（要求这两只股具有互补性，或者都是绩优股）
基因算法，购买股票A可以视为一种基因，购买股票B视为另一个基因


#### E1.3 寻找绩优股
寻找发行至今年化收益率最高的股票


## 参考文档
[设计文档][1]

[1]: /doc/design.md "设计文档"
[2]: /doc/sotckcode.txt