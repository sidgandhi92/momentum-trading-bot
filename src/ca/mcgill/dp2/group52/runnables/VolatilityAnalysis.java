package ca.mcgill.dp2.group52.runnables;

import ca.mcgill.dp2.group52.core.CoreScheduler;
import ca.mcgill.dp2.group52.core.Network;
import ca.mcgill.dp2.group52.core.VolatilityDataSet;
import ca.mcgill.dp2.group52.enums.BuySell;
import ca.mcgill.dp2.group52.enums.Company;

/**
 * Created by sgandhi on 2/1/16.
 */
public class VolatilityAnalysis implements Runnable {
  
  private CoreScheduler scheduler;
  private Network network;
  private Company company;
  
  public VolatilityAnalysis(Company company, Network network) {
    this.company = company;
    this.network = network;
  }
  
  public void run() {
    VolatilityDataSet vds = network.volatility_data_set;
    int r = company.ordinal();
    network.request_histData(company, 2);

    try {
      vds.data_fetch_latch[r].await();
    } catch (InterruptedException e) {

    }
    vds.calc_std_devs(r);
  }
  
}
