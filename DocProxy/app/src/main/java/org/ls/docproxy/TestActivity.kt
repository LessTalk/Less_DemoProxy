package org.ls.docproxy

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.btn_static_proxy
import org.ls.docproxy.staticproxy.IInvestor
import org.ls.docproxy.staticproxy.Investor
import org.ls.docproxy.staticproxy.InvestorProxy

class TestActivity : Activity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    btn_static_proxy.setOnClickListener {
      val proxy = InvestorProxy(
          Investor("张三")
              as IInvestor
      ) as IInvestor
      proxy.login("zhangsan", "123")
      proxy.buyStock()
      proxy.sellStock()
    }

  }
}
