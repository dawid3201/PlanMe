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
