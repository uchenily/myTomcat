package com.example.mytomcat_v2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AServlet implements Servlet {
    @Override
    public void init() {
        System.out.println("AServlet run init()...");
    }

    @Override
    public void service(InputStream in, OutputStream out) throws IOException {
        System.out.println("AServlet run service()...");
        out.write("AServlet run service()...".getBytes());
        out.flush();
    }

    @Override
    public void destroy() {
        System.out.println("AServlet run destroy()...");
    }
}
