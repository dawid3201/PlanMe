//JQuare library and sortable
function moveTask() {
    $(".swim-lane").sortable({
        items: ".task",
        placeholder: "task-placeholder",
        connectWith: ".swim-lane",
        forcePlaceholderSize: true, // Ensure empty swim lanes have a placeholder
        update: function(event, ui) {
            let taskId = ui.item.data("task-id");
            let barId = ui.item.closest('.swim-lane').data("bar-id");
            let newPosition = ui.item.index(".swim-lane[data-bar-id='" + barId + "'] .task") + 1;
            fetch(`/project/updateTaskPosition?taskId=${taskId}&newPosition=${newPosition}&barId=${barId}`,{
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({
                    'taskId': taskId,
                    'newPosition': newPosition,
                    'barId': barId,
                })
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(response.statusText);
                    }
                })
                .then(() => {
                    ui.item.attr("data-bar-id", barId);
                })
                .catch(error => {
                    console.error("Error updating task position: ", error);
                })
        }
    });
}
$(document).ready(moveTask);

//solution to the bad position was to get all tasks from specific bar instead of all tasks
//Before: there were 4 tasks and 3 bars: A, B and C. A = 1 task, B = 2 tasks and C = 1 task.
//When I added new task to the C its position was 5 instead of 2
//After: I added new task to C and its positoin is 2 not 5
function updateTaskElement(notification) {
    const taskUpdateMessage = JSON.parse(notification.body);

    // Find the task element by taskId and update data attributes
    const taskElement = $(`.task[data-task-id='${taskUpdateMessage.taskId}']`);
    taskElement.attr("data-bar-id", taskUpdateMessage.barId);
    taskElement.attr("data-task-position", taskUpdateMessage.newPosition);

    // Update any other DOM elements related to the task if needed

    // You can also update the visual representation of the task element here if necessary
}