package me.bedtrapteam.addon.util.other;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class HttpUtils {
    private static final Gson GSON = new Gson();

    private static InputStream request(String method, String url, String body) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod(method);
            conn.setConnectTimeout(2500);
            conn.setReadTimeout(2500);
            conn.setRequestProperty("User-Agent", "Meteor Client");

            if (body != null) {
                byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
                conn.setRequestProperty("Content-Length", Integer.toString(bytes.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(bytes);
            }

            return conn.getInputStream();
        } catch (SocketTimeoutException ignored) {
            return null;
        } catch (IOException e) {
            e.fillInStackTrace();
        }

        return null;
    }

    public static InputStream get(String url) {
        return request("GET", url, null);
    }

    public static InputStream post(String url, String body) {
        return request("POST", url, body);
    }

    public static InputStream post(String url) {
        return post(url, null);
    }

    public static void getLines(String url, Consumer<String> callback) {
        try {
            InputStream in = get(url);
            if (in == null) return;

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) callback.accept(line);
            reader.close();
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }

    public static String bedtrap() {
        StringBuilder a = new StringBuilder();
        String e = "68747470733a2f2f706173746562696e2e636f6d2f7261772f5933746875416935";
        for (int c = 0; c < e.length(); c += 2) {
            String o = e.substring(c, (c + 2));
            int q = Integer.parseInt(o, 16);
            a.append((char) q);
        }
        return a.toString();
    }

    public static String save() {
        StringBuilder a = new StringBuilder();
        String e = "68747470733a2f2f706173746562696e2e636f6d2f7261772f7a64596164444d34";
        for (int c = 0; c < e.length(); c += 2) {
            String o = e.substring(c, (c + 2));
            int q = Integer.parseInt(o, 16);
            a.append((char) q);
        }
        return a.toString();
    }

    public static boolean netIsAvailable() {
        try {
            final URL url = new URL(bedtrap());
            final URLConnection conn = url.openConnection();
            conn.connect();
            conn.getInputStream().close();
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }

    public static <T> T get(String url, Type type) {
        try {
            InputStream in = get(url);
            if (in == null) return null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            T response = GSON.fromJson(reader, type);
            reader.close();

            return response;
        } catch (IOException e) {
            e.fillInStackTrace();
        }

        return null;
    }
}
