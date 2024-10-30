package ru.kovalenko;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {
    public static void main(String[] args) {
        double lat = 47.235714;
        double lon = 39.701504;
        String url = String.format("https://api.weather.yandex.ru/v2/forecast?lat=%s&lon=%s", lat, lon);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-Yandex-Weather-Key", System.getenv("X-Yandex-Weather-Key"))
                .GET()
                .build();

        System.out.println("Request " + request);

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());

            if (response.statusCode() == 200) {
                var weatherKeeper = parseJSON(response);
                System.out.println(weatherKeeper);
                printInfoAboutWeather(weatherKeeper, 3);
            }

        } catch (Exception e) {
            System.err.println("Error making HTTP request: " + e.getMessage());
        }
    }

    static public WeatherKeeper parseJSON(HttpResponse<String> response) {
        JSONObject jsonObject = new JSONObject(response.body());
        double latitude = jsonObject.getJSONObject("info").getDouble("lat");
        double longitude = jsonObject.getJSONObject("info").getDouble("lon");
        WeatherKeeper weatherKeeper = new WeatherKeeper(latitude, longitude);
        weatherKeeper.setTempNow(jsonObject.getJSONObject("fact").getInt("temp"));
        JSONArray forecasts = jsonObject.getJSONArray("forecasts");
        for (int i = 0; i < forecasts.length(); i++) {
            weatherKeeper.addAVGTempInMorning(forecasts.getJSONObject(i).getJSONObject("parts").getJSONObject("morning").getInt("temp_avg"));
            weatherKeeper.addAVGTempInDays(forecasts.getJSONObject(i).getJSONObject("parts").getJSONObject("day").getInt("temp_avg"));
            weatherKeeper.addAVGTempInEvening(forecasts.getJSONObject(i).getJSONObject("parts").getJSONObject("evening").getInt("temp_avg"));
            weatherKeeper.addAVGTempInNight(forecasts.getJSONObject(i).getJSONObject("parts").getJSONObject("night").getInt("temp_avg"));
        }
        return weatherKeeper;
    }

    static public void printInfoAboutWeather(WeatherKeeper w, int limit) {
        System.out.println("------------ ПОГОДА ------------");
        System.out.printf("Координаты для отображения данных о погоде. Широта: %s Долгота: %s %n", w.getLat(), w.getLon());
        System.out.printf("Температура в данный момент: %s %n", w.getTempNow());
        System.out.printf("Расчетные данные средней температуры на ближайшие %d дней %n", limit);
        System.out.println("Средняя температура по утрам " + w.getAvgTempForPart(WeatherKeeper.Part.MORNING, limit));
        System.out.println("Средняя температура днем " + w.getAvgTempForPart(WeatherKeeper.Part.DAY, limit));
        System.out.println("Средняя температура по вечерам " + w.getAvgTempForPart(WeatherKeeper.Part.EVENING, limit));
        System.out.println("Средняя температура ночью " + w.getAvgTempForPart(WeatherKeeper.Part.NIGHT, limit));
    }
}
