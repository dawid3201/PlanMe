// CHECK how to update FROM with AJAX call <----------------------------------------------------------------------------
var formHTML = `
    <form id="add-task-form">
    <input type="text" id="new-task-name" placeholder="Issue name" />
    <div id="priority-section">
        <label for="task-priority"></label>
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
                    'taskPriority': newPriority,
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
                    if (text === "Bar does not exist") {
                        // Display a message on the user's screen for bar not existing
                        alert("Bar does not exist");
                    } else if (text === "Project does not exist") {
                        // Display a message on the user's screen for project not existing
                        alert("Project does not exist");
                    } else {
                        console.log('Request successful', text);
                        console.log('Bar Position:', barPosition);
                        location.reload();
                    }
                })
                .catch(function(xhr, status, error) {
                    if (xhr.status === 500) { // HTTP status code for Conflict
                        alert("Task name cannot be empty.");
                    } else {
                        // Handle other error cases
                        console.error("An error occurred:", status, error);
                    }
                });
        });
    });
});

function editTaskName(element) {
    var text = element.textContent;
    var id = element.getAttribute('data-task-id');

    // Change paragraph to an editable input field
    element.outerHTML = `<input type="text" id="input-${id}" value="${text}" maxlength="50" onblur="updateTaskName(this)" onkeydown="handleKeydown(event, this)">`;


    // Focus on the newly created input field
    const inputElem = document.getElementById(`input-${id}`);
    inputElem.focus();

    // Adjust input width
    adjustInputWidth(inputElem);
}

function updateTaskName(element) {
    var taskName = element.value;
    var id = element.id.split('-')[1];

    // Check the length of the new description
    if (taskName.length > 50) {
        taskName = taskName.substring(0, 50);
    }

    fetch('/project/updateTaskName', {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({
            'taskId': id,
            'taskName': taskName,
        })
    }).then((response) => {
        if (!response.ok) throw new Error('Network response was not ok');
        // Successfully deleted from database, now remove from HTML
        element.outerHTML = `<p onclick="editTaskName(this)" data-task-id="${id}">${taskName}</p>`;
    })
        .catch((error) => {
            console.log('Request failed', error);
        });

    // change back to a paragraph
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
    var taskElement = document.querySelector(`div[data-task-id="${taskId}"]`);
    var taskPosition = taskElement ? taskElement.getAttribute('data-task-position') : null;

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
    let taskId = element.getAttribute('data-task-id');
    console.log("Task ID in openDescriptionBox:", taskId);  // Log here
    currentTaskId = taskId;

    const taskDescription = document.querySelector(`[data-task-id="${taskId}"] .task-description-hidden`);

    if (taskDescription) {
        quill.root.innerHTML = taskDescription.textContent; // set the Quill editor content
    } else {
        console.error("Failed to find the task description element for task ID:", taskId);
    }
    document.getElementById("taskNameShow").setAttribute('data-task-id', taskId);
    displayTaskName(taskId);

    document.getElementById("taskDescriptionOverlay").style.display = "block";
}
function displayTaskName(taskId) {
    console.log("Task ID:", taskId);
    fetch(`/getTaskName/${taskId}`)
        .then(response => {
            if (response.ok) {
                return response.text();
            } else {
                throw new Error('Failed to fetch task name');
            }
        })
        .then(taskName => {
            document.getElementById("taskNameShow").textContent = taskName;
        })
        .catch(error => {
            console.error('Error:', error);
        });
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

//---------------------------------------------ASSIGN-USER-TO-TASK------------------------------------------------------
function toggleUserDropdown(element) {
    const dropdown = element.nextElementSibling;
    dropdown.style.display = dropdown.style.display === "none" ? "block" : "none";
}
function assignUserToTask(userElement, taskId) {
    const userEmail = encodeURIComponent(userElement.textContent.trim());


    fetch(`/project/assignUserToTask?userEmail=${userEmail}&taskId=${taskId}`, {
        method: 'PATCH',
    })
        .then((response) => {
            if(!response.ok) throw new Error('Network response was not ok');
            console.log('Task assigned successfully to: ');

        })
        .catch((error) => {
            console.log('Request failed', error);
        });
}
//------------------------------------------------------DISPLAY-ASSIGN-TASKS--------------------------------------------
function getAssignedTasks() {
    const userEmail = document.getElementById('userEmail').value;
    fetch(`/project/ListOfTasks?userEmail=${userEmail}`)
        .then(response => {
            if (response.ok) {
                return response.text();
            } else {
                throw new Error('Failed to fetch task name');
            }
        })
        .then(assignTasks => {
            document.getElementById("assignedTasks").textContent = assignTasks;
            document.getElementById("assignedTasks").style.whiteSpace = "pre-line"; // Set white-space property
        })
        .catch(error => {
            console.error('Error:', error);
        });
}
// Function to toggle the list of members
function toggleAssignTasks() {
    const membersList = document.getElementById("listOfAssignedTasks");
    const toggleMembers = document.getElementById("seeAssignTasks");

    if (membersList.classList.contains("hidden")) {
        membersList.classList.remove("hidden");
        toggleMembers.classList.add("closed");
        getAssignedTasks(); // Fetch and display the list of members
    } else {
        membersList.classList.add("hidden");
        toggleMembers.classList.remove("closed");
    }
}

// Add event listener to toggle the list
document.getElementById("seeAssignTasks").addEventListener("click", toggleAssignTasks);

// Initialize the initial state as hidden
document.addEventListener("DOMContentLoaded", function () {
    const membersList = document.getElementById("listOfAssignedTasks");
    const toggleMembers = document.getElementById("seeAssignTasks");

    membersList.classList.add("hidden");
    toggleMembers.classList.remove("closed");
});




















































//---------------------------------------------SEARCH-FOR-A-TASK--------------------------------------------------------
function searchTask() {
    const projectId = document.getElementById("projectId").value;
    const taskName = document.getElementById("search-task-name").value.toLowerCase(); // Convert to lowercase

    fetch(`/project/findTask?projectId=${projectId}&taskName=${taskName}`, {
        method: 'GET',
    })
        .then((response) => response.json())
        .then((data) => {
            console.log("Data received from server: ", data);
            hideNotFoundTask(data); // Pass the entire data
        })
        .catch((error) => {
            console.log('Request failed', error);
        });
}
function hideNotFoundTask(taskData) {
    const tasks = $('.task');
    tasks.each(function() {
        const currentTaskId = $(this).data('task-id');
        if (taskData[currentTaskId]) {
            console.log('Task ID found in response data', currentTaskId);
            $(this).show();
        } else {
            console.log('Task ID NOT found in response data', currentTaskId);
            $(this).hide();
        }
    });
}
function clearAndSearch() {
    document.getElementById("search-task-name").value = "";
    searchTask();
}
//--------------------------------------------------------WEB-SOCKET-SCRIPT---------------------------------------------
// bar.js

// Function to fetch bars from the server
function fetchBarsFromServer() {
    const projectId = document.getElementById('projectId').value; // Get the project ID from the hidden input field

    // Make an AJAX GET request to your server's endpoint
    fetch(`/project/getUpdatedBars/${projectId}`)
        .then((response) => response.json())
        .then((data) => {
            // Process the data and update the DOM elements
            updateDOMWithData(data);
        })
        .catch((error) => {
            console.error('Error fetching bars:', error);
        });
}
//---------------------------PROBLEM
//The only thing that is updated is name
function updateDOMWithData(data) {
    // Assuming you have a div with the class 'swim-lane' for each bar
    const barList = document.getElementsByClassName('swim-lane');

    // Loop through each swim-lane and update its content
    for (let i = 0; i <= barList.length; i++) {
        const barElement = document.createElement('div');
        barElement.textContent = data[i].name; // Assuming data structure matches the order

        // Clear the existing content
        barList[i].innerHTML = '';

        // Append the bar element to the container
        barList[i].appendChild(barElement);
    }
}




//setInterval(initializeSortableSwimLanes, 60000); // Update every minute (adjust as needed)






