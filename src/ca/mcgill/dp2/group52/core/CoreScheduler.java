package ca.mcgill.dp2.group52.core;

import ca.mcgill.dp2.group52.enums.BuySell;
import ca.mcgill.dp2.group52.enums.Company;
import ca.mcgill.dp2.group52.runnables.*;

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

        volatile_stocks = new Company[5];

        /* DEBUG CODE NEEDS TO BE REMOVED LATER
        **************************************
         */
        for (int i = 0; i < 5; i++) {
            volatile_stocks[i] = Company.values()[i];
        }
    }

    public void start_logger(LinkedBlockingQueue<String> q) {
        //pool.scheduleAtFixedRate(new Logger(q), 0, 24, TimeUnit.HOURS);
    }
    
    public void schedule_volatility_analysis() {
        for (Company company : Company.values())
            pool.schedule(new VolatilityAnalysis(company, network), 0, TimeUnit.SECONDS);
        
        try {
            network.volatility_data_set.latch.await();
        } catch (InterruptedException e) {
            q.offer("EXCEPTION" + e.getMessage());
        }
        
        ForkJoinPool fj_pool = new ForkJoinPool(2);
        for (int i = 0; i < 5; i++) {
            FindMaxTask root = new FindMaxTask(network.volatility_data_set.std_dev, 0, network.volatility_data_set.std_dev.length - 1);
            Integer result = fj_pool.invoke(root);
            
            volatile_stocks[i] = Company.values()[result];
        }
    }
    
    public void lt_refresh() {
        for (Company company : volatile_stocks) {
            pool.schedule(new RefreshLTAverage(this, network, company), 0, TimeUnit.MINUTES);
        }
    }

    public void schedule_trading_routine() throws InterruptedException {
        network.data_set.latch.await();
        
        for (Company company : volatile_stocks) {
            pool.scheduleWithFixedDelay(new MovingAvgRoutine(this, network, company), 0, 1, TimeUnit.MINUTES);
        }
    }

    public void schedule_lt_refresh() throws InterruptedException {
        for (Company company : volatile_stocks) {
            pool.scheduleWithFixedDelay(new RefreshLTAverage(this, network, company), 0, 5, TimeUnit.MINUTES);
        }
    }
}
