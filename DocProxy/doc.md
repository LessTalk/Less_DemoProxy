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
public interface IInvestor {

  /**
   * 登录股票账户
   * @param user
   * @param password
   */
  void login(String user, String password);

  /**
   * 买股票
   */
  void buyStock();

  /**
   * 卖股票
   */
  void sellStock();
}

```

```kotlin
/**
 * 真正的投资者类
 */
public class Investor implements IInvestor {

  private String mName;

  public Investor(String name){
    this.mName = name;
  }

  @Override
  public void login(String user, String password) {
    System.out.println(this.mName + "登录成功！");
  }

  @Override
  public void buyStock() {
    System.out.println(this.mName + "在买股票！");
  }

  @Override
  public void sellStock() {
    System.out.println(this.mName + "在卖股票！");
  }
}
```

```kotlin
/**
 * 操盘手类
 */
public class InvestorProxy implements IInvestor {


    private IInvestor mInvestor;

    public InvestorProxy(IInvestor investor){
        this.mInvestor = investor;
    }

    @Override
    public void login(String user, String password) {
        mInvestor.login(user, password);
    }

    @Override
    public void buyStock() {
        mInvestor.buyStock();
        fee();
    }

    @Override
    public void sellStock() {
        mInvestor.sellStock();
        fee();
    }

    public void fee(){
        System.out.println("买卖股票费用： 100元");
    }
}
```

```kotlin
/**
 * 场景类
 */
//操盘手投资
IInvestor investor = new Investor("张三");
IInvestor proxy = new InvestorProxy(investor);
proxy.login("zhangsan", "123");
proxy.buyStock();
proxy.sellStock();
```

看下结果:
```
张三登录成功！
张三在买股票！
买卖股票费用： 100元
张三在卖股票！
买卖股票费用： 100元
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
