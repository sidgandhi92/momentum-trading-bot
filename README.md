# mlt

Packages:
  1. Core: the heart of the application; the Network class handles all interaction with Interactive Brokers, the DataSet and VolatilityDataSet classes holds structured short-term market and calculated data, CoreScheduler handles the concurrency.
  2. Runnables contain the strategies themselves.
  
The idea was to create a framework where new strategies would simply have to be created and inserted as additional Runnable classes (plug & play). The momentum strategy built into this application was marginally successful - it worked in markets which showed short-term trends - at least 5 minutes, but there were too many false signals in markets which moved sideways, and any marginal profit or loss would get eaten up by broker commissions!
  
Additionally, this was using the paper trading platform, so there was no deep order book access - buy/sell orders would only get filled on the ask/bid prices, so market orders were used instead of Limit order types.
