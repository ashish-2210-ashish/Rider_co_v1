import java.util.*;

class Driver {
    private String id;
    private int x, y;  // Driver's current location
    private boolean available;  // Availability status of the driver

    // Constructor to initialize driver details
    public Driver(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.available = true;  // Driver is available by default
    }

    public String getId() { return id; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public void setLocation(int x, int y) { this.x = x; this.y = y; }

    // Calculate distance between driver and rider using Euclidean formula
    public double distanceTo(int riderX, int riderY) {
        return Math.sqrt(Math.pow(this.x - riderX, 2) + Math.pow(this.y - riderY, 2));
    }
}

class Rider {
    private String id;
    private int x, y;  // Rider's current location

    // Constructor to initialize rider details
    public Rider(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}

class Ride {
    private String rideId;
    private Rider rider;
    private Driver driver;
    private int startX, startY, destinationX, destinationY, timeTaken;
    private boolean isCompleted;  // Status of the ride

    // Constructor to initialize ride details
    public Ride(String rideId, Rider rider, Driver driver) {
        this.rideId = rideId;
        this.rider = rider;
        this.driver = driver;
        this.startX = rider.getX();
        this.startY = rider.getY();
        this.isCompleted = false;  // Ride starts as incomplete
    }

    public String getRideId() { return rideId; }
    public Driver getDriver() { return driver; }
    public boolean isCompleted() { return isCompleted; }

    // Stop the ride and update driver status and location
    public void stopRide(int x, int y, int time) {
        this.destinationX = x;
        this.destinationY = y;
        this.timeTaken = time;
        this.isCompleted = true;

        driver.setAvailable(true);  // Mark driver as available again
        driver.setLocation(x, y);  // Update driver's location

        System.out.println("Ride Stopped: " +
                "\nEnd Location: (" + x + ", " + y + ")" +
                "\nTime Taken: " + time);
    }

    // Calculate total distance traveled during the ride
    public double calculateDistance() {
        return Math.sqrt(Math.pow(destinationX - startX, 2) + Math.pow(destinationY - startY, 2));
    }

    // Calculate the fare for the ride
    public double calculateFare() {
        double baseFare = 50;
        double distance = calculateDistance();
        double timeFare = 2 * timeTaken;  // Time-based fare
        double distanceFare = 6.5 * distance;  // Distance-based fare
        double totalFare = baseFare + distanceFare + timeFare + (baseFare + distanceFare + timeFare) * 0.2; // Adding 20% service fee

        System.out.printf("BILL %s %s %.2f%n", rideId, driver.getId(), totalFare);
        return totalFare;
    }
}

public class Main {
    private Map<String, Driver> drivers = new HashMap<>();  // Store driver details
    private Map<String, Rider> riders = new HashMap<>();  // Store rider details
    private Map<String, Ride> rides = new HashMap<>();  // Store active rides
    private Map<String, List<Driver>> matchedDrivers = new HashMap<>();  // Store matched drivers for each rider

    // Process user input commands
    public void processCommands() {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String[] input = scanner.nextLine().split(" ");
            switch (input[0]) {
                case "ADD_DRIVER":
                    // Register a new driver
                    drivers.put(input[1], new Driver(input[1], Integer.parseInt(input[2]), Integer.parseInt(input[3])));
                    break;
                case "ADD_RIDER":
                    // Register a new rider
                    riders.put(input[1], new Rider(input[1], Integer.parseInt(input[2]), Integer.parseInt(input[3])));
                    break;
                case "MATCH":
                    // Match rider with nearby drivers
                    matchRider(input[1]);
                    break;
                case "START_RIDE":
                    // Start a ride with a selected driver
                    startRide(input[1], Integer.parseInt(input[2]), input[3]);
                    break;
                case "STOP_RIDE":
                    // End the ride and mark driver as available
                    stopRide(input[1], Integer.parseInt(input[2]), Integer.parseInt(input[3]), Integer.parseInt(input[4]));
                    break;
                case "BILL":
                    // Generate the fare for a completed ride
                    billRide(input[1]);
                    break;
                case "CHECK_DRIVER":
                    // Check if a driver is available
                    checkDriver(input[1]);
                    break;
            }
        }
        scanner.close();
    }

    // Find nearby drivers for a rider within a 5-unit distance
    private void matchRider(String riderId) {
        Rider rider = riders.get(riderId);
        if (rider == null) {
            System.out.println("INVALID_RIDER");
            return;
        }

        List<Driver> nearbyDrivers = new ArrayList<>();
        for (Driver driver : drivers.values()) {
            if (driver.isAvailable() && driver.distanceTo(rider.getX(), rider.getY()) <= 5) {
                nearbyDrivers.add(driver);
            }
        }

        if (nearbyDrivers.isEmpty()) {
            System.out.println("NO_DRIVERS_AVAILABLE");
            return;
        }

        // Sort drivers by shortest distance to the rider
        nearbyDrivers.sort(Comparator.comparingDouble(d -> d.distanceTo(rider.getX(), rider.getY())));
        matchedDrivers.put(riderId, nearbyDrivers);

        // Print up to 5 matched drivers
        System.out.print("DRIVERS_MATCHED ");
        for (int i = 0; i < Math.min(5, nearbyDrivers.size()); i++) {
            System.out.print(" " + nearbyDrivers.get(i).getId());
        }
        System.out.println();
    }

    // Start a ride with a selected driver from the matched list
    private void startRide(String rideId, int driverNumber, String riderId) {
        List<Driver> nearbyDrivers = matchedDrivers.get(riderId);
        if (nearbyDrivers == null || driverNumber < 1 || driverNumber > nearbyDrivers.size() || !riders.containsKey(riderId)) {
            System.out.println("INVALID_RIDE");
            return;
        }

        Driver driver = nearbyDrivers.get(driverNumber - 1);
        driver.setAvailable(false);  // Mark driver as unavailable
        rides.put(rideId, new Ride(rideId, riders.get(riderId), driver));

        System.out.println("RIDE_STARTED " + rideId);
    }

    // Stop the ride, update driver status, and set ride as completed
    private void stopRide(String rideId, int x, int y, int time) {
        Ride ride = rides.get(rideId);
        if (ride == null) {
            System.out.println("INVALID_RIDE");
            return;
        }
        ride.stopRide(x, y, time);

        // Mark driver as available again
        Driver driver = ride.getDriver();
        driver.setAvailable(true);
        driver.setLocation(x, y);
        drivers.put(driver.getId(), driver);
    }

    // Generate and print the bill for a completed ride
    private void billRide(String rideId) {
        Ride ride = rides.get(rideId);
        if (ride == null || !ride.isCompleted()) {
            System.out.println("INVALID_RIDE");
            return;
        }
        ride.calculateFare();
    }

    // Check if a driver is available
    private void checkDriver(String driverId) {
        Driver driver = drivers.get(driverId);
        if (driver == null) {
            System.out.println("INVALID_DRIVER");
        } else {
            System.out.println(driver.isAvailable() ? "AVAILABLE" : "NOT_AVAILABLE");
        }
    }

    // Main function to start the ride system
    public static void main(String[] args) {
        Main rideSystem = new Main();
        rideSystem.processCommands();
    }
}
