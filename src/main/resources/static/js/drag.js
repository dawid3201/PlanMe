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

        const bottomTask = insertAboveTask(zone, e.clientY);
        const curTask = document.querySelector(".is-dragging");


        if (!bottomTask) {
            zone.appendChild(curTask);
        } else {
            zone.insertBefore(curTask, bottomTask);
        }

        const taskId = curTask.getAttribute('data-id'); // assuming each task has a data-id attribute
        const newState = zone.id; // this function should return the new state based on the zone

        // Post the changes to the server
        fetch('/project/updateTaskSwimlane?taskId=' + taskId + '&newSwimlane=' + newState, {
            method: 'PATCH'
        })
            .then(function(response) {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(function(text) {
                console.log('Request successful', text);
            })
            .catch(function(error) {
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




