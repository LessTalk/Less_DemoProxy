package org.ls.docproxy

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.btn_dynamic_proxy
import kotlinx.android.synthetic.main.activity_main.btn_static_proxy
import org.ls.docproxy.proxy.IInvestor
import org.ls.docproxy.proxy.Investor
import org.ls.docproxy.proxy.InvestorHandler
import org.ls.docproxy.proxy.InvestorProxy
import java.lang.reflect.Proxy

class TestActivity : Activity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    System.getProperties()["sun.misc.ProxyGenerator.saveGeneratedFiles"] = "true"

    btn_static_proxy.setOnClickListener {
      val iInvestor = Investor("张三") as IInvestor
      val proxy = InvestorProxy(iInvestor) as IInvestor
      deal(proxy)
    }

    btn_dynamic_proxy.setOnClickListener {
      val iInvestor = Investor("张三") as IInvestor
      val handler = InvestorHandler(iInvestor)
      val classLoader = iInvestor.javaClass.classLoader
      val interfaces = arrayOf(IInvestor::class.java)
      val proxy = Proxy.newProxyInstance(classLoader, interfaces, handler) as IInvestor
      deal(proxy)
    }
  }

  private fun deal(proxy: IInvestor) {
    proxy.login("zhangsan", "123")
    proxy.buyStock()
    proxy.sellStock()
  }
}
