package ca.mcgill.dp2.group52.core;

import ca.mcgill.dp2.group52.enums.BuySell;
import ca.mcgill.dp2.group52.enums.Company;

/**
 * Created by sgandhi on 2/1/16.
 */
public class Computation implements Runnable {
    private CoreScheduler scheduler;
    private Network network;

    private Company company;
    private BuySell buy_sell;
    //private Network network;

    public Computation(CoreScheduler parent, Network network, Company company, BuySell buy_sell) {
        this.scheduler = parent;
        this.network = network;
        this.company = company;
        this.buy_sell = buy_sell;
    }

    @Override
    public void run() {

        if (this.buy_sell == BuySell.BUY) {
            //DO SOMETHING
        } else {
            //DO SOMETHING ELSE
        }
    }
    
    public void get_stock_data() throws InterruptedException {
        network.request_mktData(company);
        network.data_set.latches[company.ordinal()].await(); //Data is fully populated at this point, we can continue with calculations
    }

    public void run_reval() {

    }
}
