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
        
        ForkJoinPool findMaxPool = new ForkJoinPool(2);
        for (int i = 0; i < 10; i++) {
            FindMaxTask root = new FindMaxTask(network.volatility_data_set.std_dev);
            Integer result = pool.invoke(root);
            
            volatile_stocks[i] = Company.values()[result];
        }
    }

    public void schedule_trading_routine() {
        for (Company company : volatile_stocks) {
            pool.scheduleWithFixedDelay(new MovingAvgRoutine(this, network, company), 0, 1, TimeUnit.MINUTES);
        }
    }
}
