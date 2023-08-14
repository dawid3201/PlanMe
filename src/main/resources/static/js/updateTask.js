$(document).ready(function() {
    $(".swim-lane").sortable({
        items: ".task",
        placeholder: "task-placeholder",
        connectWith: ".swim-lane",
        update: function(event, ui) {
            let taskId = ui.item.data("task-id");
            let newPosition = ui.item.index(".task") + 1;
            let barId = ui.item.closest('.swim-lane').data("bar-id");
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
});
