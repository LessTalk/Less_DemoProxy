package org.ls.docproxy;

import java.lang.reflect.Proxy;
import org.ls.docproxy.proxy.IInvestor;
import org.ls.docproxy.proxy.Investor;
import org.ls.docproxy.proxy.InvestorHandler;

/**
 * Author by Less on 2019/6/9.
 */
public class ProxyTest {

  public static void main(String[] args) {
    IInvestor investor = new Investor("张三");
    InvestorHandler handler = new InvestorHandler(investor);
    ClassLoader classLoader = investor.getClass().getClassLoader();
    IInvestor proxy = (IInvestor) Proxy.newProxyInstance(classLoader, new Class[]{IInvestor.class}, handler);
    proxy.login("zhangsan", "123");
    proxy.buyStock();
    proxy.sellStock();
    System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles","true");
    System.out.print("path:"+Proxy.getProxyClass(classLoader, IInvestor.class));
  }

}
