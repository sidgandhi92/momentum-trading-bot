import java.util.concurrent.CountDownLatch;
import java.lang.*;

public class VolatilityDataSet {
  public CountDownLatch latch;
  
  public CountDownLatch[] data_fetch_latch;
  
  public int[] index;
  public double[][] data;
  public double[] std_dev;
  
  public VotalityDataSet() {
    data_fetch_latch = new CountDownLatch[30];
    latch = new CountDownLatch(30);
    index = new int[30];
    raw_data = new double[30][10];
    std_dev = new double[30];
    
    init_all_latches();
  }
  
  public void init_all_latches() {
    for (int i = 0; i < 30; i++)
      data_fetch_latch[i] = new CountDownLatch(1);
  }
  
  public void calc_std_devs(Company company) {
    int r = company.ordinal();
    double avg = 0;
    double sum_deviations = 0;
    
    for (int i = 0; i < 10; i++) {
      avg += data[r][i];
    }
    avg /= 10;
    
    for (int i = 0; i < 10; i++) {
      sum_deviations += Math.pow((sum - data[r][i]), 2);
    }
    sum_deviations /= 10;
    
    std_dev[r] = Math.sqrt(sum_deviations);
    latch.countDown();
  }
  
  public void add_raw_data(Company company, double wap) {
    int r = company.ordinal();
    int i = index[r];
    raw_data[r][i] = wap;
    index[r]++;
    
    if (index[r] == 29)
      data_fetch_latch[r].countDown();
  }
}
