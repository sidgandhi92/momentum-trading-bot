package ca.mcgill.dp2.group52.runnables;

import ca.mcgill.dp2.group52.core.CoreScheduler;
import ca.mcgill.dp2.group52.core.DataSet;
import ca.mcgill.dp2.group52.core.Network;
import ca.mcgill.dp2.group52.enums.BuySell;
import ca.mcgill.dp2.group52.enums.Company;

/**
 * Created by sgandhi on 2/1/16.
 */
public class MovingAvgRoutine implements Runnable {
    private CoreScheduler scheduler;
    private Network network;

    private Company company;

    public MovingAvgRoutine(CoreScheduler parent, Network network, Company company) {
        this.scheduler = parent;
        this.network = network;
        this.company = company;
    }

    @Override
    public void run() {
        DataSet ds = network.data_set;
        int r = this.company.ordinal();

        network.request_histData(company, 1);

        ds.st_semaphore[r].acquireUninterruptibly();
        ds.lt_semaphore[r].acquireUninterruptibly();

        if (ds.st_data[r] > ds.lt_data[r]) {
            ds.lt_semaphore[r].release();

            if (ds.last_compare[r] == 0) {
                ds.last_compare[r] = 1;
                network.place_order(company, 5, "BUY", (byte)1);
            } else {
                network.place_order(company, 1, "BUY", (byte)1);
            }
        } else {
            if (ds.last_compare[r] == 1) {
                ds.last_compare[r] = 0;

                Integer qty = network.owned.get(company);
                if (qty != null) {
                    network.place_order(company, qty, "SELL", (byte)1);
                }
            }
        }
    }

}
