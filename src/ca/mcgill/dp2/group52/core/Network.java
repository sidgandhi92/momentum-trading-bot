package ca.mcgill.dp2.group52.core;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.LinkedBlockingQueue;

import ca.mcgill.dp2.group52.enums.Company;
import ca.mcgill.dp2.group52.logging.LogUtil;
import com.ib.client.*;
import com.ib.client.TagValue;
import com.ib.client.Order;

import java.util.Vector;


public class Network extends Thread implements EWrapper {
    private Core parent;
    private ExecutorService pool;

    protected LinkedBlockingQueue<String> q;

    private EClientSocket client;

    private final int return_port = 7496;
    private int return_clientId; //TODO assign client ID
    private String ip_add = "";
    protected Semaphore sem_oid;
    private int next_orderId;

    //private Vector<TagValue> mkt_data_options;

    protected DataSet data_set;
    private HashMap<Company, Integer> order_id_mapping;

    public Date date;
    public SimpleDateFormat df_ib = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    public SimpleDateFormat df_user = new SimpleDateFormat("HH:mm:ss ");

    public Network (Core parent, DataSet data_set, LinkedBlockingQueue queue) {
        this.parent = parent;
        //this.pool = pool;
        this.data_set = data_set;

        order_id_mapping = new HashMap<Company, Integer>();

        sem_oid = new Semaphore(1, true);
        this.q = queue;

        client = new EClientSocket(this);
    }

    protected void connect() {
        client.eConnect(ip_add, return_port, return_clientId);

        try {
            while (!client.isConnected());
            q.offer("Connected to TWS server version " + client.serverVersion() + " at " + client.TwsConnectionTime());
        } catch (Exception e) {
            q.offer("EXCEPTION while connecting: " + e.getMessage());
        }
    }

    protected boolean check_connection() {
        return client.isConnected();
    }

    protected void request_mktData(Company company) {
        Contract contract = company.create_contract();

        Vector<TagValue> mkt_data_options = new Vector<TagValue>();

        sem_oid.acquireUninterruptibly();
        order_id_mapping.put(company, next_orderId);
        client.reqMktData(next_orderId, contract, null, true, mkt_data_options);
        next_orderId++;

        sem_oid.release();
    }

    protected void cancel_mktData(Company company) {

        int oid = order_id_mapping.get(company);
        client.cancelMktData(oid);

        System.out.println("MktDataCanceled for oid " + oid);
    }

    protected void request_histData(Company company) {
        Contract contract = company.create_contract();

        Vector<TagValue> mkt_data_options = new Vector<TagValue>();

        sem_oid.acquireUninterruptibly();
        order_id_mapping.put(company, next_orderId);
        client.reqHistoricalData(next_orderId, contract, "20150115 16:00:00", parent.data_period, parent.data_granularity, "TRADES", 1, 1, mkt_data_options);
        next_orderId++;
        sem_oid.release();
    }
    
    protected void place_order(Company company, int quantity, String buy_sell) {
        Contract contract = company.create_contract();
        Order order = company.create_order(buy_sell, quantity);

        sem_oid.acquireUninterruptibly();

        order_id_mapping.put(company, next_orderId);
        client.placeOrder(next_orderId, contract, order);

        q.offer(df_user.format(Calendar.getInstance().getTime()) + buy_sell + " ORDER placed: " + company.name() + "x" + quantity);
        next_orderId++;

        sem_oid.release();
    }

    @Override
    public void tickPrice(int tickerId, int field, double price, int canAutoExecute) {
        // Might need to figure out some asynchronous way to put this stuff in
        //data_set.set_data(order_id_mapping.get(tickerId), field, price);

        System.out.println("TICK PRICE: " + price + ", field: " + field + ", tickerId: " + tickerId);
    }

    @Override
    public void tickSize(int tickerId, int field, int size) {
        // Might need to figure out some asynchronous way to put this stuff
        //data_set.set_data(order_id_mapping.get(tickerId), field, size);
    }

    @Override
    public void tickOptionComputation(int tickerId, int field, double impliedVol, double delta, double optPrice, double pvDividend, double gamma, double vega, double theta, double undPrice) {

    }

    @Override
    public void tickGeneric(int tickerId, int tickType, double value) {

        System.out.println("TICK: " + value + ", TYPE: " + tickType + ", tickerId: " + tickerId);

    }

    @Override
    public void tickString(int tickerId, int tickType, String value) {

    }

    @Override
    public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints, double impliedFuture, int holdDays, String futureExpiry, double dividendImpact, double dividendsToExpiry) {

    }

    @Override
    public void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {

    }

    @Override
    public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {

    }

    @Override
    public void openOrderEnd() {

    }

    @Override
    public void updateAccountValue(String key, String value, String currency, String accountName) {

    }

    @Override
    public void updatePortfolio(Contract contract, int position, double marketPrice, double marketValue, double averageCost, double unrealizedPNL, double realizedPNL, String accountName) {

    }

    @Override
    public void updateAccountTime(String timeStamp) {

    }

    @Override
    public void accountDownloadEnd(String accountName) {

    }

    @Override
    public void nextValidId(int orderId) {

        next_orderId = orderId;

    }

    @Override
    public void contractDetails(int reqId, ContractDetails contractDetails) {

    }

    @Override
    public void bondContractDetails(int reqId, ContractDetails contractDetails) {

    }

    @Override
    public void contractDetailsEnd(int reqId) {

    }

    @Override
    public void execDetails(int reqId, Contract contract, Execution execution) {

    }

    @Override
    public void execDetailsEnd(int reqId) {

    }

    @Override
    public void updateMktDepth(int tickerId, int position, int operation, int side, double price, int size) {

    }

    @Override
    public void updateMktDepthL2(int tickerId, int position, String marketMaker, int operation, int side, double price, int size) {

    }

    @Override
    public void updateNewsBulletin(int msgId, int msgType, String message, String origExchange) {

    }

    @Override
    public void managedAccounts(String accountsList) {

    }

    @Override
    public void receiveFA(int faDataType, String xml) {

    }

    @Override
    public void historicalData(int reqId, String date, double open, double high, double low, double close, int volume, int count, double WAP, boolean hasGaps) {

        System.out.println("DATE: " + date + ", HIGH: " + high + ", LOW: " + low + ", WAP: " + WAP + ", reqId: " + reqId);
    }

    @Override
    public void scannerParameters(String xml) {

    }

    @Override
    public void scannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark, String projection, String legsStr) {

    }

    @Override
    public void scannerDataEnd(int reqId) {

    }

    @Override
    public void realtimeBar(int reqId, long time, double open, double high, double low, double close, long volume, double wap, int count) {

    }

    @Override
    public void currentTime(long time) {

    }

    @Override
    public void fundamentalData(int reqId, String data) {

    }

    @Override
    public void deltaNeutralValidation(int reqId, UnderComp underComp) {

    }

    @Override
    public void tickSnapshotEnd(int reqId) {
        //data_set.latches[order_id_mapping.get(reqId).ordinal()].countDown();
        System.out.println("SNAPSHOT ENDED FOR REQID: " + reqId);
    }

    @Override
    public void marketDataType(int reqId, int marketDataType) {

    }

    @Override
    public void commissionReport(CommissionReport commissionReport) {

    }

    @Override
    public void position(String account, Contract contract, int pos, double avgCost) {

    }

    @Override
    public void positionEnd() {

    }

    @Override
    public void accountSummary(int reqId, String account, String tag, String value, String currency) {

    }

    @Override
    public void accountSummaryEnd(int reqId) {

    }

    @Override
    public void verifyMessageAPI(String apiData) {

    }

    @Override
    public void verifyCompleted(boolean isSuccessful, String errorText) {

    }

    @Override
    public void displayGroupList(int reqId, String groups) {

    }

    @Override
    public void displayGroupUpdated(int reqId, String contractInfo) {

    }

    @Override
    public void error(Exception e) {
        LogUtil.log("EXCEPTION thrown by TWS API: " + e.getMessage());
        //TODO
    }

    @Override
    public void error(String str) {

        //TODO

    }

    @Override
    public void error(int id, int errorCode, String errorMsg) {

        //TODO

    }

    @Override
    public void connectionClosed() {

    }
}
