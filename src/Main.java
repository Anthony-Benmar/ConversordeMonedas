import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class Main {

    // URL of the API with your API key
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/ae98580a94174dbb6415f33b/latest/USD";

    // HTTP client
    private static final HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int option;

        do {
            System.out.println("**********************************************************************************");
            System.out.println("Bienvenido al Conversor de Monedas");
            System.out.println("1) Realizar una conversión");
            System.out.println("2) Salir");
            System.out.println("**********************************************************************************");
            option = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer

            switch (option) {
                case 1 -> performConversion(scanner);
                case 2 -> System.out.println("Gracias por usar el conversor. ¡Hasta luego!");
                default -> System.out.println("Opción no válida, por favor intente nuevamente.");
            }
        } while (option != 2);
    }

    private static void performConversion(Scanner scanner) {
        try {
            // Get exchange rates from the API
            JsonObject rates = fetchExchangeRates();

            System.out.println("Seleccione la moneda de origen (por código): USD, ARS, BRL, COP, EUR, PEN");
            String fromCurrency = scanner.nextLine().toUpperCase();

            System.out.println("Seleccione la moneda de destino (por código): USD, ARS, BRL, COP, EUR, PEN");
            String toCurrency = scanner.nextLine().toUpperCase();

            System.out.println("Ingrese la cantidad a convertir:");
            double amount = scanner.nextDouble();

            // Perform conversion
            double rateFrom = rates.get(fromCurrency).getAsDouble();
            double rateTo = rates.get(toCurrency).getAsDouble();
            double convertedAmount = convertCurrency(amount, rateFrom, rateTo);

            System.out.printf("Resultado: %.2f %s = %.2f %s%n", amount, fromCurrency, convertedAmount, toCurrency);

        } catch (Exception e) {
            System.out.println("Error al realizar la conversión: " + e.getMessage());
        }
    }

    private static JsonObject fetchExchangeRates() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
            return jsonResponse.getAsJsonObject("conversion_rates");
        } else {
            throw new Exception("Error al obtener tasas de cambio. Código HTTP: " + response.statusCode());
        }
    }

    private static double convertCurrency(double amount, double rateFrom, double rateTo) {
        return amount * (rateTo / rateFrom);
    }
}
