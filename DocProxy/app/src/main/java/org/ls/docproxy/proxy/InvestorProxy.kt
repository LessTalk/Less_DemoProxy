package org.ls.docproxy.proxy

import android.util.Log

/**
 * 操盘手类
 */
class InvestorProxy(private val investor: IInvestor) : IInvestor {

  override fun login(
    user: String,
    password: String
  ) {
    investor.login(user, password)
  }

  override fun buyStock() {
    investor.buyStock()
    fee()
  }

  override fun sellStock() {
    investor.sellStock()
    fee()
  }

  private fun fee() {
    Log.i("InvestorProxy", "买卖股票的费用:100")
  }

}