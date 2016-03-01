package ca.mcgill.dp2.group52.runnables;

import ca.mcgill.dp2.group52.core.CoreScheduler;
import ca.mcgill.dp2.group52.core.DataSet;
import ca.mcgill.dp2.group52.core.Network;
import ca.mcgill.dp2.group52.enums.BuySell;
import ca.mcgill.dp2.group52.enums.Company;

/**
 * Created by sgandhi on 2/1/16.
 */
public class RefreshLTAverage implements Runnable {
    private CoreScheduler scheduler;
    private Network network;

    private Company company;

    public RefreshLTAverage(CoreScheduler parent, Network network, Company company) {
        this.scheduler = parent;
        this.network = network;
        this.company = company;
    }

    @Override
    public void run() {
        network.request_histData(company, 0);
    }

}
