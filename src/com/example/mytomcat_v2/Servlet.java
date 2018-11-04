package com.example.mytomcat_v2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Servlet {
    public void init();
    public void service(InputStream in, OutputStream out) throws IOException;
    public void destroy();
}
