import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class FuzzyLogicProcessor {
    public static void main(String[] args) {
        LinkedHashMap<Double, Double> tlow = new LinkedHashMap<>();
        LinkedHashMap<Double, Double> thigh = new LinkedHashMap<>();
        LinkedHashMap<Double, Double> dlow = new LinkedHashMap<>();
        LinkedHashMap<Double, Double> dhigh = new LinkedHashMap<>();

        System.out.println("Choose fuzzy set file:");
        System.out.println("Type 1 for sets1.txt");
        System.out.println("Type 2 for sets2.txt");
        Scanner scanner = new Scanner(System.in);
        int fileChoice = -1;
        while (fileChoice != 1 && fileChoice != 2) {
            if (scanner.hasNextInt()) {
                fileChoice = scanner.nextInt();
                if (fileChoice != 1 && fileChoice != 2) {
                    System.out.print("Invalid choice. Please enter 1 or 2: ");
                }
            } else {
                scanner.next();
                System.out.print("Invalid input. Please enter 1 or 2: ");
            }
        }

        String filename = (fileChoice == 1) ? "src/ai/sets1.txt" : "src/ai/sets2.txt";
        File f = new File(filename); 
        if (!f.exists()) {
            System.out.println("File doesn't exist.");
            return;
        }

        // Step A1: Calculate implication function
        convertFileToHashMaps(f, tlow, thigh, dlow, dhigh);

        double key = getTemperature(scanner);

        System.out.println("Choose method:");
        System.out.println("Press 1 for MIN, 2 for PRODUCT, or 3 for both: ");
        int option = -1;
        while (option != 1 && option != 2 && option != 3) {
            if (scanner.hasNextInt()) {
                option = scanner.nextInt();
                if (option != 1 && option != 2 && option != 3) {
                    System.out.print("Invalid choice. Please enter 1, 2, or 3: ");
                }
            } else {
                scanner.next();
                System.out.print("Invalid input. Please enter 1, 2, or 3: ");
            }
        }
        scanner.close();

        // If the temperature key doesn't exist in the HashMaps, exit
        if (!thigh.containsKey(key) || !tlow.containsKey(key)) {
            return;
        } 

        // Step A2: Calculate partial results

        // K1
        double p1 = thigh.get(key);
        System.out.print("KEY: " + key);
        System.out.println();
        System.out.print("KEY VALUE: " + p1);
        System.out.println();
        List<Double> k1mins1 = new ArrayList<>(); // List of mins for K1
        List<Double> k1products = new ArrayList<>();
        for (double value : dhigh.values()) {
            if (option == 1 || option == 3) {
                k1mins1.add(Math.min(p1, value)); // Minimum between p1 and value
            }
            if (option == 2 || option == 3) {
                k1products.add(p1 * value); // Product of p1 and value
            }
        }

        // K2
        double p2 = tlow.get(key);
        List<Double> k2mins1 = new ArrayList<>(); // List of mins for K2
        List<Double> k2products = new ArrayList<>();
        for (double value : dlow.values()) {
            if (option == 1 || option == 3) {
                k2mins1.add(Math.min(p2, value)); // Minimum between p2 and value
            }
            if (option == 2 || option == 3) {
                k2products.add(p2 * value); // Product of p2 and value
            }
        }

        System.out.println();
        System.out.print("KEY: " + key);
        System.out.println();
        System.out.print("KEY VALUE: " + p2);
        System.out.println();

        // Step A3: Aggregate results
        HashMap<Double, Double> maxValues = new HashMap<>();
        HashMap<Double, Double> sumValues = new HashMap<>();
        int i = 0;

        // Loop through keys in dhigh
        for (Double k : dhigh.keySet()) {
            if (option == 1 || option == 3) {
                maxValues.put(k, Math.max(k1mins1.get(i), k2mins1.get(i))); // Max between k1mins1 and k2mins1
            }
            if (option == 2 || option == 3) {
                sumValues.put(k, k1products.get(i) + k2products.get(i)); // Sum of products
            }
            i++;
        }

        // If PRODUCT method or both selected, calculate result using product method
        if (option == 2 || option == 3) {
            double upperSum = 0; // Numerator sum
            double lowerSum = 0; // Denominator sum
            for (double k : sumValues.keySet()) {
                upperSum += k * sumValues.get(k);
                lowerSum += sumValues.get(k);
            }

            System.out.println("SUM VALUES");
            for (double k : sumValues.keySet()) {
                System.out.println(k + ": " + sumValues.get(k));
            }
            System.out.println();

            double productRes = upperSum / lowerSum;
            System.out.println("Product method: " + productRes);
        }

        // If MIN method or both selected, calculate result using max method
        if (option == 1 || option == 3) {
            double max = Double.MIN_VALUE; // Initialize max value
            for (double value : maxValues.values()) {
                max = Math.max(max, value);
            }

            double sum = 0;
            int cnt = 0;

            // Sum keys with max value and count them
            for (Map.Entry<Double, Double> entry : maxValues.entrySet()) {
                if (entry.getValue().equals(max)) {
                    sum += entry.getKey();
                    cnt++;
                }
            }

            // Step A4: Defuzzification
            // Calculate average of maxima
            double avg_of_maxima = sum / cnt;
            System.out.println("Average of maxima: " + avg_of_maxima);
        }
    }

    private static double getTemperature(Scanner scanner) {
        double temp = 0.0;
        System.out.print("Enter temperature: "); 
        while (!scanner.hasNextDouble()) {
            scanner.next();
            System.out.print("Error. Enter temperature (use , for doubles): ");
        }
        temp = scanner.nextDouble();
        System.out.println("You entered: " + temp);
        return temp;
    }

    private static void convertFileToHashMaps(File f, LinkedHashMap<Double, Double> tlow,
                                              LinkedHashMap<Double, Double> thigh,
                                              LinkedHashMap<Double, Double> dlow,
                                              LinkedHashMap<Double, Double> dhigh) {
        // Create HashMaps for each category
        try (Scanner scanner = new Scanner(f)) {
            String numbers = "0123456789."; // Allowed digits and decimal points
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty() || !line.contains("/")) {
                    continue; // Skip invalid lines
                }
                // Remove braces and split pairs by comma
                line = line.replaceAll("[\\{\\}]", "").trim();
                String[] pairs = line.split(",");

                if (line.startsWith("TLOW")) {
                    parsePairs(pairs, tlow);
                } else if (line.startsWith("THIGH")) {
                    parsePairs(pairs, thigh);
                } else if (line.startsWith("DLOW")) {
                    parsePairs(pairs, dlow);
                } else if (line.startsWith("DHIGH")) {
                    parsePairs(pairs, dhigh);
                }
            }

            // Print contents of each HashMap
            printHashMap("TLOW", tlow);
            printHashMap("THIGH", thigh);
            printHashMap("DLOW", dlow);
            printHashMap("DHIGH", dhigh);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void parsePairs(String[] pairs, HashMap<Double, Double> map) {
        StringBuilder firstPairInt = new StringBuilder();
        String numbers = "0123456789./";
        String firstPair = pairs[0];
        for (int i = 0; i < firstPair.length(); i++) {
            if (numbers.indexOf(firstPair.charAt(i)) != -1) {
                firstPairInt.append(firstPair.charAt(i));
            }
        }
        pairs[0] = firstPairInt.toString();

        for (String pair : pairs) {
            pair = pair.trim();
            if (pair.contains("/")) {
                String[] keyValue = pair.split("/");
                if (keyValue.length == 2) {
                    try {
                        double keyValueDouble = Double.parseDouble(keyValue[1].trim());
                        double valueValueDouble = Double.parseDouble(keyValue[0].trim());
                        map.put(keyValueDouble, valueValueDouble);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid number format in pair: " + pair);
                    }
                }
            }
        }
    }

    private static void printHashMap(String mapName, HashMap<Double, Double> map) {
        System.out.println(mapName + " HASHMAP:");
        for (double num : map.keySet()) {
            System.out.println(num + ": " + map.get(num));
        }
    }
}
