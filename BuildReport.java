package OCEChallenge.OCETechnical;
import java.util.*;
import java.util.stream.Collectors;

public class BuildReport {

    // Helper class to represent a record
    static class Record {
        String customerId;
        String contractId;
        String geozone;
        String teamcode;
        String projectcode;
        int buildDuration;

        // Constructor to parse each line
        public Record(String line) {
            String[] tokens = line.split(",");
            this.customerId = tokens[0];
            this.contractId = tokens[1];
            this.geozone = tokens[2];
            this.teamcode = tokens[3];
            this.projectcode = tokens[4];
            this.buildDuration = Integer.parseInt(tokens[5].replace("s", "")); // remove 's' and convert to integer
        }
    }
    
    
    
    

    public static void main(String[] args) {
        // Input multiline string
        String inputData = "2343225,2345,us_east,RedTeam,ProjectApple,3445s\n" +
                           "1223456,2345,us_west,BlueTeam,ProjectBanana,2211s\n" +
                           "3244332,2346,eu_west,YellowTeam3,ProjectCarrot,4322s\n" +
                           "1233456,2345,us_west,BlueTeam,ProjectDate,2221s\n" +
                           "3244132,2346,eu_west,YellowTeam3,ProjectEgg,4122s";

        List<Record> records = Arrays.stream(inputData.split("\n"))
                .map(Record::new)
                .collect(Collectors.toList());

        // Maps for the required outputs
        Map<String, Set<String>> contractToCustomers = new HashMap<>();
        Map<String, Set<String>> geozoneToCustomers = new HashMap<>();
        Map<String, List<Integer>> geozoneToDurations = new HashMap<>();

        // Populate maps
        for (Record record : records) {
            // Unique customerIds per contractId
            contractToCustomers
                    .computeIfAbsent(record.contractId, k -> new HashSet<>())
                    .add(record.customerId);

            // Unique customerIds per geozone
            geozoneToCustomers
                    .computeIfAbsent(record.geozone, k -> new HashSet<>())
                    .add(record.customerId);

            // Build durations per geozone for average calculation
            geozoneToDurations
                    .computeIfAbsent(record.geozone, k -> new ArrayList<>())
                    .add(record.buildDuration);
        }

        // Report output
        System.out.println("Number of unique customerIds for each contractId:");
        contractToCustomers.forEach((contractId, customers) ->
                System.out.println("Contract ID: " + contractId + ", Unique Customers: " + customers.size()));

        System.out.println("\nNumber of unique customerIds for each geozone:");
        geozoneToCustomers.forEach((geozone, customers) ->
                System.out.println("Geozone: " + geozone + ", Unique Customers: " + customers.size()));

        System.out.println("\nAverage build duration for each geozone:");
        geozoneToDurations.forEach((geozone, durations) -> {
            double averageDuration = durations.stream().mapToInt(Integer::intValue).average().orElse(0);
            System.out.println("Geozone: " + geozone + ", Average Build Duration: " + averageDuration + "s");
        });

        System.out.println("\nList of unique customerIds for each geozone:");
        geozoneToCustomers.forEach((geozone, customers) ->
                System.out.println("Geozone: " + geozone + ", Customers: " + customers));
    }
}
