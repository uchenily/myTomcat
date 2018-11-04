package com.example.mytomcat_v2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/*
版本二, 在版本一的基础上, 添加实现发送动态资源(Servlet)
 */
public class MyTomcat {

    // 存放服务端WebContent的绝对路径
    private static final String WEB_BOOT = System.getProperty("user.dir") + "\\" + "WebContent";

    // 静态map, 存储conf.properties中的信息
    private static Map<String, String> map = new HashMap<>();

    // 在服务器启动之前将配置信息加载到map
    static {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(WEB_BOOT + "\\conf.properties"));
            Set set = prop.keySet();
            for (Object o : set) {
                map.put((String) o, prop.getProperty((String) o));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        // System.out.println(WEB_BOOT);
        // System.out.println(map);
        ServerSocket serverSocket = new ServerSocket(8080);
        while (true) {
            // 获取到客户端对应的socket
            Socket socket = serverSocket.accept();
            // 获取输入流对象和输出流对象
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            // 获取资源路径
            String url = MyTomcat.getRequestPath(in);
            // 发送响应
            if (url.endsWith(".html")) {
                MyTomcat.sendStaticResource(out, url);
            } else {
                MyTomcat.sendDynamicResource(in, out, url);
            }
            // 释放资源
            out.close();
            in.close();
            socket.close();
        }
    }

    // 发送动态资源
    private static void sendDynamicResource(InputStream in, OutputStream out, String url) throws Exception {
        // 判断map中是否存在一个key, 和本次请求路径一致
        // 如果存在, 则将对应的value通过反射把Servlet加载到内存
        // 执行init(), Service()
        if (map.containsKey(url)) {
            out.write("HTTP/1.0 200 OK\n".getBytes());
            out.write("Server:MyTomcat\n".getBytes());
            out.write("Content-Type:text/html;charset=utf-8\n".getBytes());
            out.write("\n".getBytes());

            String className = map.get(url);
            Class<?> clazz = Class.forName(className);
            Servlet servlet = (Servlet) clazz.newInstance();
            servlet.init();
            servlet.service(in, out);
        } else {
            out.write("HTTP/1.0 404 not found\n".getBytes());
            out.write("Server:MyTomcat\n".getBytes());
            out.write("Content-Type:text/html;charset=utf-8\n".getBytes());
            out.write("\n".getBytes());

            out.write("<strong>Error</strong>: dynamic resource not found\n".getBytes());
        }
    }

    // 发送静态资源
    private static void sendStaticResource(OutputStream out, String url) throws IOException {
        byte[] buffer = new byte[1024];
        FileInputStream fin = null;
        File file = new File(WEB_BOOT, url);
        if (file.exists()) {
            out.write("HTTP/1.0 200 OK\n".getBytes());
            out.write("Server:MyTomcat\n".getBytes());
            out.write("Content-Type:text/html;charset=utf-8\n".getBytes());
            out.write("\n".getBytes());
            fin = new FileInputStream(file);
            int len;
            while ((len = fin.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } else {
            out.write("HTTP/1.0 404 not found\n".getBytes());
            out.write("Server:MyTomcat\n".getBytes());
            out.write("Content-Type:text/html;charset=utf-8\n".getBytes());
            out.write("\n".getBytes());

            out.write("<strong>Error</strong>: file not found\n".getBytes());
        }
        if (fin != null) {
            fin.close();
        }
    }

    // 通过输入流对象获取请求路径
    private static String getRequestPath(InputStream in) throws IOException {
        byte[] buffer = new byte[1024]; // 这里假设request长度不超过1024(其实只需要获取第一行中的资源数据)
        StringBuffer sb = new StringBuffer();
        int len = in.read(buffer);
        String requestStr = new String(buffer, 0, len);
        System.out.println(requestStr);
        int begin = requestStr.indexOf(" ");
        int end = requestStr.indexOf(" ", begin + 1);
        String requestPath = requestStr.substring(begin + 2, end);
        // System.out.println("requestPath = " + requestPath);
        return requestPath;
    }

}
