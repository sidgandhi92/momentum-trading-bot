package ca.mcgill.dp2.group52.core;

import ca.mcgill.dp2.group52.enums.BuySell;
import ca.mcgill.dp2.group52.enums.Company;
import ca.mcgill.dp2.group52.runnables.MovingAvgRoutine;
import ca.mcgill.dp2.group52.runnables.VolatilityAnalysis;

import java.util.concurrent.*;

/**
 * Created by sgandhi on 1/24/16.
 */
public class CoreScheduler {

    public int loss_threshold, valuation_threshold;

    public Company volatile_stocks[];

    protected LinkedBlockingQueue q;

    private Core parent;
    protected Network network;

    private ScheduledExecutorService pool;

    public CoreScheduler(Core parent, Network network, LinkedBlockingQueue<String> q) {
        this.parent = parent;
        this.network = network;

        pool = Executors.newScheduledThreadPool(2*Runtime.getRuntime().availableProcessors());
        this.q = q;
        start_logger(q);

        volatile_stocks = new Company[10];
    }

    public void start_logger(LinkedBlockingQueue<String> q) {
        pool.scheduleAtFixedRate(new Logger(q), 0, 24, TimeUnit.HOURS);
    }
    
    public void schedule_volatility_analysis() {
        for (Company company : Company.values())
            pool.schedule(new VolatilityAnalysis(company, network), 0, TimeUnit.SECONDS);
        
        try {
            network.volatility_data_set.latch.await();
        } catch (InterruptedException e) {
            q.offer("EXCEPTION" + e.getMessage());
        }
        // Now need to schedule parallelized sorting for all the companies
        // plus the ability to get the 10 most volatile companies

        // all 10 companies should be put in volatile_stocks[];
    }

    public void schedule_trading_routine() {
        for (Company company : volatile_stocks) {
            pool.scheduleWithFixedDelay(new MovingAvgRoutine(this, network, company), 0, 1, TimeUnit.MINUTES);
        }
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
