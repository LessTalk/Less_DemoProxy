package org.ls.docproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Author by Less on 2019/6/9.
 */
public class InvestorIH implements InvocationHandler {

  /** 被代理者 */
  private Class mCls = null;
  /** 被代理的实例 */
  private Object mObj = null;

  public InvestorIH(Object obj){
    this.mObj = obj;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    //切入点
    Object result = method.invoke(this.mObj, args);
    //切入点
    return result;
  }
}
