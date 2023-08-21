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

function openTaskForm(element) {
    let barId = $(element).closest('.swim-lane').data("bar-id");
    // Now, store this barId somewhere where your form can access it,
    // e.g., in a hidden input field inside your form

    // Assuming you have an input field like:
    // <input type="hidden" id="selectedBarId" name="barId">
    $('#selectedBarId').val(barId);

    // You can now show the form for the user to fill in the task details
    $('#form-container').show();
}



function closeForm(targetFormContainer) {
    targetFormContainer.style.display = "none";
    targetFormContainer.nextElementSibling.style.display = "block";
    formOpen = false;
    document.removeEventListener("click", handleClickOutsideForm);
}

function handleClickOutsideForm(event) {
    var formContainers = document.querySelectorAll(".form-container");
    formContainers.forEach(formContainer => {
        var isClickInsideForm = formContainer.contains(event.target);
        var associatedBtn = formContainer.nextElementSibling;

        if (formOpen && !isClickInsideForm && event.target !== associatedBtn) {
            closeForm(formContainer);
        }
    });
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
                    'taskDescription': newTaskName,
                    'taskPriority' : newPriority,
                    'barId': barId // Pass the bar ID to the server
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
                    location.reload(); // reload the page to see the new task
                })
                .catch(function(error) {
                    console.log('Request failed', error);
                    console.log('Bar Position:', barPosition);
                });
        });
    });
});

function editTaskDescription(element) {
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
    var newDescription = element.value;
    var id = element.id.split('-')[1];

    // Check the length of the new description
    if (newDescription.length > 50) {
        newDescription = newDescription.substring(0, 50);
    }

    fetch('/project/updateTaskName', {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: new URLSearchParams({
            'taskId': id,
            'taskDescription': newDescription,
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
            element.parentElement.parentElement.parentElement.remove();
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



