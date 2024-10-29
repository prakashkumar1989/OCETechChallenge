package OCEChallenge.OCETechnical;

import java.util.*;
import java.util.stream.Collectors;

// Data class representing a record
class Record {
    private final String customerId;
    private final String contractId;
    private final String geozone;
    private final String teamcode;
    private final String projectcode;
    private final int buildDuration;

    // Constructor to parse each line
    public Record(String line) {
        String[] tokens = line.split(",");
        this.customerId = tokens[0];
        this.contractId = tokens[1];
        this.geozone = tokens[2];
        this.teamcode = tokens[3];
        this.projectcode = tokens[4];
        this.buildDuration = Integer.parseInt(tokens[5].replace("s", ""));
    }

    // Getters for each field
    public String getCustomerId() {
        return customerId;
    }

    public String getContractId() {
        return contractId;
    }

    public String getGeozone() {
        return geozone;
    }

    public int getBuildDuration() {
        return buildDuration;
    }
}

// Interface for report generation
interface ReportGenerator {
    void generateReport(List<Record> records);
}

// Concrete class to calculate contract-to-customer mapping
class ContractCustomerReport implements ReportGenerator {

    @Override
    public void generateReport(List<Record> records) {
        Map<String, Set<String>> contractToCustomers = records.stream()
                .collect(Collectors.groupingBy(
                        Record::getContractId,
                        Collectors.mapping(Record::getCustomerId, Collectors.toSet())
                ));

        System.out.println("Number of unique customerIds for each contractId:");
        contractToCustomers.forEach((contractId, customers) ->
                System.out.println("Contract ID: " + contractId + ", Unique Customers: " + customers.size()));
    }
}

// Concrete class to calculate geozone-to-customer mapping
class GeoZoneCustomerReport implements ReportGenerator {

    @Override
    public void generateReport(List<Record> records) {
        Map<String, Set<String>> geozoneToCustomers = records.stream()
                .collect(Collectors.groupingBy(
                        Record::getGeozone,
                        Collectors.mapping(Record::getCustomerId, Collectors.toSet())
                ));

        System.out.println("\nNumber of unique customerIds for each geozone:");
        geozoneToCustomers.forEach((geozone, customers) ->
                System.out.println("Geozone: " + geozone + ", Unique Customers: " + customers.size()));
    }
}

// Concrete class to calculate average build duration per geozone
class GeoZoneDurationReport implements ReportGenerator {

    @Override
    public void generateReport(List<Record> records) {
        Map<String, List<Integer>> geozoneToDurations = records.stream()
                .collect(Collectors.groupingBy(
                        Record::getGeozone,
                        Collectors.mapping(Record::getBuildDuration, Collectors.toList())
                ));

        System.out.println("\nAverage build duration for each geozone:");
        geozoneToDurations.forEach((geozone, durations) -> {
            double averageDuration = durations.stream().mapToInt(Integer::intValue).average().orElse(0);
            System.out.println("Geozone: " + geozone + ", Average Build Duration: " + averageDuration + "s");
        });
    }
}

// Concrete class to list unique customerIds for each geozone
class GeoZoneCustomerListReport implements ReportGenerator {

    @Override
    public void generateReport(List<Record> records) {
        Map<String, Set<String>> geozoneToCustomers = records.stream()
                .collect(Collectors.groupingBy(
                        Record::getGeozone,
                        Collectors.mapping(Record::getCustomerId, Collectors.toSet())
                ));

        System.out.println("\nList of unique customerIds for each geozone:");
        geozoneToCustomers.forEach((geozone, customers) ->
                System.out.println("Geozone: " + geozone + ", Customers: " + customers));
    }
}

// Main class responsible for processing and delegating to different reports
public class BuildReport {

    public static void main(String[] args) {
        String inputData = "2343225,2345,us_east,RedTeam,ProjectApple,3445s\n" +
                           "1223456,2345,us_west,BlueTeam,ProjectBanana,2211s\n" +
                           "3244332,2346,eu_west,YellowTeam3,ProjectCarrot,4322s\n" +
                           "1233456,2345,us_west,BlueTeam,ProjectDate,2221s\n" +
                           "3244132,2346,eu_west,YellowTeam3,ProjectEgg,4122s";

        List<Record> records = Arrays.stream(inputData.split("\n"))
                .map(Record::new)
                .collect(Collectors.toList());

        // List of different report generators
        List<ReportGenerator> reportGenerators = Arrays.asList(
                new ContractCustomerReport(),
                new GeoZoneCustomerReport(),
                new GeoZoneDurationReport(),
                new GeoZoneCustomerListReport()
        );

        // Generate each report
        reportGenerators.forEach(generator -> generator.generateReport(records));
    }
}
