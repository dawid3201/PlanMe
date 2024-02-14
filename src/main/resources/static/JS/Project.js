//Member form
document.getElementById("add-member-form").addEventListener("submit", function(event) {
    event.preventDefault();

    var projectId = document.getElementById('projectId').value;
    var newMemberEmail = document.getElementById('new-member-email').value;

    $.ajax({
        url: "/project/addMember",
        type: "POST",
        data: { projectId: projectId, memberEmail: newMemberEmail },
        success: function(response) {
            location.reload(); // Reload the page to reflect changes
        },
        error: function(error) {
            console.error("Error adding new member: ", error);
        }
    });
});
function deleteProject(element) {
    const projectId = element.getAttribute('data-project-id');
    fetch("/project/deleteProject?projectId=" + projectId, {
        method: 'DELETE',
    })
        .then((response) => {
            if (!response.ok) throw new Error('Network response was not ok');
            // Successfully deleted from database, now remove from HTML
            element.parentElement.parentElement.remove();
            window.location.href = '/homepage';
        })
        .catch((error) => {
            console.log('Request failed', error);
        });
}
function activateUpdateProjectName(element) {
    var text = element.textContent;
    var id = element.getAttribute('ProjectDetailName'); // Use 'ProjectDetailName' as the attribute name

    element.setAttribute('data-original-text', text);

    // Change h2 to a styled editable input field
    element.innerHTML = `<input type="text" id="input-${id}" class="editable-input" value="${text}" onblur="updateProjectName(this)" onkeydown="handleKeydownBar(event, this)" onclick="event.stopPropagation()">`;

    // Immediately focus the input field to prevent needing another click
    document.getElementById(`input-${id}`).focus();
}
function updateProjectName(element){
    let newProjectName = element.value;
    let projectId = document.getElementById('projectId').value;
    let parentElement = element.parentElement;
    let originalText = parentElement.getAttribute('data-original-text');

    if (newProjectName !== originalText || newProjectName.trim() !== "") {
        fetch('/project/updateProjectName', {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({
                'projectId': projectId,
                'newName': newProjectName,
            })
        })
            .then(response => {
                if (!response.ok) {throw new Error(response.statusText);}

                // Update HTML content only on successful response
                parentElement.innerHTML = `<h3 onclick="activateUpdateBarName(this)" data-project-id="${projectId}">${newProjectName}</h3>`;
                parentElement.setAttribute('data-original-text', newProjectName);
            })
            .catch(function(error) {
                if (error instanceof Error) {
                    console.error("An error occurred:", "Project name cannot be empty");
                     alert("Project name cannot be empty");
                } else {
                    console.error("An error occurred:", error);
                }
            });
    } else {
        // Prevent updating HTML when newProjectName is empty or unchanged
        parentElement.innerHTML = `<h3 onclick="activateUpdateBarName(this)" data-project-id="${projectId}">${originalText}</h3>`;
    }

// Additional step: Update data-original-text attribute even if there is no change in the name
    parentElement.setAttribute('data-original-text', originalText);
}

function handleKeydownBar(event, element) {
    if (event.keyCode === 13) {
        event.preventDefault();
        element.blur();
    }
}
function getMemberList() {
    const projectId = document.getElementById('projectId').value;
    fetch(`/getMembers/${projectId}`)
        .then(response => {
            if (response.ok) {
                return response.text();
            } else {
                throw new Error('Failed to fetch task name');
            }
        })
        .then(userList => {
            const membersListP = document.getElementById("members_list_p");
            membersListP.textContent = userList;
            membersListP.style.whiteSpace = "pre-line"; // Set white-space property

            // Separate names
            const names = userList.split('\n');
            membersListP.textContent = names.map(name => `${name}\n-------------------------------`).join('\n');
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

// Function to toggle the list of members
function toggleMemberList() {
    const membersList = document.getElementById("membersList");
    const toggleMembers = document.getElementById("toggleMembers");

    if (membersList.classList.contains("hidden")) {
        membersList.classList.remove("hidden");
        toggleMembers.classList.add("closed");
        getMemberList(); // Fetch and display the list of members
    } else {
        membersList.classList.add("hidden");
        toggleMembers.classList.remove("closed");
    }
}

// Add event listener to toggle the list
document.getElementById("toggleMembers").addEventListener("click", toggleMemberList);

// Initialize the initial state as hidden
document.addEventListener("DOMContentLoaded", function () {
    const membersList = document.getElementById("membersList");
    const toggleMembers = document.getElementById("toggleMembers");

    membersList.classList.add("hidden");
    toggleMembers.classList.remove("closed");
});
//--------------------------------------------------DISPLAY-DEADLINE-DAYS-LEFT------------------------------------------
function calculateDeadlineDays(deadline){
    const date1 = deadline.getTime(); //deadline
    const date2 = new Date();
    const msDiff = date1 - date2;
    const aDayInMs = 24 * 60 * 60 * 1000;
    return Math.round(msDiff / aDayInMs);
}
const resultElement = document.getElementById("deadlineResult");
const projectDeadlineInput = document.getElementById("projectDeadline");//calculations
const currentDeadline = document.getElementById('projectDeadline').value;//display

// Get the deadline date from the input field
const deadlineDate = new Date(projectDeadlineInput.value);

// Calculate the days until the deadline
const daysUntilDeadline = calculateDeadlineDays(deadlineDate);
const absDays = Math.abs(daysUntilDeadline);
// Update the content of the HTML element
resultElement.textContent = currentDeadline + " || " + absDays + " days";
resultElement.style.color = daysUntilDeadline >= 0 ? 'green' : 'red';
