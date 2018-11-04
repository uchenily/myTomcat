package com.example.test;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TestClient {

    // 模拟浏览器获取服务器资源
    @Test
    public void testClient() throws IOException {
        // 1.建立一个socket对象
        Socket socket = new Socket("www.baidu.com", 80);
        // 2.获取输入流, 输出流对象
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();
        // 3.发起请求
        out.write("GET / HTTP/1.0\n".getBytes()); // http/1.0默认短连接, 避免阻塞 (1.1默认长连接)
        out.write("HOST:www.baidu.com\n".getBytes());
        out.write("\n".getBytes());
        // 4.读取服务端发来的响应
        byte[] buffer = new byte[1024];
        int len;
        StringBuffer sb = new StringBuffer();
        while ((len = in.read(buffer)) != -1) {
            sb.append(new String(buffer, 0, len, "UTF-8"));
        }
        System.out.println(sb);
        // 5.释放资源
        in.close();
        out.close();
        socket.close();
    }
}
