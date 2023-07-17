var formHTML = `
    <form id="add-task-form">
    <input type="text" id="new-task-name" placeholder="Issue name" />
    <div id="priority-section">
        <label for="priority"></label>
        <select id="task-priority" name="priority">
            <option value="1">1</option>
            <option value="2">2</option>
            <option value="3">3</option>
        </select>
        <button type="submit" class="submit-task-btn"><i class="fas fa-check"></i></button>
    </div>

   
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
        var newPriority = document.querySelector('#task-priority').value;


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
    element.outerHTML = `<input type="text" id="input-${id}" value="${text}" onblur="updateTaskName(this)" onkeydown="handleKeydown(event, this)">`;
}
function updateTaskName(element) {
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
        updateTaskName(element);
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
// This function sends a PATCH request to the server to update the task's priority
function updateTaskPriority(dropdown) {
    var taskId = dropdown.getAttribute('data-task-id');
    var newPriority = dropdown.value;

    fetch(`/project/updateTaskPriority?taskId=${taskId}&newTaskPriority=${newPriority}`, {
        method: 'PATCH',
    })
        .then((response) => {
            if (!response.ok) throw new Error('Network response was not ok');

            // Update the task priority in the HTML and remove the dropdown
            dropdown.previousSibling.textContent = newPriority;
            dropdown.remove();
        })
        .catch((error) => {
            console.log('Request failed', error);
        });
}
// Event listener to remove the popup when clicked anywhere outside it
document.addEventListener('click', function(e) {
    let popup = document.querySelector('.priority-selector');
    if (popup) {
        popup.remove();
    }
});
// This function creates the priority selector popup
function createPriorityDropdown(element) {
    // Check if a dropdown already exists
    if (document.getElementById('priority-dropdown')) return;

    var taskId = element.getAttribute('data-task-id');


    // Create a dropdown
    var dropdown = document.createElement('select');
    dropdown.id = 'priority-dropdown';
    dropdown.setAttribute('data-task-id', taskId);

    // Create options for the dropdown
    for (var i = 0; i <= 3; i++) {
        var option = document.createElement('option');
        option.value = i;
        option.textContent = i;
        dropdown.appendChild(option);
    }

    // Add an event listener to the dropdown
    dropdown.addEventListener('change', function() {
        updateTaskPriority(this);
    });

    // Add the dropdown to the DOM
    element.parentNode.insertBefore(dropdown, element.nextSibling);
}
