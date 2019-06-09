package org.ls.docproxy.proxy

import android.util.Log

/**
 * 真正的投资者类
 */
class Investor(private var name: String) : IInvestor {

  override fun login(
    user: String,
    password: String
  ) {
    System.out.println("$name-登录成功")
  }

  override fun buyStock() {
    System.out.println("$name-在购买股票")
  }

  override fun sellStock() {
    System.out.println("$name-在卖股票")
  }

}