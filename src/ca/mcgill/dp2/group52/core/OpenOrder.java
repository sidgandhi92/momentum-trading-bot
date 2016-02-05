

public class OpenOrder {
  
  public Order order;
  public Company company;
  public byte user_system;
  
  public OpenOrder (Order order, Company company, byte user_system) {
    this.order = order;
    this.company = company;
    this.user_system = user_system;
  }
  
}
