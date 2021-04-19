package com.demo.sd.app;

import com.demo.sd.app.model.Stock;
import com.demo.sd.app.model.User;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class AppTest {

    public static HttpResponse<String> createUser(String name, Long money) throws Exception {
        var values = new HashMap<String, String>() {{
            put("login", name);
            put("money", money.toString());
        }};

        var objectMapper = new ObjectMapper();
        String requestBody = objectMapper
                .writeValueAsString(values);

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/user"))
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return HttpClient.newHttpClient().send(request1, HttpResponse.BodyHandlers.ofString());
    }
    @ClassRule
    public static GenericContainer simpleWebServer
            = new FixedHostPortGenericContainer("hello-app:1.0-SNAPSHOT")
            .withFixedExposedPort(8080, 8080)
            .withExposedPorts(8080);

    @Test
    public void addStock() throws Exception {
        var values = new HashMap<String, String>() {{
            put("company", "Comp1");
            put("price", "15");
            put("count", "10");
        }};

        var objectMapper = new ObjectMapper();
        String requestBody = objectMapper
                .writeValueAsString(values);

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/stock"))
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        var response1 = HttpClient.newHttpClient().send(request1, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(200, response1.statusCode());
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/stock"))
                .GET()
                .build();

        var response2 = HttpClient.newHttpClient().send(request2, HttpResponse.BodyHandlers.ofString());

        Stock[] map = objectMapper.readValue(response2.body(), Stock[].class);

        Assert.assertEquals("Comp1", map[map.length - 1].getCompany());
    }

    @Test
    public void addUser() throws Exception {
        var response1 = createUser("Nick", (long) 0);

        Assert.assertEquals(200, response1.statusCode());
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/user"))
                .GET()
                .build();
        var response2 = HttpClient.newHttpClient().send(request2, HttpResponse.BodyHandlers.ofString());

        var objectMapper = new ObjectMapper();

        User[] map = objectMapper.readValue(response2.body(), User[].class);

        Assert.assertEquals("Nick", map[map.length - 1].getLogin());
    }

    @Test
    public void authUser() throws Exception {

        //createUser();
        var response = createUser("Ann", (long) 100);
        var objectMapper = new ObjectMapper();
        User map = objectMapper.readValue(response.body(), User.class);
        Integer id = map.getId();
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/user/" + id.toString()))
                .setHeader("Content-Type", "application/json")
                .GET()
                .build();

        var response1 = HttpClient.newHttpClient().send(request1, HttpResponse.BodyHandlers.ofString());
        Assert.assertEquals(200, response1.statusCode());
        objectMapper = new ObjectMapper();

        map = objectMapper.readValue(response1.body(), User.class);
        Assert.assertEquals("Ann", map.getLogin());
        id+=1;
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/user/" + id.toString()))
                .setHeader("Content-Type", "application/json")
                .GET()
                .build();

        var response2 = HttpClient.newHttpClient().send(request2, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(404, response2.statusCode());
    }

    @Test
    public void buyAndSell() throws Exception{
        var response = createUser("Seller", (long) 500);
        var objectMapper = new ObjectMapper();
        User map = objectMapper.readValue(response.body(), User.class);
        Integer id = map.getId();

        var values = new HashMap<String, String>() {{
            put("company", "Comp2");
            put("price", "15");
            put("count", "10");
        }};

        String requestBody = objectMapper
                .writeValueAsString(values);

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/stock"))
                .setHeader("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpClient.newHttpClient().send(request1, HttpResponse.BodyHandlers.ofString());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/api/user/" + id.toString() +"/buy?company=Comp2&count=2"))
                .setHeader("Content-Type", "application/json")
                .GET()
                .build();
        var response2 = HttpClient.newHttpClient().send(request2, HttpResponse.BodyHandlers.ofString());

        Assert.assertEquals(200, response2.statusCode());
    }
}
