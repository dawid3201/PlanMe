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
            document.getElementById("members_list_p").textContent = userList;
            document.getElementById("members_list_p").style.whiteSpace = "pre-line"; // Set white-space property
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