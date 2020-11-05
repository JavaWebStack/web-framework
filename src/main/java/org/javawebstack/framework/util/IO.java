package org.javawebstack.framework.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class IO {

    public static byte[] readFile(File file) throws IOException {
        return readStream(new FileInputStream(file));
    }

    public static byte[] readFile(String fileName) throws IOException {
        return readFile(new File(fileName));
    }

    public static String readTextFile(File file) throws IOException {
        return new String(readFile(file), StandardCharsets.UTF_8);
    }

    public static String readTextFile(String fileName) throws IOException {
        return readTextFile(new File(fileName));
    }

    public static JsonElement readJsonFile(File file) throws IOException {
        return new Gson().fromJson(readTextFile(file), JsonElement.class);
    }

    public static JsonElement readJsonFile(String fileName) throws IOException {
        return readJsonFile(new File(fileName));
    }

    public static Properties readPropertyFile(File file) throws IOException {
        return readPropertyStream(new FileInputStream(file));
    }

    public static Properties readPropertyFile(String fileName) throws IOException {
        return readPropertyFile(new File(fileName));
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

    public static String readTextResource(ClassLoader classLoader, String path) throws IOException {
        return new String(readResource(classLoader, path), StandardCharsets.UTF_8);
    }

    public static String readTextResource(String path) throws IOException {
        return new String(readResource(path), StandardCharsets.UTF_8);
    }

    public static JsonElement readJsonResource(ClassLoader classLoader, String path) throws IOException {
        return new Gson().fromJson(readTextResource(classLoader, path), JsonElement.class);
    }

    public static JsonElement readJsonResource(String path) throws IOException {
        return new Gson().fromJson(readTextResource(path), JsonElement.class);
    }

    public static Properties readPropertyResource(ClassLoader classLoader, String path) throws IOException {
        return readPropertyStream(classLoader.getResourceAsStream(path));
    }

    public static Properties readPropertyResource(String path) throws IOException {
        return readPropertyResource(ClassLoader.getSystemClassLoader(), path);
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

    public static String readTextStream(InputStream stream) throws IOException {
        return new String(readStream(stream), StandardCharsets.UTF_8);
    }

    public static JsonElement readJsonStream(InputStream stream) throws IOException {
        return new Gson().fromJson(readTextStream(stream), JsonElement.class);
    }

    public static Properties readPropertyStream(InputStream stream) throws IOException {
        Properties properties = new Properties();
        properties.load(stream);
        return properties;
    }

    public static void writeFile(File file, byte[] data) throws IOException {
        writeStream(new FileOutputStream(file), data);
    }

    public static void writeFile(String fileName, byte[] data) throws IOException {
        writeFile(new File(fileName), data);
    }

    public static void writeFile(File file, String data) throws IOException {
        writeFile(file, data.getBytes(StandardCharsets.UTF_8));
    }

    public static void writeFile(String fileName, String data) throws IOException {
        writeFile(new File(fileName), data);
    }

    public static void writeFile(File file, JsonElement data) throws IOException {
        writeFile(file, new GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create()
                .toJson(data)
        );
    }

    public static void writeFile(String fileName, JsonElement data) throws IOException {
        writeFile(new File(fileName), data);
    }

    public static void writeFile(File file, Properties properties) throws IOException {
        writeStream(new FileOutputStream(file), properties);
    }

    public static void writeFile(String fileName, Properties properties) throws IOException {
        writeFile(new File(fileName), properties);
    }

    public static void writeStream(OutputStream stream, byte[] data) throws IOException {
        stream.write(data);
        stream.flush();
        stream.close();
    }

    public static void writeStream(OutputStream stream, String data) throws IOException {
        writeStream(stream, data.getBytes(StandardCharsets.UTF_8));
    }

    public static void writeStream(OutputStream stream, JsonElement data) throws IOException {
        writeStream(stream, new GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create()
                .toJson(data)
        );
    }

    public static void writeStream(OutputStream stream, Properties properties) throws IOException {
        properties.store(stream, null);
    }

    public static void connect(InputStream in, OutputStream out) throws IOException {
        int r;
        byte[] buffer = new byte[1024];
        while ((r = in.read(buffer)) != -1)
            out.write(buffer, 0, r);
    }

}
