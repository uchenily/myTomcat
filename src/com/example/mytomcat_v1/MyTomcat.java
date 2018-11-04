package com.example.mytomcat_v1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/*
版本一, 实现的功能只是简单的向客户端发送静态资源
 */
public class MyTomcat {

    // 存放服务端WebContent的绝对路径
    private static final String WEB_BOOT = System.getProperty("user.dir") + "\\" + "WebContent";

    public static void main(String[] args) throws IOException {
        // System.out.println(WEB_BOOT);
        ServerSocket serverSocket = new ServerSocket(8080);
        while(true) {
            // 获取到客户端对应的socket
            Socket socket = serverSocket.accept();
            // 获取输入流对象和输出流对象
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            // 获取资源路径
            String url = MyTomcat.getRequestPath(in);
            // 发送响应
            MyTomcat.sendResponse(out, url);
            // 释放资源
            out.close();
            in.close();
            socket.close();
        }
    }

    // 通过输出流对象发送响应
    private static void sendResponse(OutputStream out, String url) throws IOException {
        byte[] buffer = new byte[1024];
        FileInputStream fin = null;
        File file = new File(WEB_BOOT, url);
        if(file.exists()) {
            out.write("HTTP/1.0 200 OK\n".getBytes());
            out.write("Server:MyTomcat\n".getBytes());
            out.write("Content-Type:text/html;charset=utf-8\n".getBytes());
            out.write("\n".getBytes());
            fin = new FileInputStream(file);
            int len;
            while((len = fin.read(buffer))!=-1) {
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
        int end = requestStr.indexOf(" ", begin+1);
        String requestPath = requestStr.substring(begin+2, end);
        // System.out.println("requestPath = " + requestPath);
        return requestPath;
    }

}
