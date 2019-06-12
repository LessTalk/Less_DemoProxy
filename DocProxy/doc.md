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

在上面的内容中,我们了解了动态代理的基础使用,下面我们分析下具体的源码
```java
ublic static Object newProxyInstance(ClassLoader loader,
                                          Class<?>[] interfaces,
                                          InvocationHandler h)

```
这个方法需要传入3个参数，先看看他们的作用
- loader 一个类加载器
- 一个Interface对象的数组，表示的是我将要给我需要代理的对象提供一组什么接口
- 上文中多次提到的handler

这里有一个疑问 就是我们的操盘手类究竟是如何生成的 我们继续看下
```java
@CallerSensitive
   public static Object newProxyInstance(ClassLoader loader,
                                         Class<?>[] interfaces,
                                         InvocationHandler h)
       throws IllegalArgumentException
   {

       Objects.requireNonNull(h);

       //克隆要被代理的接口
       final Class<?>[] intfs = interfaces.clone();
       final SecurityManager sm = System.getSecurityManager();
       if (sm != null) {
           checkProxyAccess(Reflection.getCallerClass(), loader, intfs);
       }

       /*
        * Look up or generate the designated proxy class.
        */
        //查找或者生成特定的代理类 class
       Class<?> cl = getProxyClass0(loader, intfs);

       /*
        * Invoke its constructor with the designated invocation handler.
        */
       try {
           if (sm != null) {
               checkNewProxyPermission(Reflection.getCallerClass(), cl);
           }
           //获取参数类型是InvocationHandler.class的代理类构造器
           final Constructor<?> cons = cl.getConstructor(constructorParams);
           final InvocationHandler ih = h;
           if (!Modifier.isPublic(cl.getModifiers())) {
               AccessController.doPrivileged(new PrivilegedAction<Void>() {
                   public Void run() {
                       cons.setAccessible(true);
                       return null;
                   }
               });
           }
            //传入InvocationHandler实例去构造一个代理类的实例
           return cons.newInstance(new Object[]{h});
       } catch (IllegalAccessException|InstantiationException e) {
           throw new InternalError(e.toString(), e);
       } catch (InvocationTargetException e) {
           Throwable t = e.getCause();
           if (t instanceof RuntimeException) {
               throw (RuntimeException) t;
           } else {
               throw new InternalError(t.toString(), t);
           }
       } catch (NoSuchMethodException e) {
           throw new InternalError(e.toString(), e);
       }
   }
```

所以获取代理类的实例，重点到了下面的这行代码中

```java
//查找或者生成特定的代理类 class
Class<?> cl = getProxyClass0(loader, intfs);
```
看下getProxyClass0
```java
/**
   * Generate a proxy class.  Must call the checkProxyAccess method
   * to perform permission checks before calling this.
   */
  private static Class<?> getProxyClass0(ClassLoader loader,
                                         Class<?>... interfaces) {
      if (interfaces.length > 65535) {
          throw new IllegalArgumentException("interface limit exceeded");
      }

      // If the proxy class defined by the given loader implementing
      // the given interfaces exists, this will simply return the cached copy;
      // otherwise, it will create the proxy class via the ProxyClassFactory

      //如果缓存中有，则使用缓存，否则通过ProxyClassFactory创建
      return proxyClassCache.get(loader, interfaces);
  }
```
再来看get 方法
```java
public V get(K var1, P var2) {
        Objects.requireNonNull(var2);
        this.expungeStaleEntries();
        Object var3 = WeakCache.CacheKey.valueOf(var1, this.refQueue);
        Object var4 = (ConcurrentMap)this.map.get(var3);
        if (var4 == null) {
            ConcurrentMap var5 = (ConcurrentMap)this.map.putIfAbsent(var3, var4 = new ConcurrentHashMap());
            if (var5 != null) {
                var4 = var5;
            }
        }
        //先忽略缓存.重点看下这里 找不到就apply生成一个
        Object var9 = Objects.requireNonNull(this.subKeyFactory.apply(var1, var2));
        Object var6 = (Supplier)((ConcurrentMap)var4).get(var9);
        WeakCache.Factory var7 = null;

        while(true) {
            if (var6 != null) {
                Object var8 = ((Supplier)var6).get();
                if (var8 != null) {
                    return var8;
                }
            }

            if (var7 == null) {
                var7 = new WeakCache.Factory(var1, var2, var9, (ConcurrentMap)var4);
            }

            if (var6 == null) {
                var6 = (Supplier)((ConcurrentMap)var4).putIfAbsent(var9, var7);
                if (var6 == null) {
                    var6 = var7;
                }
            } else if (((ConcurrentMap)var4).replace(var9, var6, var7)) {
                var6 = var7;
            } else {
                var6 = (Supplier)((ConcurrentMap)var4).get(var9);
            }
        }
    }
```
在看apply
```java
public Class<?> apply(ClassLoader var1, Class<?>[] var2) {
            IdentityHashMap var3 = new IdentityHashMap(var2.length);
            Class[] var4 = var2;
            int var5 = var2.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                Class var7 = var4[var6];
                Class var8 = null;

                try {
                    var8 = Class.forName(var7.getName(), false, var1);
                } catch (ClassNotFoundException var15) {
                }

                if (var8 != var7) {
                    throw new IllegalArgumentException(var7 + " is not visible from class loader");
                }

                if (!var8.isInterface()) {
                    throw new IllegalArgumentException(var8.getName() + " is not an interface");
                }

                if (var3.put(var8, Boolean.TRUE) != null) {
                    throw new IllegalArgumentException("repeated interface: " + var8.getName());
                }
            }

            String var16 = null;
            byte var17 = 17;
            Class[] var18 = var2;
            int var20 = var2.length;

            for(int var21 = 0; var21 < var20; ++var21) {
                Class var9 = var18[var21];
                int var10 = var9.getModifiers();
                if (!Modifier.isPublic(var10)) {
                    var17 = 16;
                    String var11 = var9.getName();
                    int var12 = var11.lastIndexOf(46);
                    String var13 = var12 == -1 ? "" : var11.substring(0, var12 + 1);
                    if (var16 == null) {
                        var16 = var13;
                    } else if (!var13.equals(var16)) {
                        throw new IllegalArgumentException("non-public interfaces from different packages");
                    }
                }
            }

            if (var16 == null) {
                var16 = "com.sun.proxy.";
            }

            long var19 = nextUniqueNumber.getAndIncrement();
            String var23 = var16 + "$Proxy" + var19;
            byte[] var22 = ProxyGenerator.generateProxyClass(var23, var2, var17);

            try {
                return Proxy.defineClass0(var1, var23, var22, 0, var22.length);
            } catch (ClassFormatError var14) {
                throw new IllegalArgumentException(var14.toString());
            }
        }
    }
```
通过上面的逻辑 我们看到 通过ProxyGenerator.generateProxyClass（）生成了代理类的.class文件
```java
public static byte[] generateProxyClass(final String var0, Class<?>[] var1, int var2) {
        ProxyGenerator var3 = new ProxyGenerator(var0, var1, var2);
        final byte[] var4 = var3.generateClassFile();
        if (saveGeneratedFiles) {
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
                public Void run() {
                    try {
                        int var1 = var0.lastIndexOf(46);
                        Path var2;
                        if (var1 > 0) {
                            Path var3 = Paths.get(var0.substring(0, var1).replace('.', File.separatorChar));
                            Files.createDirectories(var3);
                            var2 = var3.resolve(var0.substring(var1 + 1, var0.length()) + ".class");
                        } else {
                            var2 = Paths.get(var0 + ".class");
                        }

                        Files.write(var2, var4, new OpenOption[0]);
                        return null;
                    } catch (IOException var4x) {
                        throw new InternalError("I/O exception saving generated file: " + var4x);
                    }
                }
            });
        }

        return var4;
    }
```

我们再来看下generateClassFile()
```java
private byte[] generateClassFile() {
        this.addProxyMethod(hashCodeMethod, Object.class);
        this.addProxyMethod(equalsMethod, Object.class);
        this.addProxyMethod(toStringMethod, Object.class);
        Class[] var1 = this.interfaces;
        int var2 = var1.length;

        int var3;
        Class var4;
        for(var3 = 0; var3 < var2; ++var3) {
            var4 = var1[var3];
            Method[] var5 = var4.getMethods();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                Method var8 = var5[var7];
                this.addProxyMethod(var8, var4);
            }
        }

        Iterator var11 = this.proxyMethods.values().iterator();

        List var12;
        while(var11.hasNext()) {
            var12 = (List)var11.next();
            checkReturnTypes(var12);
        }

        Iterator var15;
        try {
            this.methods.add(this.generateConstructor());
            var11 = this.proxyMethods.values().iterator();

            while(var11.hasNext()) {
                var12 = (List)var11.next();
                var15 = var12.iterator();

                while(var15.hasNext()) {
                    ProxyGenerator.ProxyMethod var16 = (ProxyGenerator.ProxyMethod)var15.next();
                    this.fields.add(new ProxyGenerator.FieldInfo(var16.methodFieldName, "Ljava/lang/reflect/Method;", 10));
                    this.methods.add(var16.generateMethod());
                }
            }

            this.methods.add(this.generateStaticInitializer());
        } catch (IOException var10) {
            throw new InternalError("unexpected I/O Exception", var10);
        }

        if (this.methods.size() > 65535) {
            throw new IllegalArgumentException("method limit exceeded");
        } else if (this.fields.size() > 65535) {
            throw new IllegalArgumentException("field limit exceeded");
        } else {
            this.cp.getClass(dotToSlash(this.className));
            this.cp.getClass("java/lang/reflect/Proxy");
            var1 = this.interfaces;
            var2 = var1.length;

            for(var3 = 0; var3 < var2; ++var3) {
                var4 = var1[var3];
                this.cp.getClass(dotToSlash(var4.getName()));
            }

            this.cp.setReadOnly();
            ByteArrayOutputStream var13 = new ByteArrayOutputStream();
            DataOutputStream var14 = new DataOutputStream(var13);

            try {
                var14.writeInt(-889275714);
                var14.writeShort(0);
                var14.writeShort(49);
                this.cp.write(var14);
                var14.writeShort(this.accessFlags);
                var14.writeShort(this.cp.getClass(dotToSlash(this.className)));
                var14.writeShort(this.cp.getClass("java/lang/reflect/Proxy"));
                var14.writeShort(this.interfaces.length);
                Class[] var17 = this.interfaces;
                int var18 = var17.length;

                for(int var19 = 0; var19 < var18; ++var19) {
                    Class var22 = var17[var19];
                    var14.writeShort(this.cp.getClass(dotToSlash(var22.getName())));
                }

                var14.writeShort(this.fields.size());
                var15 = this.fields.iterator();

                while(var15.hasNext()) {
                    ProxyGenerator.FieldInfo var20 = (ProxyGenerator.FieldInfo)var15.next();
                    var20.write(var14);
                }

                var14.writeShort(this.methods.size());
                var15 = this.methods.iterator();

                while(var15.hasNext()) {
                    ProxyGenerator.MethodInfo var21 = (ProxyGenerator.MethodInfo)var15.next();
                    var21.write(var14);
                }

                var14.writeShort(0);
                return var13.toByteArray();
            } catch (IOException var9) {
                throw new InternalError("unexpected I/O Exception", var9);
            }
        }
    }
```


这里要稍微解释一下 Java程序的执行只依赖于class文件,和java文件是没有关系的,这个class文件描述了一个类的新,当我们需要使用到一个类时,java虚拟机就会提前去加载这个类的class文件并进行初始化和相关的检验工作,Java虚拟机能够保证在你使用到这个类之前就会完成这些工作，我们只需要安心的去使用它就好了，而不必关心Java虚拟机是怎样加载它的。当然，Class文件并不一定非得通过编译Java文件而来，你甚至可以直接通过文本编辑器来编写Class文件。在这里，JDK动态代理就是通过程序来动态生成Class文件的。到这里我们就知道动态代理的这个代理类是怎么生成的了。
接下来我们手动操作下
```java
byte[] bytes = ProxyGenerator
                .generateProxyClass("$Proxy0", new Class<?>[] { IInvestor.class });
        String pathDir = "/Users/less/Desktop/DocLess/DocProxy/DocProxy";
        String path = "\\$Proxy0.class";
        File f = new File(pathDir);
        if (!f.exists()) {
            f.mkdir();
        }
        path = f.getAbsolutePath() + path;
        f = new File(path);
        if (f.exists()) {
            f.delete();
        }
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(bytes, 0, bytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
```
将proxy类输出到项目的根目录
```java
package com.sun.proxy;

import com.cfp.pattern.proxy.dynamic.IInvestor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;

public final class $Proxy0 extends Proxy implements IInvestor {
    private static Method m1;
    private static Method m5;
    private static Method m4;
    private static Method m2;
    private static Method m3;
    private static Method m0;

    public $Proxy0(InvocationHandler var1) throws  {
        super(var1);
    }

    public final boolean equals(Object var1) throws  {
        try {
            return ((Boolean)super.h.invoke(this, m1, new Object[]{var1})).booleanValue();
        } catch (RuntimeException | Error var3) {
            throw var3;
        } catch (Throwable var4) {
            throw new UndeclaredThrowableException(var4);
        }
    }

    public final void sellStock() throws  {
        try {
            super.h.invoke(this, m5, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final void buyStock() throws  {
        try {
            super.h.invoke(this, m4, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final String toString() throws  {
        try {
            return (String)super.h.invoke(this, m2, (Object[])null);
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    public final int login(String var1, String var2) throws  {
        try {
            return ((Integer)super.h.invoke(this, m3, new Object[]{var1, var2})).intValue();
        } catch (RuntimeException | Error var4) {
            throw var4;
        } catch (Throwable var5) {
            throw new UndeclaredThrowableException(var5);
        }
    }

    public final int hashCode() throws  {
        try {
            return ((Integer)super.h.invoke(this, m0, (Object[])null)).intValue();
        } catch (RuntimeException | Error var2) {
            throw var2;
        } catch (Throwable var3) {
            throw new UndeclaredThrowableException(var3);
        }
    }

    static {
        try {
            m1 = Class.forName("java.lang.Object").getMethod("equals", new Class[]{Class.forName("java.lang.Object")});
            m5 = Class.forName("com.cfp.pattern.proxy.dynamic.IInvestor").getMethod("sellStock", new Class[0]);
            m4 = Class.forName("com.cfp.pattern.proxy.dynamic.IInvestor").getMethod("buyStock", new Class[0]);
            m2 = Class.forName("java.lang.Object").getMethod("toString", new Class[0]);
            m3 = Class.forName("com.cfp.pattern.proxy.dynamic.IInvestor").getMethod("login", new Class[]{Class.forName("java.lang.String"), Class.forName("java.lang.String")});
            m0 = Class.forName("java.lang.Object").getMethod("hashCode", new Class[0]);
        } catch (NoSuchMethodException var2) {
            throw new NoSuchMethodError(var2.getMessage());
        } catch (ClassNotFoundException var3) {
            throw new NoClassDefFoundError(var3.getMessage());
        }
    }
}
```

