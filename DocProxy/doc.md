# 静态代理

### 前沿

最近的股市很疯狂，
时常1秒钟就能错过一个亿。
但是上班时间，又没法花太多的时间关注股市。
于是决定找个操盘手，让他替我炒股,于是就有了设计模式中的代理模式

```kotlin
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
```

```kotlin
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
```

```kotlin
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
```

```kotlin
/**
 * 场景类
 */
btn_static_proxy.setOnClickListener {
      val proxy = InvestorProxy(
          Investor("张三")
              as IInvestor
      ) as IInvestor
      proxy.login("zhangsan", "123")
      proxy.buyStock()
      proxy.sellStock()
    }
```

看下结果:
```
张三-登录成功
张三-在购买股票
买卖股票的费用:100
张三-在卖股票
买卖股票的费用:100
```

通过上面的演示发现
真正的投资者什么都不需要做就有人帮我们买卖股票了
雇佣别人炒股也得给人家一定的费用这就是静态代理

#### 代理模式的优点

- 职责清晰<br>
  真正的角色只需要关心本身的业务,
  一些附加的任务,可以在代理中实现

- 高扩展性<br>
  真实的角色随时都有可能发生变化,只要实现了他的接口
  代理类不需要有任何修改