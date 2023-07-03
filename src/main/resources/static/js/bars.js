
$(document).ready(function(){
    $("#addBar").click(function(){
        var projectId = /*[[${project.id}]]*/ '';
        var barName = $("#barName").val();

        $.ajax({
            url: "/project/addBar",
            type: "GET",
            data: { projectId: projectId, barName: barName },
            success: function(response) {
                // Assuming 'response' contains the new bar
                alert("Bar Added Successfully");

                // Create new bar element. This would depend on how you want to display the bar
                var newBarElement = '<div class="bar">' + response.name + '</div>';

                // Append new bar to your bars container
                $('#barsContainer').append(newBarElement);
            },
            error: function(response) {
                alert("Failed to add bar");
            }
        });
    });
});
