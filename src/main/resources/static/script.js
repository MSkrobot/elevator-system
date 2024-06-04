let currentElevatorId = null;
let selectedFloor = null;

document.addEventListener('DOMContentLoaded', function() {
    // 1. Initialize the elevator elements and their animations
    const elevatorContainer = document.getElementById('elevator-container');
    const totalElevators = 16;
    for (let i = 0; i < totalElevators; i++) {
        const elevator = document.createElement('div');
        elevator.id = `elevator-${i}`;
        elevator.className = 'elevator';
        const transitionTime = 0.4 + (15 - i) * 0.06;
        elevator.style.transition = `transform ${transitionTime}s ease-out`;
        elevatorContainer.appendChild(elevator);
    }

    // 2. Initialize the floor labels
    const labelsContainer = document.getElementById('floor-labels');
    for (let i = 9; i >= 0; i--) {
        const label = document.createElement('div');
        label.className = 'floor-label';
        label.textContent = `${i}`;
        labelsContainer.appendChild(label);
    }

    // 3. Initialize the floor buttons for requesting pickup
    const buttonsContainer = document.getElementById('floor-buttons');
    for (let i = 9; i >= 0; i--) {
        const floorContainer = document.createElement('div');
        floorContainer.className = 'floor-container';

        if(i ===  9){
            const buttonDown = createArrowButton(i, -1);
            floorContainer.appendChild(buttonDown);
        }
        else if(i === 0){
            const buttonUp = createArrowButton(i, 1);
            floorContainer.appendChild(buttonUp);
        }
        else{
            const buttonUp = createArrowButton(i, 1);
            floorContainer.appendChild(buttonUp);

            const buttonDown = createArrowButton(i, -1);
            floorContainer.appendChild(buttonDown);
        }
        buttonsContainer.appendChild(floorContainer);
    }

    // 4. Initialize the destination buttons for selecting a floor
    const destinationContainer = document.getElementById('destination-buttons');
    for (let i = 9; i >= 0; i -= 2) {
        const row = document.createElement('div');
        row.className = 'button-row';

        [i, i - 1].forEach(num => {
            if (num >= 0) {
                const button = document.createElement('button');
                button.textContent = num;
                button.onclick = function() { selectFloor(num); setDestination(); };
                row.appendChild(button);
            }
        });

        destinationContainer.appendChild(row);
    }

    // Refresh the status of elevators
    refreshStatus();
});

// Creates an arrow button for requesting elevator pickup
function createArrowButton(floor, direction) {
    const button = document.createElement('button');
    button.className = direction === 1 ? 'button-up' : 'button-down';
    button.style.flex = "1";
    button.onclick = function() { requestPickup(floor, direction); };
    return button;
}

// Sends a request to move the system one step forward
function step() {
    fetch('/api/elevators/step', { method: 'POST' })
        .then(() => {
            refreshStatus();
        })
        .catch(error => console.error('Error: ', error));
}

// Refreshes the status of all elevators and updates the UI
function refreshStatus() {
    fetch('http://localhost:8080/api/elevators/status')
        .then(response => response.json())
        .then(data => {
            data.forEach(elevator => {
                const el = document.getElementById(`elevator-${elevator.id}`);
                if (el) {
                    const moveAmount = (9 - elevator.currentFloor) * 50;
                    el.style.transform = `translateY(${moveAmount}px)`;
                    el.onclick = () => selectElevator(elevator.id);
                } else {
                    console.error('Element not found:', `elevator-${elevator.id}`);
                }
            });
        })
        .catch(error => console.error('Error fetching status:', error));
}

// Logs the status of all elevators to the console
function logElevators() {
    fetch('http://localhost:8080/api/elevators/status')
        .then(response => response.json())
        .then(data => {
            data.forEach(elevator => {
                console.log(`Elevator: ${elevator.id + 1}, floor: ${elevator.currentFloor}, destinations: `, elevator.allDestinations);
            });
        })
        .catch(error => console.error('Error logging elevator destinations:', error));
}

// Selects an elevator and highlights it in the UI
function selectElevator(elevatorId) {
    currentElevatorId = elevatorId;
    const label = document.getElementById('destination-floor-label');
    if (label) {
        label.textContent = `Select floor for Elevator ${elevatorId + 1}`;
    }

    document.querySelectorAll('.elevator').forEach(e => e.classList.remove('highlight'));
    const selectedElevator = document.getElementById(`elevator-${elevatorId}`);
    if (selectedElevator) {
        selectedElevator.classList.add('highlight');
    }

    console.log(`Elevator ${elevatorId + 1} selected as current elevator.`);
    refreshStatus();
}

// Sends a pickup request to the server for a specific floor and direction
function requestPickup(floor, direction) {
    fetch(`http://localhost:8080/api/elevators/pickup?floor=${floor}&direction=${direction}`, {
        method: 'POST'
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                response.text().then(text => { throw new Error(text); });
            }
        })
        .then(data => {
            if (data && data.elevatorId !== undefined) {
                currentElevatorId = data.elevatorId;
                selectElevator(currentElevatorId);
                console.log('Pickup request successful for floor', floor, 'with direction', direction, 'taken by elevator', currentElevatorId + 1);
                refreshStatus();
            } else {
                throw new Error('Elevator ID was not returned');
            }
        })
        .catch(error => console.error('Error:', error));
}

// Selects a floor to set as the destination for the current elevator
function selectFloor(floor) {
    selectedFloor = floor;
    console.log("Selected floor: " + floor);
    document.getElementById('destination-floor-label').textContent = `Floor Selected: ${floor}`;
}

// Sets the destination floor for the current elevator
function setDestination() {
    if (currentElevatorId == null) {
        alert('Please call an elevator first.');
        return;
    }
    if (selectedFloor === null) {
        alert("Please select a floor first.");
        return;
    }
    fetch(`http://localhost:8080/api/elevators/set-destination?elevatorId=${currentElevatorId}&destinationFloor=${selectedFloor}`, {
        method: 'POST'
    })
        .then(response => {
            if (response.ok) {
                console.log('Destination set successfully for elevator ', currentElevatorId + 1);
                refreshStatus();
            } else {
                throw new Error('Failed to set destination');
            }
        })
        .catch(error => console.error('Error:', error));
}