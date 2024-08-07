//************************************************HANDLE ADD TASK FORMS AND ADD TASKS TO THE PROJECT************
var formOpen = false; // Check if form is open
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
    if (currentFormContainer && formOpen && !currentFormContainer.contains(event.target) && event.target.className !== "open-form-btn") {
        closeForm();
    }
}

document.querySelectorAll(".open-form-btn").forEach(btn => {
    btn.addEventListener("click", function(event) {
        event.preventDefault();
        if(formOpen){
            closeForm();
        }

        const targetFormContainer = this.previousElementSibling;
       // const barId = this.closest('.swim-lane').getAttribute("data-bar-id");

        targetFormContainer.style.display = "block";
        this.style.display = "none";

        currentFormContainer = targetFormContainer;

        if (!formOpen) {
            formOpen = true;
            document.addEventListener("click", handleClickOutsideForm);

            // Remove previous event listener from submit button
            const submitButton = targetFormContainer.querySelector(".submit-task-btn");
            submitButton.removeEventListener('click', handleSubmit);

            // Add event listener to submit button instead of form submission
            submitButton.addEventListener("click", handleSubmit);
        }
    });
});
function handleSubmit(event) {
    event.preventDefault(); // Prevent the default form submission behavior

    const form = currentFormContainer.querySelector("#add-task-form"); // Access the form from the container
    const taskName = form.querySelector("#new-task-name").value;
    const priority = form.querySelector('#task-priority').value;
    const barId = currentFormContainer.closest('.swim-lane').getAttribute("data-bar-id");



    fetch('/task/addTask', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({
            'taskName': taskName,
            'priority': priority,
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
                alert("Bar does not exist");
            } else if (text === "Project does not exist") {
                alert("Project does not exist");
            } else {
                console.log('Request successful', text);
                location.reload();
            }
        })
        .catch(function(error) {
            console.error("An error occurred:", error);
        });
}

function adjustInputWidth(inputElement) {
    // Adjust the input size based on its content length
    inputElement.style.width = `${inputElement.scrollWidth}px`;
}
//***************************EDIT TASK NAME*******************************
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
//******************************UPDATE TASK NAME************************
function updateTaskName(element) {
    let taskName = element.value;
    let id = element.id.split('-')[1];

    // Check the length of the new name
    if (taskName.length > 50) {
        taskName = taskName.substring(0, 50);
    }

    if (taskName.trim() !== "") {
        fetch('/task/updateTaskName', {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({
                'taskId': id,
                'taskName': taskName,
            })
        }).then((response) => {
            if (!response.ok) {throw new Error(response.statusText);}

            // Successfully updated in the database, now update HTML content
            element.outerHTML = `<p onclick="editTaskName(this)" data-task-id="${id}">${taskName}</p>`;
        })
    }
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
    fetch("/task/removeTask?taskId=" + taskId, {
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

    fetch(`/task/updateTaskPriority?taskId=${taskId}&newTaskPriority=${newPriority}`, {
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
document.addEventListener('click', function(event) {
    let popup = document.querySelector('#priority-dropdown');
    if (popup && !popup.contains(event.target) && !event.target.hasAttribute('data-task-id')) {
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

    // Position of the dropdown
    dropdown.style.position = 'absolute';
    dropdown.style.top = '33px';
    dropdown.style.right = '1px';

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
    fetch(`/task/getTaskName/${taskId}`)
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
document.addEventListener('DOMContentLoaded', () => { //event was in the brackets
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
    const endpoint = '/task/addDescription';
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
//TODO: dropdown does not hide when user clicks on anything else on the page
document.addEventListener('click', function(event) {
    const dropdowns = document.querySelectorAll('.user-dropdown');

    dropdowns.forEach(function(dropdown) {
        if (dropdown.style.display === "block" && !dropdown.contains(event.target)) {
            dropdown.style.display = "none";
        }
    });
});
function assignUserToTask(userElement, taskId) {
    const userEmail = encodeURIComponent(userElement.textContent.trim());

    fetch(`/member/assignUserToTask?userEmail=${userEmail}&taskId=${taskId}`, {
        method: 'PATCH',
    })
        .then((response) => {
            if(!response.ok){
                if(response.status === 500){
                    alert("Conflict: User " + decodeURIComponent(userEmail) + " is already assign to this task");
                } else {
                    throw new Error('Network response was not ok');
                }
            }
            console.log('Task assigned successfully to: ');

            const dropdown = userElement.closest('.user-dropdown');
            toggleUserDropdown(dropdown.querySelector('i'));

        })
        .catch((error) => {
            console.log('Request failed', error);
        });
}
//------------------------------------------------------DISPLAY-ASSIGN-TASKS--------------------------------------------
function getAssignedTasks() {
    const userEmail = document.getElementById('userEmail').value;
    const projectId = document.getElementById('projectId').value;

    fetch(`/task/ListOfTasks?userEmail=${userEmail}&projectId=${projectId}`)
        .then(response => {
            if (response.ok) {
                return response.text();
            } else {
                throw new Error('Failed to fetch task name');
            }
        })
        .then(assignTasks => {
            const taskListP = document.getElementById("assignedTasks");
            taskListP.style.whiteSpace = "pre-line"; // Set white-space property

            if(assignTasks.trim() === ''){
                taskListP.textContent = "No tasks assigned to you.";
            }else{
                // Separate tasks
                const taskNames = assignTasks.split('\n').filter(name => name.trim() !== '');
                taskListP.textContent = taskNames.map(name => `${name}\n-------------------------------`).join('\n');
            }
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
let isSliderOn = false;
function searchTask() {
    const projectId = document.getElementById("projectId").value;
    const taskName = document.getElementById("search-task-name").value.toLowerCase(); // Convert to lowercase

    fetch(`/task/findTask?projectId=${projectId}&taskName=${taskName}`, {
        method: 'GET',
    })
        .then((response) => response.json())
        .then((data) => {
            console.log("Data received from server: ", data);
            if(isSliderOn){
                showAllTasksForUserEmail();
            }else{
                hideNotFoundTasks(data);
            }
        })
        .catch((error) => {
            console.log('Request failed', error);
        });
}
function getTasksByUserEmail() {
    const projectId = document.getElementById("projectId").value;
    const userEmail = document.getElementById("userEmail").value;
    const showMyTasksSwitch = document.getElementById("showMyTasksSwitch");
    isSliderOn = true;
    if (showMyTasksSwitch.checked) {
        fetch(`/task/getTaskByUser?projectId=${projectId}&userEmail=${userEmail}`, {
            method: 'GET',
        })
            .then((response) => response.json())
            .then((data) => {
                console.log("Data received from server: ", data);
                hideNotFoundTasks(data); // Hide other tasks
            })
            .catch((error) => {
                console.log('Request failed', error);
            });
    } else {
        isSliderOn = false;
        showAllTasks();
    }
}

function hideNotFoundTasks(taskData) {
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
function showAllTasks() {
    const tasks = $('.task');
    tasks.each(function() {
        $(this).show();
    });
}
function showAllTasksForUserEmail() {
    const projectId = document.getElementById("projectId").value;
    const userEmail = document.getElementById("userEmail").value;

    fetch(`/task/getTaskByUser?projectId=${projectId}&userEmail=${userEmail}`, {
        method: 'GET',
    })
        .then((response) => response.json())
        .then((data) => {
            console.log("Data received from server: ", data);
        })
        .catch((error) => {
            console.log('Request failed', error);
        });
}


function clearAndSearch() {
    document.getElementById("search-task-name").value = "";
    searchTask();
}
function handleKeyPress(event){
    if(event.keyCode === 13){
        searchTask();
        return false; //stops defaults page refreshing
    }
}