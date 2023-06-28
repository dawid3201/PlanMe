
function editTaskDescription(element) {
    var text = element.textContent;
    var id = element.getAttribute('data-id');

    // change paragraph to an editable input field
    element.outerHTML = `<input type="text" id="input-${id}" value="${text}" onblur="updateTaskDescription(this)" onkeydown="handleKeydown(event, this)">`;
}
function updateTaskDescription(element) {
    var newDescription = element.value; // this is the new task name
    var id = element.id.split('-')[1];

    fetch('/project/updateTaskName', {
        method: 'PATCH', // use PATCH method here
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({
            'taskId': id, // pass taskId here
            'taskDescription': newDescription, // pass new task name here
        })
    })

    // change back to a paragraph
    element.outerHTML = `<p onclick="editTaskDescription(this)" data-id="${id}">${newDescription}</p>`;
}
function handleKeydown(event, element) {
    // 13 is the key code for the Enter key
    if (event.keyCode == 13) {
        // prevent the default action, update the task description, and blur the input field
        event.preventDefault();
        updateTaskDescription(element);
        element.blur();
    }
}
function deleteTask(element) {
    var taskId = element.getAttribute('data-id');
    var xhr = new XMLHttpRequest();
    xhr.open("DELETE", "/project/removeTask?taskId=" + taskId, true);
    xhr.onreadystatechange = function () {
        if (this.readyState === XMLHttpRequest.DONE && this.status === 200) {
            // Successfully deleted from database, now remove from HTML
            element.parentElement.remove();
        }
    }
    xhr.send();
}