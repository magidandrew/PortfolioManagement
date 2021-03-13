# PortfolioManagement

GUI investment portfolio management application.

- Automatically downloads historical prices for selected securities from Yahoo Finance.
- Calculates security monthly/annualized risk (st. dev) and return.
- Calculates security beta to assess its risk compared to the market benchmark.
- Displays graphical visualization of security historical data and returns.
- Calculates portfolio’s annualized risk, return, and beta based on its asset allocation.
    - Calculates covariances of security historical returns in order to compute portfolio variance.
    - Allows user to load and save portfolio’s asset allocation (security weights).
- Uses Gurobi optimizer to generate a graphical efficient frontier of optimal portfolios.

## Dependencies:
- [nasdaq trader](http://ftp.nasdaqtrader.com/Trader.aspx?id=symbollookup) for the list of available securities
  (csvs are stored in **SecurityUniverse**)
- [JFreeChart](https://www.jfree.org/jfreechart/) for graphing
- Python script I wrote for downloading the securities using the [yFinance](https://pypi.org/project/yfinance/) package
  located in **scripts**