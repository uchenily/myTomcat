package com.example.test;

import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer {

    // 模拟服务端向客户端发出响应
    @Test
    public void testServer() throws IOException {
        // 1.创建ServerSocket对象, 监听本机8080端口
        ServerSocket serverSocket = new ServerSocket(8080);
        while (true) {
            // 2.等待客户端发出请求(需要获取来自客户端的Socket对象)
            Socket socket = serverSocket.accept();
            // 3.通过获取的Socket对象获取输出流对象
            OutputStream out = socket.getOutputStream();
            // 4.通过输出流对象将http响应部分发送到客户端
            out.write("HTTP/1.0 200 OK\n".getBytes());
            out.write("Content-Type:text/html;charset=utf-8\n".getBytes());
            out.write("Server:MyServer\n".getBytes());
            out.write("\n".getBytes());

            StringBuffer sb = new StringBuffer();
            sb.append("<html>");
            sb.append("<head><title>这是标题</title></head>");
            sb.append("<body><h1>这是H1</h1></body>");
            sb.append("</html>");
            out.write(sb.toString().getBytes());
            out.flush();
            // 5.释放资源
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        }
    }
}
