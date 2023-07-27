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