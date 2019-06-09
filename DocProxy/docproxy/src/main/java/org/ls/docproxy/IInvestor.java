package org.ls.docproxy;

/**
 * Author by Less on 2019/6/9.
 */
public interface IInvestor {

  /**
   * 登录股票账户
   * @param user
   * @param password
   */
  void login(String user, String password);

  /**
   * 买股票
   */
  void buyStock();

  /**
   * 卖股票
   */
  void sellStock();
}
