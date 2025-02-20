package Tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class RideServiceTests {
    private Services.RideService rideService;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        rideService = new Services.RideService();
        System.setOut(new PrintStream(outputStream)); // Redirect system output for testing
    }

    @Test
    void testAddDriver() {
        rideService.addDriver("D1", 1, 2);
        rideService.checkDriver("D1");
        assertEquals("AVAILABLE\n", outputStream.toString());
    }

    @Test
    void testAddRider() {
        rideService.addRider("R1", 5, 5);
        assertNotNull(rideService); // Ensuring rideService object is valid
    }

    @Test
    void testMatchRider_NoDriversAvailable() {
        rideService.addRider("R1", 0, 0);
        rideService.matchRider("R1");
        assertEquals("NO_DRIVERS_AVAILABLE\n", outputStream.toString());
    }

    @Test
    void testMatchRider_DriversAvailable() {
        rideService.addDriver("D1", 1, 1);
        rideService.addRider("R1", 0, 0);
        rideService.matchRider("R1");

        String output = outputStream.toString().trim();
        assertTrue(output.startsWith("DRIVERS_MATCHED D1"));
    }

    @Test
    void testMatchRider_MultipleDrivers() {
        rideService.addDriver("D1", 1, 1);
        rideService.addDriver("D2", 2, 2);
        rideService.addDriver("D3", 3, 3);
        rideService.addRider("R1", 0, 0);
        rideService.matchRider("R1");

        String output = outputStream.toString();
        assertTrue(output.contains("DRIVERS_MATCHED D1 D2 D3"));
    }

    @Test
    void testStartRide_ValidRide() {
        rideService.addDriver("D1", 1, 1);
        rideService.addRider("R1", 0, 0);
        rideService.matchRider("R1");
        rideService.startRide("RIDE1", 1, "R1");

        String output = outputStream.toString();
        assertTrue(output.contains("RIDE_STARTED RIDE1"));
    }

    @Test
    void testStartRide_NoDriverMatched() {
        rideService.addRider("R1", 0, 0);
        rideService.startRide("RIDE1", 1, "R1");
        assertEquals("INVALID_RIDE\n", outputStream.toString());
    }

    @Test
    void testStopRide_InvalidRide() {
        rideService.stopRide("INVALID_RIDE", 10, 10, 15);
        assertEquals("INVALID_RIDE\n", outputStream.toString());
    }

    @Test
    void testStopRide_ValidRide() {
        rideService.addDriver("D1", 1, 1);
        rideService.addRider("R1", 0, 0);
        rideService.matchRider("R1");
        rideService.startRide("RIDE1", 1, "R1");
        rideService.stopRide("RIDE1", 10, 10, 15);

        String output = outputStream.toString();
        assertTrue(output.contains("RIDE_STOPPED RIDE1"));
    }

    @Test
    void testStopRide_WithoutStarting() {
        rideService.stopRide("RIDE1", 10, 10, 15);
        assertEquals("INVALID_RIDE\n", outputStream.toString());
    }

    @Test
    void testBillRide_InvalidRide() {
        rideService.billRide("INVALID_RIDE");
        assertEquals("INVALID_RIDE\n", outputStream.toString());
    }


    @Test
    void testBillRide_ValidRide() {
        rideService.addDriver("D1", 1, 1);
        rideService.addRider("R1", 0, 0);
        rideService.matchRider("R1");
        rideService.startRide("RIDE1", 1, "R1");
        rideService.stopRide("RIDE1", 10, 10, 15);
        rideService.billRide("RIDE1");

        String output = outputStream.toString();
        assertTrue(output.contains("BILL RIDE1 D1"));
    }

    @Test
    void testCheckDriver_NotAvailable() {
        rideService.addDriver("D1", 1, 1);
        rideService.addRider("R1", 0, 0);
        rideService.matchRider("R1");
        rideService.startRide("RIDE1", 1, "R1");
        rideService.checkDriver("D1");

        assertTrue(outputStream.toString().contains("NOT_AVAILABLE"));
    }

    @Test
    void testCheckDriver_AfterRide() {
        rideService.addDriver("D1", 1, 1);
        rideService.addRider("R1", 0, 0);
        rideService.matchRider("R1");
        rideService.startRide("RIDE1", 1, "R1");
        rideService.stopRide("RIDE1", 10, 10, 15);
        rideService.checkDriver("D1");

        assertTrue(outputStream.toString().contains("AVAILABLE"));
    }

    @Test
    void testDuplicateDrivers() {
        rideService.addDriver("D1", 1, 1);
        rideService.addDriver("D1", 2, 2);
        rideService.checkDriver("D1");

        assertTrue(outputStream.toString().contains("AVAILABLE"));
    }

    @Test
    void testDuplicateRiders() {
        rideService.addRider("R1", 0, 0);
        rideService.addRider("R1", 5, 5);
        rideService.matchRider("R1");

        assertTrue(outputStream.toString().contains("NO_DRIVERS_AVAILABLE"));
    }
}
