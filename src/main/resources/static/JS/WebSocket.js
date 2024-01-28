const socket = new SockJS('/websocket-endpoint');
const stompClient = Stomp.over(socket);

stompClient.connect({},
    function (frame) {
        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/messages', function (message) {
            console.log('Received: ' + message.body);
        });

        stompClient.subscribe('/topic/updates', function (notification) {
            if (notification.body === "THERE WAS AN UPDATE FOR A TASK ELEMENT") {
                reloadPage();
            }
        });
    },
    function(error) {  // Error callback
        console.error('STOMP error:', error);
    }
);
function reloadPage(){
    location.reload();
}
//TODO: in order to update all elements, get a method that will save all elements
function refreshBoard() {
    let projectId = document.getElementById('projectId').value;

    // Make a fetch request to the server to get updated board content
    fetch(`/project/getTaskList?projectId=${projectId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text();
        })
        .then(data => {
            // Replace the content of the board container with the updated content
            document.getElementById('board-container').innerHTML = data;
        })
        .catch(error => {
            console.error('Error refreshing board:', error);
        });
}


//take tasks, bars from database
