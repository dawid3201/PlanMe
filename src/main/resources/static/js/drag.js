const draggables = document.querySelectorAll(".task");
const droppables = document.querySelectorAll(".swim-lane");

draggables.forEach((task) => {
    task.addEventListener("dragstart", () => {
        task.classList.add("is-dragging");
    });

    task.addEventListener("dragend", () => {
        task.classList.remove("is-dragging");
    });
});

droppables.forEach((zone) => {
    zone.addEventListener("dragover", (e) => {
        e.preventDefault();

        const draggableTask = insertAboveTask(zone, e.clientY);
        const curTask = document.querySelector(".is-dragging");

        if (!draggableTask) {
            zone.appendChild(curTask);
        } else {
            zone.insertBefore(curTask, draggableTask);
        }

        const taskId = curTask.getAttribute('data-task-id');
        const tasks = Array.from(zone.querySelectorAll(".task"));
        const newPosition = tasks.indexOf(curTask) + 1; // +1 because positions start from 1
        const barId = zone.getAttribute('data-bar-id');
        const newState = zone.id;

        // Post the changes to the server to update the swimlane
        fetch(`/project/updateTaskSwimlane?taskId=${taskId}&newSwimlane=${newState}&barId=${barId}`, {
            method: 'PATCH'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(text => {
                console.log('Request successful', text);

                // Post the changes to the server to update the position
                return fetch(`/project/updateTaskPosition?taskId=${taskId}&newPosition=${newPosition}`, {
                    method: 'PATCH'
                });
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(text => {
                console.log('Request successful', text);
                location.reload(); // add this line to refresh the page
            })
            .catch(error => {
                console.log('Request failed', error);
            });
    });
});

const insertAboveTask = (zone, mouseY) => {
    const els = zone.querySelectorAll(".task:not(.is-dragging)");

    let closestTask = null;
    let closestOffset = Number.NEGATIVE_INFINITY;

    els.forEach((task) => {
        const { top } = task.getBoundingClientRect();

        const offset = mouseY - top;

        if (offset < 0 && offset > closestOffset) {
            closestOffset = offset;
            closestTask = task;
        }
    });
    return closestTask;
};




