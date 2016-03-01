package ca.mcgill.dp2.group52.runnables;

import ca.mcgill.dp2.group52.core.CoreScheduler;
import ca.mcgill.dp2.group52.core.DataSet;
import ca.mcgill.dp2.group52.core.Network;
import ca.mcgill.dp2.group52.enums.BuySell;
import ca.mcgill.dp2.group52.enums.Company;

import java.util.Calendar;
import java.util.GregorianCalendar;

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
        System.out.println("RTN: " + this.company.name());
        try {
            DataSet ds = network.data_set;
            int r = this.company.ordinal();

            Calendar cl = new GregorianCalendar();

            if (cl.get(Calendar.HOUR_OF_DAY) >= 14) {
                if (cl.get(Calendar.MINUTE) >= 50) {
                    Integer qty = network.owned.get(company);
                    if (qty != null) {
                        network.place_order(company, qty, "SELL", (byte) 1);
                    }

                    return;
                }
            }

            network.request_histData(company, 1);

            ds.st_semaphore[r].acquireUninterruptibly();
            //ds.lt_semaphore[r].acquireUninterruptibly();

            if (ds.continue_trading[r] == 0) {
                ds.continue_trading[r] = 1;
                return;
            }

            //System.out.println("Company:" + company.name() + " LT:" + ds.lt_data[r] + " ST:" + ds.st_data[r]);

            if (ds.st_data[r] > ds.lt_data[r]) {
                // ds.lt_semaphore[r].release();
                //System.out.println("Company:" + company.name() + " LT:" + ds.lt_data[r] + " ST:" + ds.st_data[r])
                if (ds.last_compare[r] == 0) {
                    ds.last_compare[r] = 1;
                    System.out.println("BUY: " + this.company.name());
                    network.place_order(company, 50, "BUY", (byte) 1);
                } else {
                    network.place_order(company, 10, "BUY", (byte) 1);
                    System.out.println("BUY: " + this.company.name());
                }
            } else {
                //ds.lt_semaphore[r].release();

                if (ds.last_compare[r] == 1) {
                    ds.last_compare[r] = 0;

                    Integer qty = network.owned.get(company);
                    if (qty != null) {
                        network.place_order(company, qty, "SELL", (byte) 1);
                        System.out.println("SELL: " + this.company.name());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
