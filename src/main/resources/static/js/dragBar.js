$(document).ready(function() {

    $(document).ready(function() {
        $(".lanes").sortable({
            items: ".bar-container",
            handle: ".bar-header",
            cursor: "move",
            helper: "clone",
            tolerance: "pointer",
            update: function(event, ui) {
                let newPosition = ui.item.index() + 1;
                let barId = ui.item.find(".swim-lane").data("bar-id");
                $.ajax({
                    type: "PATCH",
                    url: "/project/updateBarPosition",
                    cache: false,
                    data: {
                        barId: barId,
                        newPosition: newPosition,
                    },
                    success: function() {
                        console.log('Bar position updated.');
                    },
                    error: function(jqXHR, textStatus, errorThrown) {
                        console.error('Error updating bar position:', textStatus, errorThrown);
                    }
                });
            }
        });
    });
});

//Bar positoin is updating wrong
