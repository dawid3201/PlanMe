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
                fetch(`/project/updateBarPosition?barId=${barId}&newPosition=${newPosition}`,{
                    method: 'PATCH',
                    headers:{
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: new URLSearchParams({
                        'barId': barId,
                        'newPosition': newPosition,
                    })
                })
                    .then(response  => {
                        if(!response.ok){
                            throw new Error(response.statusText);
                        }
                    })
                    .catch(error => {
                        console.log("Error updating bar position: ",error);
                    })
            }
        });
}
$(document).ready(moveCards);