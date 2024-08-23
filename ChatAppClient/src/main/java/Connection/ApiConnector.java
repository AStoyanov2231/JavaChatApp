package Connection;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiConnector {

    private final String url;

    public ApiConnector(String url){
        this.url = url;
    }

    public String sendPostRequest(String params) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(params))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String sendGetRequest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Debug print from ApiConnector - sendGetRequest:");
        System.out.println(response.body());
        return response.body();
    }

    public String sendDeleteRequest(String params) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String Full_URL = url + params;
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(Full_URL))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        return response.body();
    }

    // TODO: testing new get request that returns objects
    // TODO: Commented out since it may not work/be needed
//    public List<String> sendGetRequest2() throws IOException, InterruptedException {
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder()
//                .GET()
//                .header("accept","application/json")
//                .uri(URI.create(url))
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        ObjectMapper mapper = new ObjectMapper();
//        Group groups = mapper.readValue(response.body(), new TypeReference<Group>() {});
//
//        System.out.println(groups.toString());
//        //groups.getFirst().getGroupName()
//
//        // debug
//        //System.out.println(response.body());
//
//        List<String> test = new ArrayList<>();
//        test.add("a");
//        test.add("b");
//        test.add("c");
//
//       return test;
//    }
}