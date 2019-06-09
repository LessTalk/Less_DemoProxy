package org.ls.docproxy.proxy

/**
 * 先定义一个投资者接口
 */
interface IInvestor {

  /**
   * 登录股票账户
   * @param user
   * @param password
   */
  fun login(
    user: String,
    password: String
  )

  /**
   * 买股票
   */
  fun buyStock()

  /**
   * 卖股票
   */
  fun sellStock()

}