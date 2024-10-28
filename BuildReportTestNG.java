import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.util.*;

public class BuildReportTestNG {

    private List<BuildReport.Record> records;

    @BeforeMethod
    public void setUp() {
        String inputData = "2343225,2345,us_east,RedTeam,ProjectApple,3445s\n" +
                "1223456,2345,us_west,BlueTeam,ProjectBanana,2211s\n" +
                "3244332,2346,eu_west,YellowTeam3,ProjectCarrot,4322s\n" +
                "1233456,2345,us_west,BlueTeam,ProjectDate,2221s\n" +
                "3244132,2346,eu_west,YellowTeam3,ProjectEgg,4122s";

        records = Arrays.stream(inputData.split("\n"))
                .map(BuildReport.Record::new)
                .collect(Collectors.toList());
    }

    @Test
    public void testUniqueCustomersPerContract() {
        Map<String, Set<String>> contractToCustomers = new HashMap<>();
        for (BuildReport.Record record : records) {
            contractToCustomers
                    .computeIfAbsent(record.contractId, k -> new HashSet<>())
                    .add(record.customerId);
        }

        Assert.assertEquals(contractToCustomers.get("2345").size(), 3, "Unique customers for contract 2345 should be 3.");
        Assert.assertEquals(contractToCustomers.get("2346").size(), 2, "Unique customers for contract 2346 should be 2.");
    }

    @Test
    public void testUniqueCustomersPerGeozone() {
        Map<String, Set<String>> geozoneToCustomers = new HashMap<>();
        for (BuildReport.Record record : records) {
            geozoneToCustomers
                    .computeIfAbsent(record.geozone, k -> new HashSet<>())
                    .add(record.customerId);
        }

        Assert.assertEquals(geozoneToCustomers.get("us_east").size(), 1, "Unique customers for geozone us_east should be 1.");
        Assert.assertEquals(geozoneToCustomers.get("us_west").size(), 2, "Unique customers for geozone us_west should be 2.");
        Assert.assertEquals(geozoneToCustomers.get("eu_west").size(), 2, "Unique customers for geozone eu_west should be 2.");
    }

    @Test
    public void testAverageBuildDurationPerGeozone() {
        Map<String, List<Integer>> geozoneToDurations = new HashMap<>();
        for (BuildReport.Record record : records) {
            geozoneToDurations
                    .computeIfAbsent(record.geozone, k -> new ArrayList<>())
                    .add(record.buildDuration);
        }

        double usWestAverage = geozoneToDurations.get("us_west").stream()
                .mapToInt(Integer::intValue)
                .average().orElse(0);
        double euWestAverage = geozoneToDurations.get("eu_west").stream()
                .mapToInt(Integer::intValue)
                .average().orElse(0);

        Assert.assertEquals(geozoneToDurations.get("us_east").stream()
                .mapToInt(Integer::intValue)
                .average().orElse(0), 3445.0, "Average build duration for us_east should be 3445s.");
        Assert.assertEquals(usWestAverage, 2216.0, "Average build duration for us_west should be 2216s.");
        Assert.assertEquals(euWestAverage, 4222.0, "Average build duration for eu_west should be 4222s.");
    }

    @Test
    public void testUniqueCustomerListPerGeozone() {
        Map<String, Set<String>> geozoneToCustomers = new HashMap<>();
        for (BuildReport.Record record : records) {
            geozoneToCustomers
                    .computeIfAbsent(record.geozone, k -> new HashSet<>())
                    .add(record.customerId);
        }

        Set<String> usWestCustomers = new HashSet<>(Arrays.asList("1223456", "1233456"));
        Set<String> euWestCustomers = new HashSet<>(Arrays.asList("3244332", "3244132"));

        Assert.assertTrue(geozoneToCustomers.get("us_west").containsAll(usWestCustomers), "Customer list for us_west is incorrect.");
        Assert.assertTrue(geozoneToCustomers.get("eu_west").containsAll(euWestCustomers), "Customer list for eu_west is incorrect.");
    }
}
