//Bar form
// var barFormHTML = `
//     <form id="add-bar-form">
//       <input type="text" id="new-bar-name" placeholder="Add Card" /><br>
//       <button type="submit" class="tick-button"><i class="fas fa-check"></i></button>
//     </form>
//   `;
var barFormOpen = false;

function closeBarForm() {
    document.getElementById("add-bar-form-container").style.display = "none";
    document.getElementById("open-bar-form-btn").style.display = "block";
    barFormOpen = false;
    document.removeEventListener("click", handleClickOutsideBarForm);
}

function handleClickOutsideBarForm(event) {
    const formContainer = document.getElementById("add-bar-form-container");
    const isClickInsideForm = formContainer.contains(event.target);

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
            element.parentElement.parentElement.remove();
            location.reload();  //<---------------------------- RELOADING PAGE HERE
        })
        .catch((error) => {
            console.log('Request failed', error);
        });
}

document.getElementById("open-bar-form-btn").addEventListener("click", function(event) {
    event.preventDefault();

    // document.getElementById("add-bar-form-container").innerHTML = barFormHTML;
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
            type: "POST",
            data: { projectId: projectId, barName: newBarName },
            success: function(response) {
                // Create new bar element.
                const newBarElement = '<div class="bar-container"><div class="swim-lane" data-bar-id="' + response.id + '"><h3 class="heading">' + response.name + '</h3></div></div>';

                $(newBarElement).insertBefore("#add-bar-form-container");
                location.reload(); //<---------------------------- RELOADING PAGE HERE
                closeBarForm();
            },
            error: function(xhr, status, error) {
                if (xhr.status === 500) { // HTTP status code for Conflict
                    alert("There already is a bar with name: " + newBarName);
                } else {
                    // Handle other error cases
                    console.error("An error occurred:", status, error);
                }
            }
        });
    });
});
function activateUpdateBarName(element) {
    var text = element.firstElementChild.textContent;
    var id = element.getAttribute('data-bar-id');

    // change h3 to an editable input field
    element.innerHTML = `<input type="text" id="input-${id}" value="${text}" onblur="updateBarName(this)" onkeydown="handleKeydownBar(event, this)" onclick="event.stopPropagation()">`;

    // Immediately focus the input field to prevent needing another click
    document.getElementById(`input-${id}`).focus();
}
function updateBarName(element) {
    var newDescription = element.value;
    var barId = element.id.split('-')[1];
    var parentElement = element.parentElement;
    var originalText = parentElement.getAttribute('data-original-text');

    if (newDescription !== originalText) {
        fetch('/project/updateBarName', {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({
                'barId': barId,
                'barName': newDescription,
            })
        })
    }else{
        newDescription = originalText;
    }
    parentElement.innerHTML = `<h3 onclick="activateUpdateBarName(this)" data-bar-id="${barId}">${newDescription}</h3>`;
}
function handleKeydownBar(event, element) {
    if (event.keyCode === 13) {
        event.preventDefault();
        updateBarName(element);
        element.blur();
    }
}
