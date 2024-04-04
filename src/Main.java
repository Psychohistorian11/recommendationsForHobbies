import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String apiKey = "AIzaSyA-dg5_nxz03QMOcCgRuXuQdCd3iAkNmDU";
    private static final String nickname_user1 = "cristianf";
    private static final String password_user1 = "abcd1234";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<String> hobbies = new ArrayList<>();
        List<String> pasatiempo = new ArrayList<>();

        while (true) {
            System.out.println("LOGIN BOOKING");
            System.out.println("---------------------------------");
            System.out.println("Enter your username: ");
            String user = scanner.nextLine();
            System.out.println("Enter your password: ");
            String password = scanner.nextLine();

            if (user.equals(nickname_user1) && password.equals(password_user1)) {
                while (true) {
                    System.out.println("BOOKING MENU");
                    System.out.println("---------------------------------");
                    System.out.println("1. Enter hobbies");
                    System.out.println("2. View recommendations based on my hobbies");
                    int opcion = scanner.nextInt();
                    scanner.nextLine();
                    if (opcion == 1) {
                        enterHobbies(scanner, hobbies);
                    } else if (opcion == 2) {
                        viewRecommendations(scanner, hobbies, pasatiempo);
                    } else {
                        System.out.println("Invalid Command");
                    }
                }
            } else {
                System.out.println("User does not exist");
            }
        }
    }

    private static void enterHobbies(Scanner scanner, List<String> hobbies) {
        while (true) {
            System.out.println("---------------------------------");
            System.out.print("Enter your hobbies: ");
            String hobby = scanner.nextLine();
            System.out.println("Press 's' if you don't have more hobbies");

            if (hobby.equalsIgnoreCase("s")) {
                break;
            }
            hobbies.add(hobby);
        }
    }

    private static void viewRecommendations(Scanner scanner, List<String> hobbies, List<String> pasatiempo) {
        if (hobbies.isEmpty()) {
            System.out.println("You haven't entered any hobbies");
        } else {
            System.out.println("What places do you want to see according to your hobbies:");
            System.out.println("Your hobbies:");
            int contador = 1;
            for (String hobbie : hobbies) {
                System.out.println(contador + "." + hobbie);
                contador++;
            }
            pasatiempo = new ArrayList<>();
            while (true) {
                int indice = scanner.nextInt();
                if (indice == 0) {
                    break;
                } else {
                    if (pasatiempo.contains(hobbies.get(indice - 1))){
                        System.out.println("[WARNING] You already selected this one before, try another one");
                    }
                    else{
                        pasatiempo.add(hobbies.get(indice - 1));
                    }

                    System.out.println("If you want to add more press '0'");
                }
            }
            double[] userLocation = getLocation();
            searchPlacesForHobbies(userLocation[0], userLocation[1], pasatiempo);
        }
    }

    public static double[] getLocation() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/geolocation/v1/geolocate?key=AIzaSyA-dg5_nxz03QMOcCgRuXuQdCd3iAkNmDU")
                .post(okhttp3.RequestBody.create(null, new byte[0]))
                .build();

        try {
            double[] userLocation = {0.0, 0.0};
            Response response = client.newCall(request).execute();
            String responseData = response.body().string();
            JSONObject jsonObject = new JSONObject(responseData);
            JSONObject location = jsonObject.getJSONObject("location");
            double latitude = location.getDouble("lat");
            double longitude = location.getDouble("lng");
            userLocation[0] = latitude;
            userLocation[1] = longitude;
            return userLocation;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new double[0];
    }

    public static void searchPlacesForHobbies(double lat, double lng, List<String> hobbies) {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();

        for (String hobby : hobbies) {
            NearbySearchRequest request = PlacesApi.nearbySearchQuery(context, new LatLng(lat, lng))
                    .keyword(hobby);
            request.radius(5000);
            try {
                PlacesSearchResponse response = request.await();
                System.out.println("Results for the hobby: " + hobby);
                System.out.println("---------------------------------");

                Arrays.stream(response.results).forEach(result -> {
                    System.out.println("Name: " + result.name);
                    System.out.println("Address: " + result.vicinity);
                    System.out.println("------------------------");
                });
            } catch (ApiException | InterruptedException | IOException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }
}
