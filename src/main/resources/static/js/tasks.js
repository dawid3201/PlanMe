var formHTML = `
    <form id="add-task-form">
      <input type="text" id="new-task-name" placeholder="Enter task name here" /><br>
      <label for="priority1">1</label>
      <input type="radio" id="priority1" name="priority" value="1" />
      <label for="priority2">2</label>
      <input type="radio" id="priority2" name="priority" value="2" />
      <label for="priority3">3</label>
      <input type="radio" id="priority3" name="priority" value="3" />
      <input type="submit" value="Add Task" />
    </form>
  `;

var formOpen = false;

// Get all task elements
var tasks = document.querySelectorAll('.task');

// Add event listeners for the drag start and end events
tasks.forEach(function(task) {
    task.addEventListener('dragstart', function() {
        this.classList.add('is-dragging');
    });

    task.addEventListener('dragend', function() {
        this.classList.remove('is-dragging');
    });
});
function closeForm() {
    document.getElementById("form-container").style.display = "none";
    document.getElementById("open-form-btn").style.display = "block";
    formOpen = false;
    document.removeEventListener("click", handleClickOutsideForm);
}

function handleClickOutsideForm(event) {
    var formContainer = document.getElementById("form-container");
    var isClickInsideForm = formContainer.contains(event.target);

    if (formOpen && !isClickInsideForm && event.target.id !== "open-form-btn") {
        closeForm();
    }
}

document.getElementById("open-form-btn").addEventListener("click", function(event) {
    // Prevent the default link behavior
    event.preventDefault();

    // Insert form HTML into div.
    document.getElementById("form-container").innerHTML = formHTML;
    document.getElementById("form-container").style.display = "block";
    document.getElementById("open-form-btn").style.display = "none";

    formOpen = true;
    // Add event listener for clicking outside of form
    document.addEventListener("click", handleClickOutsideForm);

    // Add event listener to form after it is added to the page.
    document.getElementById("add-task-form").addEventListener("submit", function(event) {
        event.preventDefault(); // Prevent the default form submission behavior

        var projectId = document.getElementById('projectId').value;
        var newTaskName = document.querySelector("#new-task-name").value;
        var newPriority = document.querySelector('input[name="priority"]:checked').value;

        fetch('/project/addTask', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({
                'projectId': projectId,
                'taskDescription': newTaskName,
                'taskPriority' : newPriority
            })
        })
            .then(function(response) {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.text();
            })
            .then(function(text) {
                console.log('Request successful', text);
                location.reload(); // reload the page to see the new task
            })
            .catch(function(error) {
                console.log('Request failed', error);
            });
    });
});
function editTaskDescription(element) {
    var text = element.textContent;
    var id = element.getAttribute('data-task-id');

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
    element.outerHTML = `<p onclick="editTaskDescription(this)" data-task-id="${id}">${newDescription}</p>`;
}
function handleKeydown(event, element) {
    // 13 is the key code for the Enter key
    if (event.keyCode === 13) {
        // prevent the default action, update the task description, and blur the input field
        event.preventDefault();
        updateTaskDescription(element);
        element.blur();
    }
}
function deleteTask(element) {
    var taskId = element.getAttribute('data-task-id');
    fetch("/project/removeTask?taskId=" + taskId, {
        method: 'DELETE',
    })
        .then((response) => {
            if (!response.ok) throw new Error('Network response was not ok');
            // Successfully deleted from database, now remove from HTML
            element.parentElement.remove();
        })
        .catch((error) => {
            console.log('Request failed', error);
        });
}



