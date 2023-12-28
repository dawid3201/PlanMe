function moveCards(){
        $(".lanes").sortable({
            items: ".bar-container",
            placeholder: "bar-placeholder",
            handle: ".bar-header",
            cursor: "move",
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
}
$(document).ready(moveCards);

//Bar positoin is updating wrong
