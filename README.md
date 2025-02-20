# Changes Made

- Removed `static` from all methods to avoid side effects in JUnit testing.
- Merged all methods into a single file (`RideService.java`).
- Combined all test cases into one file (`RideServiceTest.java`).
- Defined `Driver`, `Rider`, and `Ride` as global classes in `RideService.java`.


## Repo of the previous code (decomposed one)- https://github.com/ashish-2210-ashish/Rider_C0.git

---

# The Rider Co. - Ride Hailing System

## Problem Statement
Building a solution that will help match riders with drivers based on their location and generate a bill for the ride.

## Input Commands & Format

### 1. **ADD_DRIVER <DRIVER_ID> <X_COORDINATE> <Y_COORDINATE>**
This command allows a driver to join the service.  
**Arguments:**
- `DRIVER_ID`: Unique identifier for the driver.
- `X_COORDINATE`: X-coordinate of the driver's location.
- `Y_COORDINATE`: Y-coordinate of the driver's location.

### 2. **ADD_RIDER <RIDER_ID> <X_COORDINATE> <Y_COORDINATE>**
This command allows a rider to request a ride.  
**Arguments:**
- `RIDER_ID`: Unique identifier for the rider.
- `X_COORDINATE`: X-coordinate of the rider's location.
- `Y_COORDINATE`: Y-coordinate of the rider's location.

### 3. **MATCH <RIDER_ID>**
This command matches the rider with the nearest available drivers within a 5 km range.  
**Output Format:**
- Print the nearest 5 drivers’ IDs in ascending order of their distance from the rider.
- If two drivers are at an equal distance, print them in lexicographical order.

**Example Output:**

DRIVERS_MATCHED <DRIVER_ID1> <DRIVER_ID2> <DRIVER_ID3> <DRIVER_ID4> <DRIVER_ID5>

- If no drivers are available, print `NO_DRIVERS_AVAILABLE`.

### 4. **START_RIDE <RIDE_ID> <N> <RIDER_ID>**
This command starts the ride with the Nth driver from the matched list. The `N` should be between 1 and 5.  
**Conditions:**
- If the ride with the `RIDE_ID` already exists, or the driver is not available, print `INVALID_RIDE`.
- If there are fewer than N drivers, print `INVALID_RIDE`.

**Example Output:**

RIDE_STARTED <RIDE_ID>


### 5. **STOP_RIDE <RIDE_ID> <DESTINATION_X_COORDINATE> <DESTINATION_Y_COORDINATE> <TIME_TAKEN_IN_MIN>**
This command stops the ride.  
**Conditions:**
- If the `RIDE_ID` does not exist or the ride has already been stopped, print `INVALID_RIDE`.

**Example Output:**

RIDE_STOPPED <RIDE_ID>


### 6. **BILL <RIDE_ID>**
This command prints the total bill of the ride.  
**Bill Calculation Formula:**
1. A base fare of ₹50 is charged for every ride.
2. An additional ₹6.5 is charged for every kilometer traveled.
3. An additional ₹2 is charged for every minute spent in the ride.
4. A service tax of 20% is added to the final amount.

**Example Output:**

BILL <RIDE_ID> <DRIVER_ID> <AMOUNT>


## Assumptions:
1. No two drivers or riders will have the same ID.
2. Ride can only be started once the match is completed.
3. Every start ride request will happen after the match request.
4. One rider can make multiple match requests.
5. Bill for the ride will be calculated based on the distance between the rider's location and the destination.
6. The driver will not be available to accept another rider's request after the ride has started.


## Sample inputs:

ADD_DRIVER D1 2 3

ADD_DRIVER D2 5 6

ADD_DRIVER D3 1 1

ADD_DRIVER D4 7 8

ADD_DRIVER D5 10 10

ADD_RIDER R1 3 3

MATCH R1

START_RIDE RIDE101 1 R1

STOP_RIDE RIDE101 8 8 10

CHECK_DRIVER D1

BILL RIDE101



