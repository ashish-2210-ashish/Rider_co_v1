import java.util.Scanner;
import Services.*;

public class Main {
    public static void main(String[] args) {
        RideService rideService = new RideService();
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String[] input = scanner.nextLine().split(" ");
            switch (input[0]) {
                case "ADD_DRIVER":
                    rideService.addDriver(input[1], Integer.parseInt(input[2]), Integer.parseInt(input[3]));
                    break;
                case "ADD_RIDER":
                    rideService.addRider(input[1], Integer.parseInt(input[2]), Integer.parseInt(input[3]));
                    break;
                case "MATCH":
                    rideService.matchRider(input[1]);
                    break;
                case "START_RIDE":
                    rideService.startRide(input[1], Integer.parseInt(input[2]), input[3]);
                    break;
                case "STOP_RIDE":
                    rideService.stopRide(input[1], Integer.parseInt(input[2]), Integer.parseInt(input[3]), Integer.parseInt(input[4]));
                    break;
                case "BILL":
                    rideService.billRide(input[1]);
                    break;
                case "CHECK_DRIVER":
                    rideService.checkDriver(input[1]);
                    break;
                default:
                    System.out.println("INVALID_COMMAND");
            }
        }
        scanner.close();
    }
}
