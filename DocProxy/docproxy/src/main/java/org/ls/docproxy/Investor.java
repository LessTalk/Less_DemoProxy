package org.ls.docproxy;

/**
 * Author by Less on 2019/6/9.
 */
public class Investor implements IInvestor {

  private String mName;

  public Investor(String name){
    this.mName = name;
  }

  @Override
  public void login(String user, String password) {
    System.out.println(this.mName + "登录成功！");
  }

  @Override
  public void buyStock() {
    System.out.println(this.mName + "在买股票！");
  }

  @Override
  public void sellStock() {
    System.out.println(this.mName + "在卖股票！");
  }
}
