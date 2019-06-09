package org.ls.docproxy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import sun.misc.ProxyGenerator;

public class Test {

  public static void main(String[] args) {
    IInvestor investor = new Investor("张三");
    InvocationHandler handler = new InvestorIH(investor);
    ClassLoader cl = investor.getClass().getClassLoader();
    IInvestor proxy =
        (IInvestor) Proxy.newProxyInstance(cl, new Class[] { IInvestor.class }, handler);
    proxy.login("zhangsan", "123");
    proxy.buyStock();
    proxy.sellStock();
    System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
    System.out.print("path:" + Proxy.getProxyClass(cl, IInvestor.class));
    //ttt();
  }

  private static void ttt() {
    byte[] bytes = ProxyGenerator
        .generateProxyClass("$Proxy0", new Class<?>[] { IInvestor.class });
    String pathDir = "/Users/wangjingjing/Desktop/Doc/DocProxy/DocProxy";
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
  }
}
