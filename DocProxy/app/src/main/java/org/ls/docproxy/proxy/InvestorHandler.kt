package org.ls.docproxy.proxy

import android.util.Log
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * Author by Less on 2019/6/9.
 */
class InvestorHandler(private val obj: Any) : InvocationHandler {

  override fun invoke(
    proxy: Any?,
    method: Method?,
    args: Array<out Any>?
  ): Any? {
    return method?.invoke(obj, *args.orEmpty())
  }

}