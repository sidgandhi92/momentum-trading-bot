package ca.mcgill.dp2.group52.runnables;

import ca.mcgill.dp2.group52.core.CoreScheduler;
import ca.mcgill.dp2.group52.core.DataSet;
import ca.mcgill.dp2.group52.core.Network;
import ca.mcgill.dp2.group52.enums.BuySell;
import ca.mcgill.dp2.group52.enums.Company;

public class FindMaxTask extends RecursiveTask<Double> {
  public volatile double[] std_devs;
  private int start, end;
  
  private final int THRESHOLD = 15;
  
  public FindMinTask(double[] arr, int start, int end) {
    this.std_devs = arr;
    this.start = start;
    this.end = end;
  }
  
  @Override
  protected Integer compute() {
    if (end - start <= THRESHOLD) {
      return seq_compute();
    } else {
      int mid = start + (end - start) / 2;
      
      FindMaxTask left = new FindMaxTask(std_devs, start, mid);
      FindMaxTask right = new FindMaxTask(std_devs, mid, end);
      
      invokeAll(left, right)
      
      int l_index = left.join();
      int r_index = right.join();
      
      boolean b = std_devs[l_index] > std_devs[r_index] ? true : false;
      int final = b ? l_index : r_index;
      
      if (start == 0 && end == std_devs.length - 1) {
        if (b)
          std_devs[l_index] = -1;
        else
          std_devs[r_index] = -1;
      }
      
      return final;
    }
  }
  
  protected Integer seq_compute() {
    double max = -1;
    int ind = start;
    
    for (int i = start; i < end; i++) {
      if (std_devs[i] > max) {
        ind = i;
        max = std_devs[i];
      }
    }
    
    return ind;
  }
}
