import java.util.*;

class Driver {
    private String id;
    private int x, y;
    private boolean available;

    public Driver(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.available = true;
    }

    public String getId() { return id; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public void setLocation(int x, int y) { this.x = x; this.y = y; }

    public double distanceTo(int riderX, int riderY) {
        return Math.sqrt(Math.pow(this.x - riderX, 2) + Math.pow(this.y - riderY, 2));
    }
}

class Rider {
    private String id;
    private int x, y;

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
    private boolean isCompleted;

    public Ride(String rideId, Rider rider, Driver driver) {
        this.rideId = rideId;
        this.rider = rider;
        this.driver = driver;
        this.startX = rider.getX();
        this.startY = rider.getY();
        this.isCompleted = false;
    }

    public String getRideId() { return rideId; }
    public Driver getDriver() { return driver; }
    public boolean isCompleted() { return isCompleted; }

    public void stopRide(int x, int y, int time) {
        this.destinationX = x;
        this.destinationY = y;
        this.timeTaken = time;
        this.isCompleted = true;
        driver.setAvailable(true);
        driver.setLocation(x, y);

        System.out.println("Ride Stopped: " +
                "\nEnd Location: (" + x + ", " + y + ")" +
                "\nTime Taken: " + time);
    }

    public double calculateDistance() {
        return Math.sqrt(Math.pow(destinationX - startX, 2) + Math.pow(destinationY - startY, 2));
    }

    public double calculateFare() {
        double baseFare = 50;
        double distance = calculateDistance();
        double timeFare = 2 * timeTaken;
        double distanceFare = 6.5 * distance;
        double totalFare = baseFare + distanceFare + timeFare + (baseFare + distanceFare + timeFare) * 0.2;

        System.out.printf("BILL %s %s %.2f%n", rideId, driver.getId(), totalFare);
        return totalFare;
    }
}

public class Main {
    private static Map<String, Driver> drivers = new HashMap<>();
    private static Map<String, Rider> riders = new HashMap<>();
    private static Map<String, Ride> rides = new HashMap<>();
    private static Map<String, List<Driver>> matchedDrivers = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String[] input = scanner.nextLine().split(" ");
            switch (input[0]) {
                case "ADD_DRIVER":
                    drivers.put(input[1], new Driver(input[1], Integer.parseInt(input[2]), Integer.parseInt(input[3])));
                    break;
                case "ADD_RIDER":
                    riders.put(input[1], new Rider(input[1], Integer.parseInt(input[2]), Integer.parseInt(input[3])));
                    break;
                case "MATCH":
                    matchRider(input[1]);
                    break;
                case "START_RIDE":
                    startRide(input[1], Integer.parseInt(input[2]), input[3]);
                    break;
                case "STOP_RIDE":
                    stopRide(input[1], Integer.parseInt(input[2]), Integer.parseInt(input[3]), Integer.parseInt(input[4]));
                    break;
                case "BILL":
                    billRide(input[1]);
                    break;
            }
        }
        scanner.close();
    }

    private static void matchRider(String riderId) {
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

        nearbyDrivers.sort(Comparator.comparingDouble(d -> d.distanceTo(rider.getX(), rider.getY())));
        matchedDrivers.put(riderId, nearbyDrivers);

        System.out.print("DRIVERS_MATCHED");
        for (int i = 0; i < Math.min(5, nearbyDrivers.size()); i++) {
            System.out.print(" " + nearbyDrivers.get(i).getId());
        }
        System.out.println();
    }

    private static void startRide(String rideId, int driverNumber, String riderId) {
        List<Driver> nearbyDrivers = matchedDrivers.get(riderId);
        if (nearbyDrivers == null || driverNumber < 1 || driverNumber > nearbyDrivers.size() || !riders.containsKey(riderId)) {
            System.out.println("INVALID_RIDE");
            return;
        }

        Driver driver = nearbyDrivers.get(driverNumber - 1);
        driver.setAvailable(false);
        rides.put(rideId, new Ride(rideId, riders.get(riderId), driver));

        System.out.println("RIDE_STARTED " + rideId);
    }

    private static void stopRide(String rideId, int x, int y, int time) {
        Ride ride = rides.get(rideId);
        if (ride == null) {
            System.out.println("INVALID_RIDE");
            return;
        }
        ride.stopRide(x, y, time);
    }

    private static void billRide(String rideId) {
        Ride ride = rides.get(rideId);
        if (ride == null || !ride.isCompleted()) {
            System.out.println("INVALID_RIDE");
            return;
        }
        ride.calculateFare();
    }
}
