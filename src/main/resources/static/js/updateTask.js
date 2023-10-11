//Sortable function, does not update DOM
function initializeSortableSwimLanes() {
    $(".swim-lane").sortable({
        items: ".task",
        placeholder: "task-placeholder",
        connectWith: ".swim-lane",
        forcePlaceholderSize: true, // Ensure empty swim lanes have a placeholder
        update: function(event, ui) {
            let taskId = ui.item.data("task-id");
            let barId = ui.item.closest('.swim-lane').data("bar-id");
            let newPosition = ui.item.index(".swim-lane[data-bar-id='" + barId + "'] .task") + 1;
            console.log("Calculated new position:", newPosition);
            $.ajax({
                type: "PATCH",
                url: "/project/updateTaskPosition",
                cache: false,
                data: {
                    taskId: taskId,
                    newPosition: newPosition,
                    barId: barId
                },
                success: function() {
                    console.log('Task position updated successfully.');
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    console.error('Error updating task position:', textStatus, errorThrown);
                }
            });
        }
    });
}

$(document).ready(initializeSortableSwimLanes);

//solution to the bad position was to get all tasks from specific bar instead of all tasks
//Before: there were 4 tasks and 3 bars: A, B and C. A = 1 task, B = 2 tasks and C = 1 task.
//When I added new task to the C its position was 5 instead of 2
//After: I added new task to C and its positoin is 2 not 5