var barFormHTML = `
    <form id="add-bar-form">
      <input type="text" id="new-bar-name" placeholder="Enter bar name here" /><br>
      <input type="submit" value="Add Bar" />
    </form>
  `;

var barFormOpen = false;

function closeBarForm() {
    document.getElementById("add-bar-form-container").style.display = "none";
    document.getElementById("open-bar-form-btn").style.display = "block";
    barFormOpen = false;
    document.removeEventListener("click", handleClickOutsideBarForm);
}

function handleClickOutsideBarForm(event) {
    var formContainer = document.getElementById("add-bar-form-container");
    var isClickInsideForm = formContainer.contains(event.target);

    if (barFormOpen && !isClickInsideForm && event.target.id !== "open-bar-form-btn") {
        closeBarForm();
    }
}
function deleteBar(element) {
    const barId = element.getAttribute('data-bar-id');
    fetch("/project/deleteBar?barId=" + barId, {
        method: 'DELETE',
    })
        .then((response) => {
            if (!response.ok) throw new Error('Network response was not ok');
            // Successfully deleted from database, now remove from HTML
            element.parentElement.parentElement.remove();
        })
        .catch((error) => {
            console.log('Request failed', error);
        });
}

document.getElementById("open-bar-form-btn").addEventListener("click", function(event) {
    event.preventDefault();

    document.getElementById("add-bar-form-container").innerHTML = barFormHTML;
    document.getElementById("add-bar-form-container").style.display = "block";
    document.getElementById("open-bar-form-btn").style.display = "none";

    barFormOpen = true;
    document.addEventListener("click", handleClickOutsideBarForm);

    document.getElementById("add-bar-form").addEventListener("submit", function(event) {
        event.preventDefault();

        var projectId = document.getElementById('projectId').value;
        var newBarName = document.querySelector("#new-bar-name").value;

        $.ajax({
            url: "/project/addBar",
            type: "GET",
            data: { projectId: projectId, barName: newBarName },
            success: function(response) {
                // Create new bar element.
                const newBarElement = '<div class="bar-container"><div class="swim-lane" data-bar-id="' + response.id + '"><h3 class="heading">' + response.name + '</h3></div></div>';

                // Append new bar to your bars container
                $('.lanes').append(newBarElement);

                // Re-initialize drag and drop AFTER the new bar is added
                initializeDragAndDrop();
                location.reload();

                closeBarForm();
            },
        });
    });
});