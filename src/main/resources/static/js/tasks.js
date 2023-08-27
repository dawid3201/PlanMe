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
var formOpen = false; //check if form is open
let currentFormContainer = null;

function closeForm() {
    if (currentFormContainer) {
        const openButton = currentFormContainer.nextElementSibling;
        if (openButton && openButton.classList.contains("open-form-btn")) {
            currentFormContainer.style.display = "none";
            openButton.style.display = "block";
        }
        formOpen = false;
        document.removeEventListener("click", handleClickOutsideForm);
        currentFormContainer = null;
    }
}

function handleClickOutsideForm(event) {
    if (currentFormContainer) {
        const isClickInsideForm = currentFormContainer.contains(event.target);
        if (currentFormContainer && formOpen && !currentFormContainer.contains(event.target) && event.target.className !== "open-form-btn") {
            closeForm();
        }
    }
}

function adjustInputWidth(inputElement) {
    // Adjust the input size based on its content length
    inputElement.style.width = `${inputElement.scrollWidth}px`;
}


document.querySelectorAll(".open-form-btn").forEach(btn => {
    btn.addEventListener("click", function(event) {
        event.preventDefault();
        var targetFormContainer = this.previousElementSibling;
        var barId = this.closest('.swim-lane').getAttribute("data-bar-id");

        targetFormContainer.innerHTML = formHTML;
        targetFormContainer.style.display = "block";
        this.style.display = "none";

        if (formOpen) {
            closeForm();
        }

        currentFormContainer = this.previousElementSibling;

        formOpen = true;
        document.addEventListener("click", handleClickOutsideForm);

        // Add event listener to form after it is added to the page
        targetFormContainer.querySelector("#add-task-form").addEventListener("submit", function(event) {
            event.preventDefault();
            // Prevent the default form submission behavior

            const projectId = document.getElementById('projectId').value;
            const newTaskName = document.querySelector("#new-task-name").value;
            const newPriority = document.querySelector('#task-priority').value;
            const barElement = event.target.closest('.swim-lane');
            const barPosition = barElement.getAttribute('data-bar-position');

            fetch('/project/addTask', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams({
                    'projectId': projectId,
                    'taskName': newTaskName,
                    'taskPriority' : newPriority,
                    'barId': barId
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
                    console.log('Bar Position:', barPosition);
                    location.reload();
                })
                .catch(function(error) {
                    console.log('Request failed', error);
                    console.log('Bar Position:', barPosition);
                });
        });
    });
});
function editTaskName(element) {
    var text = element.textContent;
    var id = element.getAttribute('data-task-id');
    //edit paragraph
    element.outerHTML = `<input type="text" id="input-${id}" value="${text}" maxlength="50" onblur="updateTaskName(this)" onkeydown="handleKeydown(event, this)">`;

    const inputElem = document.getElementById(`input-${id}`);
    inputElem.focus();

    // Adjust input width
    adjustInputWidth(inputElem);
}

function updateTaskName(element) {
    var newTaskName = element.value;
    var id = element.id.split('-')[1];

    // Check the length of the new description
    if (newTaskName.length > 50) {
        newTaskName = newTaskName.substring(0, 50);
    }

    fetch('/project/updateTaskName', {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({
            'taskId': id,
            'taskName': newTaskName,
        })
    })

    // change back to a paragraph
    element.outerHTML = `<p onclick="editTaskName(this)" data-task-id="${id}">${newTaskName}</p>`;
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
            element.parentElement.parentElement.parentElement.remove();
        })
        .catch((error) => {
            console.log('Request failed', error);
        });
}
//----------------------------------------------------------------TASK PRIORITY CONTENT---------------------------------

function updatePriorityVisuals(element, newPriority) {
    let taskElement = element.closest('.task');
    let priorityLabel = taskElement.querySelector('.priority-label');

    let labelClass;
    let labelText;
    switch(newPriority) {
        case '1':
            labelClass = 'priority-one';
            labelText = 'LOW';
            break;
        case '2':
            labelClass = 'priority-two';
            labelText = 'MEDIUM';
            break;
        case '3':
            labelClass = 'priority-three';
            labelText = 'HIGH';
            break;
    }

    ['priority-one', 'priority-two', 'priority-three'].forEach(cls => priorityLabel.classList.remove(cls));
    priorityLabel.classList.add(labelClass);
    priorityLabel.textContent = labelText;
}


function updateTaskPriority(dropdown) {
    var taskId = dropdown.getAttribute('data-task-id');
    var newPriority = dropdown.value;

    fetch(`/project/updateTaskPriority?taskId=${taskId}&newTaskPriority=${newPriority}`, {
        method: 'PATCH',
    })
        .then((response) => {
            if (!response.ok) throw new Error('Network response was not ok');

            let associatedPriorityElement = dropdown.previousSibling;
            if (associatedPriorityElement) {
                associatedPriorityElement.textContent = newPriority;
                updatePriorityVisuals(associatedPriorityElement, newPriority);
            }

            if (dropdown && dropdown.parentNode) {
                dropdown.remove();
            }
        })
        .catch((error) => {
            console.log('Request failed', error);
        });
}

// Event listener to remove the popup when clicked anywhere outside it
document.addEventListener('click', function(e) {
    let popup = document.querySelector('#priority-dropdown');
    if (popup && !popup.contains(e.target) && !e.target.hasAttribute('data-task-id')) {
        popup.remove();
    }
});

// This function creates the priority selector popup
function createPriorityDropdown(element) {
    if (document.getElementById('priority-dropdown')) return;

    var taskId = element.getAttribute('data-task-id');
    var originalPriority = element.textContent;

    // Hide the clicked element
    element.style.display = 'none';

    // Create a dropdown
    var dropdown = document.createElement('select');
    dropdown.id = 'priority-dropdown';
    dropdown.setAttribute('data-task-id', taskId);
    dropdown.setAttribute('data-original-priority', originalPriority);

    // Create options for the dropdown
    for (var i = 1; i <= 3; i++) {
        var option = document.createElement('option');
        option.value = i;
        option.textContent = i;
        if (i === parseInt(originalPriority)) {
            option.selected = true;
        }
        dropdown.appendChild(option);
    }

    // Add an event listener to the dropdown
    dropdown.addEventListener('change', function () {
        updateTaskPriority(this);
        element.style.display = 'block';
    });

    dropdown.addEventListener('blur', function () {
        if (this.value === this.getAttribute('data-original-priority')) {
            element.textContent = this.getAttribute('data-original-priority');
            element.style.display = 'block';
        } else {
            updateTaskPriority(this);
        }
        this.remove();
    });

    // Add the dropdown to the DOM
    element.parentNode.insertBefore(dropdown, element.nextSibling);

    // Focus on the newly created dropdown
    dropdown.focus();
}

//----------------------------------------------------------TASK DESCRIPTION CONTENT------------------------------------
let currentTaskId = null; //Store current TaskID in variable
function openDescriptionBox(element) {
    const taskId = element.getAttribute('data-task-id');
    currentTaskId = taskId; // Assuming you've defined currentTaskId globally

    const taskDescription = document.querySelector(`[data-task-id="${taskId}"] .task-description-hidden`).textContent;

    quill.root.innerHTML = taskDescription; // set the Quill editor content
    document.getElementById("taskDescriptionOverlay").style.display = "block";
}

let quill;
document.addEventListener('DOMContentLoaded', (event) => {
    const toolbarOptions = [
        ['bold', 'italic', 'underline'],
        [{ 'list': 'ordered'}, { 'list': 'bullet' }],
        [{ 'size': ['small', false, 'large', 'huge'] }],
        [{ 'font': [] }],
    ];

    quill = new Quill('#editor', {
        modules: {
            toolbar: toolbarOptions
        },
        theme: 'snow'
    });
});

function saveDescription() {
    if (!currentTaskId || currentTaskId === "undefined") {
        console.log("Task ID is not defined or invalid");
        return;
    }

    const description = quill.root.innerHTML; // get the HTML content of the editor
    saveTaskDescription(currentTaskId, description);
}
//save text on database
function saveTaskDescription(taskId, description) {
    const endpoint = '/project/addDescription';
    const data = {
        taskId: taskId,
        description: description
    };

    fetch(endpoint, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams(data).toString()
    })
        .then(response => {
            if (response.ok) {
                console.log('Description saved successfully.');
                //Update task Description imminently after Save button is pressed
                let taskElement = document.querySelector(`[data-task-id="${taskId}"] .task-description-hidden`);
                if (taskElement) {
                    taskElement.textContent = description;
                }
            } else {
                console.error('Failed to save description. Server responded with', response.status);
            }
        })
        .catch(error => {
            console.error('Error occurred while saving the description:', error);
        });
}

function closeDescriptionBox() {
    document.getElementById("taskDescriptionOverlay").style.display = "none";
    currentTaskId = null;
}
//Print name on Task textarea
function displayTaskName(taskName) {
    document.getElementById('taskNameShow').innerText = taskName;
}