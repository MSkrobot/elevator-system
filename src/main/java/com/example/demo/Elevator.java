package com.example.demo;

import java.util.*;

public class Elevator {
    final int id;
    int currentFloor;
    TreeSet<Integer> destinationsUp;
    TreeSet<Integer> destinationsDown;
    int direction; // -1 for down, 0 for idle, 1 for up
    int pickupDirection;

    // Constructor to initialize the elevator with a given id
    public Elevator(int id) {
        this.id = id;
        this.currentFloor = 0;
        this.destinationsUp = new TreeSet<>();
        this.destinationsDown = new TreeSet<>();
        this.direction = 0;
        this.pickupDirection = 0;
    }

    // Updates the destination floors for the elevator
    public void updateDestination(int destinationFloor) {
        if (destinationFloor == currentFloor) {
            return;
        }
        if (destinationFloor > currentFloor) {
            destinationsUp.add(destinationFloor);
        } else {
            destinationsDown.add(destinationFloor);
        }
        updateDirection();
    }

    // Updates the direction of the elevator
    private void updateDirection() {
        if (direction == 0) {
            if (!destinationsUp.isEmpty()) {
                direction = 1;
            } else if (!destinationsDown.isEmpty()) {
                direction = -1;
            }
        }
    }

    // Checks if the elevator can accept a request to a specified floor
    public boolean canAcceptRequest(int requestedFloor) {
        if (direction == 1) {
            return requestedFloor > currentFloor;
        } else if (direction == -1) {
            return requestedFloor < currentFloor;
        }
        return true;
    }

    // Moves the elevator one step in its current direction
    public void step() {
        if (direction == 1 && !destinationsUp.isEmpty()) {
            currentFloor++;
            if (destinationsUp.contains(currentFloor)) {
                destinationsUp.remove(currentFloor);
                System.out.println("Elevator " + id + " stopped at floor " + currentFloor);
            }
        } else if (direction == -1 && !destinationsDown.isEmpty()) {
            currentFloor--;
            if (destinationsDown.contains(currentFloor)) {
                destinationsDown.remove(currentFloor);
                System.out.println("Elevator " + id + " stopped at floor " + currentFloor);
            }
        }

        if (direction == 1) {
            if (destinationsUp.isEmpty() && !destinationsDown.isEmpty()) {
                direction = -1;
            }
        } else if (direction == -1) {
            if (destinationsDown.isEmpty() && !destinationsUp.isEmpty()) {
                direction = 1;
            }
        } else if (direction == 0) {
            if (!destinationsUp.isEmpty()) {
                direction = 1;
            } else if (!destinationsDown.isEmpty()) {
                direction = -1;
            }
        }
    }

    // Returns the current status of the elevator
    public Map<String, Object> status() {
        Map<String, Object> status = new HashMap<>();
        List<Integer> allDestinations = new ArrayList<>(destinationsUp);
        allDestinations.addAll(destinationsDown);
        Collections.sort(allDestinations);

        status.put("id", id);
        status.put("currentFloor", currentFloor);
        status.put("allDestinations", allDestinations);
        return status;
    }
}