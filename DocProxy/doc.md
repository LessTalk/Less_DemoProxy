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
  
  
# 动态代理

在静态代理中,我们需要为每一个被代理类生成一个代理类(也就是 操盘手).在动态代理中,这个类是可以自动生成的.
另外目前很流行的一个名词叫做切面编程(AOP),其核心就是用了动态代理机制.下面还是以炒股来看看动态代理的实现
接口类和真实类还是使用上面的代码，然后再定义一个InvestorIH，实现InvocationHandler接口，如下
```java
public class InvestorIH implements InvocationHandler {

    /**
     * 被代理的实例
     */
    private Object mObj;

    public InvestorIH(Object obj) {
        this.mObj = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(this.mObj, args);
    }
}
```
动态代理是根据被代理的接口生成所有的方法,但是默认是没有逻辑的,返回值都是空,所有的方法都由InvocationHandler接管处理 下面看下场景类
```java
IInvestor investor = new Investor("张三");
InvocationHandler handler = new InvestorIH(investor);
ClassLoader cl = investor.getClass().getClassLoader();
IInvestor proxy =
                (IInvestor) Proxy.newProxyInstance(cl, new Class[] { IInvestor.class }, handler);
proxy.login("zhangsan", "123");
proxy.buyStock();
proxy.sellStock();
```
从上面的代理可以发现,Proxy.newProxyInstance 会给我们生成一个代理类.那么代理类中的逻辑需要我们InvestorIH中来实现.这就是动态代理的jdk实现,因为我们使用了jdk中的api生成代理类.

# JDK中动态代理分析

在上面的内容中,我们了解了动态代理的基础使用
