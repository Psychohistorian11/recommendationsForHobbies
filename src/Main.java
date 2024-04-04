import com.google.maps.*;
import com.google.maps.errors.ApiException;
import com.google.maps.model.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Lista de hobbies
        Scanner scanner = new Scanner(System.in);
        List<String> hobbies = new ArrayList<>();
        List<String> pasatiempo = new ArrayList<>();
        String nickname_user1 = "cristianf";
        String password_user1 = "abcd1234";
        System.out.println("LOGIN BOOKING");
        System.out.println("---------------------------------");
        System.out.println("Ingrese su usuario: ");
        String user = scanner.nextLine();
        System.out.println("Ingrese su contraseña: ");
        String password = scanner.nextLine();

        if (user.equals(nickname_user1) && password.equals(password_user1)) {
            while(true){
                System.out.println("BOOKING MENU");
                System.out.println("---------------------------------");
                System.out.println("1. Ingresar hobbies");
                System.out.println("2. Ver recomendaciones segun mis hobbies");
                int opcion = scanner.nextInt();
                scanner.nextLine();
                if (opcion == 1){
                    while(true) {
                        System.out.println("---------------------------------");
                        System.out.print("Ingresa tus Hobbies: ");
                        String hobby = scanner.nextLine();
                        System.out.println("Presione la letra 's' en caso de que no tenga más hobbies");

                        // Verificar si se presiona la tecla 'q' para salir
                        if (hobby.equalsIgnoreCase("s")) {
                            break;
                        }
                        hobbies.add(hobby);
                    }

                } else if (opcion == 2) {
                    if(hobbies.isEmpty()){
                        System.out.println("No has ingresado hobbies");
                    }else {
                        System.out.println("Que lugares quieres que te muestre según tus hobbies:");
                        System.out.println("Tus hobbies:");
                        int contador = 1;
                        for (String hobbie : hobbies) {
                            System.out.println(contador + "." + hobbie);
                            contador = contador + 1;
                        }
                        while(true) {
                            int indice = scanner.nextInt();
                            if (indice == 0 ) {
                                break;
                            }else{
                            pasatiempo.add(hobbies.get(indice - 1));
                            System.out.println("En caso de no agregar mas pulse '0'");
                            }
                        }
                        String apiKey = "AIzaSyA-dg5_nxz03QMOcCgRuXuQdCd3iAkNmDU";
                        double[] userLocation = getLocation();
                        searchPlacesForHobbies(userLocation[0], userLocation[1], apiKey, pasatiempo);
                        //scanner.close();
                    }

                }else{
                    System.out.println("Comando Invalido");
                }

            }
        }else {
            System.out.println("El usuario no existe");
        }
        scanner.close();
    }

    public static double[] getLocation() {
        // Crea un cliente HTTP
        OkHttpClient client = new OkHttpClient();

        // Construye la solicitud para obtener la ubicación
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
            double latitud = location.getDouble("lat");
            double longitud = location.getDouble("lng");

            System.out.println("Latitud: " + latitud);
            System.out.println("Longitud: " + longitud);

            userLocation[0] = latitud;
            userLocation[1] = longitud;

            return userLocation;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new double[0];
    }

    public static void searchPlacesForHobbies(double lat, double lng, String apiKey, List<String> hobbies) {
        // Inicializar el cliente de Google Places
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();

        // Iterar sobre cada hobby y buscar lugares cercanos
        for (String hobby : hobbies) {
            // Crear una solicitud de lugares cercanos con filtro de palabra clave
            NearbySearchRequest request = PlacesApi.nearbySearchQuery(context, new LatLng(lat, lng))
                    .keyword(hobby);

            request.radius(1000);

            try {
                // Realizar la solicitud y obtener los resultados
                PlacesSearchResponse response = request.await();

                System.out.println("Resultados para el hobby: " + hobby);
                System.out.println("---------------------------------");

                for (PlacesSearchResult result : response.results) {
                    System.out.println("Nombre: " + result.name);
                    System.out.println("Dirección: " + result.vicinity);
                    System.out.println("------------------------");
                }
            } catch (ApiException | InterruptedException | IOException e) {
                e.printStackTrace();
            }

            System.out.println();
        }
    }
}
