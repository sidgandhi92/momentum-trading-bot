import java.util.concurrent.CountDownLatch;
import java.lang.*;

public class VolatilityDataSet {
  public CountDownLatch latch;
  
  public double[][] data;
  public double[] std_dev;
  
  public VotalityDataSet() {
    latch = new CountDownLatch(30);
    raw_data = new double[30][10];
    std_dev = new double[30];
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
  
  
}
