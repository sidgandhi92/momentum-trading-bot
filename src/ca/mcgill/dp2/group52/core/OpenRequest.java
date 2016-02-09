package ca.mcgill.dp2.group52.core;

import ca.mcgill.dp2.group52.enums.Company;
import com.ib.client.Order;

public class OpenRequest {
  
  public Company company;
  public int long_short_volatility;
  
  public OpenRequest (Company company, int long_short_volatility) {
    this.company = company;
    this.long_short_volatility = long_short_volatility;
  }
  
}
