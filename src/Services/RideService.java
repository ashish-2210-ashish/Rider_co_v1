package Services;

import java.util.*;

class Driver {
    private final String id;
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
    private final String id;
    private final int x, y;

    public Rider(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}

class Ride {
    private final String rideId;
    private final Rider rider;
    private final Driver driver;
    private final int startX, startY;
    private int destinationX, destinationY, timeTaken;
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
        System.out.println("RIDE_STOPPED " + rideId);
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

public class RideService {
    private final Map<String, Driver> drivers = new HashMap<>();
    private final Map<String, Rider> riders = new HashMap<>();
    private final Map<String, Ride> rides = new HashMap<>();
    private final Map<String, List<Driver>> matchedDrivers = new HashMap<>();

    public void addDriver(String id, int x, int y) {
        drivers.put(id, new Driver(id, x, y));
    }

    public void addRider(String id, int x, int y) {
        riders.put(id, new Rider(id, x, y));
    }

    public void matchRider(String riderId) {
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

    public void startRide(String rideId, int driverNumber, String riderId) {
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

    public void stopRide(String rideId, int x, int y, int time) {
        Ride ride = rides.get(rideId);
        if (ride == null) {
            System.out.println("INVALID_RIDE");
            return;
        }
        ride.stopRide(x, y, time);
    }

    public void billRide(String rideId) {
        Ride ride = rides.get(rideId);
        if (ride == null || !ride.isCompleted()) {
            System.out.println("INVALID_RIDE");
            return;
        }
        ride.calculateFare();
    }

    public void checkDriver(String driverId) {
        Driver driver = drivers.get(driverId);
        if (driver == null) {
            System.out.println("INVALID_DRIVER");
        } else {
            System.out.println(driver.isAvailable() ? "AVAILABLE" : "NOT_AVAILABLE");
        }
    }
}
