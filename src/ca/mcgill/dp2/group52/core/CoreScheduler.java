package ca.mcgill.dp2.group52.core;

import ca.mcgill.dp2.group52.enums.BuySell;
import ca.mcgill.dp2.group52.enums.Company;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by sgandhi on 1/24/16.
 */
public class CoreScheduler {

    public int loss_threshold, valuation_threshold;

    private Core parent;
    protected Network network;

    private ScheduledExecutorService pool;

    public CoreScheduler(Core parent, Network network) {
        this.parent = parent;
        this.network = network;

        pool = Executors.newScheduledThreadPool(4);
    }

    public void schedule_all() {
        // Schedule Reval
        schedule_buy_reval(Company.AXP);
        schedule_sell_reval(Company.AXP);
        schedule_loss_reval();
        // Schedule DB write
    }

    public void schedule_buy_reval(Company company) {
        pool.scheduleAtFixedRate(new Computation(this, network, company, BuySell.BUY), 0, 24, TimeUnit.HOURS);
    }


    public void schedule_buy_reval_all() {
        for (Company company : Company.values()) {
            schedule_buy_reval(company);
        }

    }

    public void schedule_sell_reval(Company company) {
        pool.scheduleAtFixedRate(new Computation(this, network, company, BuySell.SELL), 0, 24, TimeUnit.HOURS);
    }

    public void schedule_loss_reval() {

    }




}
