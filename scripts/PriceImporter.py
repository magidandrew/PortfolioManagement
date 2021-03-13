#!/usr/bin/env python
import yfinance as yf
from pandas_datareader import data as pdr
import sys

# TODO CHANGE SAVE LOCATION TO BE A PARAMETER AND MAKE IT SPECIFIC TO THE APPLICATION
saveLocation = "/Users/andrewmagid/IdeaProjects/CapmModel/DownloadedRawPrices/"
args = sys.argv
del args[0]


if len(args) != 3:
    print("Arguments not valid. Requires: Ticker | Start-Date | End-Date")
    print("input date as yyyy-mm-dd")
else:

    # fund = yf.Ticker(args[0])
    # history = fund.history(period="1mo", interval="1mo", start=args[1], end=args[2], auto_adjust=True, rounding=False, threads=True)[
    #     'Close']  # dates are "yyyy-mm-dd"
    # history.dropna().to_csv(saveLocation + fund.info['longName'] + ".csv")  # determine where final save location will be
    # print("OK\tName:" + fund.info['longName'])

    # args[0] = ticker string | args[1] = start date | args[2] = end date
    history = yf.download(args[0], start=args[1], end=args[2], interval="1mo", auto_adjust=True, rounding=False, threads=True)['Close']
# if not history.isna():
    history.dropna().to_csv(saveLocation + args[0] + ".csv")
    print("OK\tName:" + args[0])
# else:
#     print("Cannot save. Dataframe is null for: " + args[0])


