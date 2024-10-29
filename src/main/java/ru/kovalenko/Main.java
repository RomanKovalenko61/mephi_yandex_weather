package ru.kovalenko;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {

        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get("example" + File.separator + "weather.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // https://yandex.ru/dev/weather/doc/ru/concepts/forecast-info
        JSONObject jsonObject = new JSONObject(content);
        int now = jsonObject.getInt("now");
        JSONObject fact = jsonObject.getJSONObject("fact");
        int temp = fact.getInt("temp");
        System.out.println(temp);
    }
}
