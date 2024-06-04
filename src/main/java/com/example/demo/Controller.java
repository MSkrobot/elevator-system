package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/elevators")
public class Controller {
    private final ElevatorSystem elevatorSystem = new ElevatorSystem(16);

    // Endpoint to get the status of all elevators
    @GetMapping("/status")
    public ResponseEntity<List<Map<String, Object>>> getStatus() {
        List<Map<String, Object>> status = elevatorSystem.status();
        return ResponseEntity.ok(status);
    }

    // Endpoint to handle pickup requests from a specific floor and direction
    @PostMapping("/pickup")
    public ResponseEntity<Map<String, Integer>> pickup(@RequestParam int floor, @RequestParam int direction) {
        int elevatorId = elevatorSystem.pickup(floor, direction);
        if (elevatorId != -1) {
            Map<String, Integer> result = new HashMap<>();
            result.put("elevatorId", elevatorId);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", 404));
        }
    }

    // Endpoint to advance the state of the elevator system by one step
    @PostMapping("/step")
    public void step() {
        elevatorSystem.step();
    }

    // Endpoint to set the destination floor for a specific elevator
    @PostMapping("/set-destination")
    public ResponseEntity<?> setDestination(@RequestParam int elevatorId, @RequestParam int destinationFloor) {
        if (elevatorId < elevatorSystem.elevators.size()) {
            elevatorSystem.elevators.get(elevatorId).updateDestination(destinationFloor);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid elevator ID"));
        }
    }
}