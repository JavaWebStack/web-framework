package org.javawebstack.framework.util;

import java.io.*;

public class IOHelper {

    public static byte[] readFile(File file) throws IOException {
        return readStream(new FileInputStream(file));
    }

    public static byte[] readResource(ClassLoader classLoader, String path) throws IOException {
        InputStream is = classLoader.getResourceAsStream(path);
        if(is == null)
            throw new FileNotFoundException("Resource '"+path+"' not found!");
        return readStream(is);
    }

    public static byte[] readResource(String path) throws IOException {
        return readResource(ClassLoader.getSystemClassLoader(), path);
    }

    public static byte[] readStream(InputStream stream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int r;
        while((r = stream.read(buffer)) != -1)
            baos.write(buffer, 0, r);
        stream.close();
        return baos.toByteArray();
    }

    public static void writeFile(File file, byte[] data) throws IOException {
        writeStream(new FileOutputStream(file), data);
    }

    public static void writeStream(OutputStream stream, byte[] data) throws IOException {
        stream.write(data);
        stream.flush();
        stream.close();
    }

}
