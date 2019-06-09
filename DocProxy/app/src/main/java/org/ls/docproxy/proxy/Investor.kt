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
    Log.i("Investor", "$name-登录成功")
  }

  override fun buyStock() {
    Log.i("Investor", "$name-在购买股票")
  }

  override fun sellStock() {
    Log.i("Investor", "$name-在卖股票")
  }

}