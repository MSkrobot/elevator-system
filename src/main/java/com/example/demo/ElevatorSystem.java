package com.example.demo;

import java.util.*;

public class ElevatorSystem {
    List<Elevator> elevators = new ArrayList<>();
    Queue<Request> waitlist = new LinkedList<>();

    // Initializes the system with a given number of elevators
    public ElevatorSystem(int numElevators) {
        for (int i = 0; i < numElevators; i++) {
            elevators.add(new Elevator(i));
        }
    }

    // Handles a pickup request from a specific floor in a specific direction
    public int pickup(int floor, int direction) {
        Elevator closest = null;
        int minDistance = Integer.MAX_VALUE;
        for (Elevator elevator : elevators) {
            if (elevator.direction == direction || (elevator.destinationsDown.isEmpty() && elevator.destinationsUp.isEmpty())) {
                int distance = Math.abs(elevator.currentFloor - floor);
                if (distance < minDistance) {
                    closest = elevator;
                    minDistance = distance;
                }
            }
        }
        if (closest != null) {
            closest.updateDestination(floor);
            return closest.id;
        } else {
            waitlist.add(new Request(floor, direction));
            return -1;
        }
    }

    // Moves each elevator one step in their current direction
    public void step() {
        for (Elevator elevator : elevators) {
            elevator.step();
        }
        checkWaitlist();
    }

    // Returns the current status of all elevators
    public List<Map<String, Object>> status() {
        List<Map<String, Object>> statuses = new ArrayList<>();
        for (Elevator elevator : elevators) {
            statuses.add(elevator.status());
        }
        return statuses;
    }

    // Checks the waitlist and assigns requests to elevators if possible
    private void checkWaitlist() {
        Iterator<Request> iterator = waitlist.iterator();
        while (iterator.hasNext()) {
            Request request = iterator.next();
            int elevatorId = assignRequestToElevator(request);
            if (elevatorId != -1) {
                System.out.println("Waitlisted request for floor " + request.floor + " assigned to elevator " + elevatorId);
                iterator.remove();
            }
        }
    }

    // Assigns a request to an available elevator
    private int assignRequestToElevator(Request request) {
        for (Elevator elevator : elevators) {
            if ((elevator.direction == request.direction || elevator.direction == 0) &&
                    elevator.canAcceptRequest(request.floor)) {
                elevator.updateDestination(request.floor);
                return elevator.id;
            }
        }
        return -1;
    }
}