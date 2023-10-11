const socket = new SockJS('/websocket-endpoint');
const stompClient = Stomp.over(socket);

stompClient.connect({},
    function (frame) {
        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/messages', function (message) {
            console.log('Received: ' + message.body);
        });

        stompClient.subscribe('/topic/updates', function (notification) {
            if (notification.body === "An update has occurred!") {
                refreshContentOrPage();
            }
        });
        stompClient.subscribe('/topic/taskUpdates', function (notification) {
            if (notification.body === "THERE WAS AN UPDATE FOR A TASK ELEMENT") {
                initializeSortableSwimLanes();
            }
        });


        // Send a message to the server (Optional based on your requirement)
        stompClient.send("/app/send", {}, "Hello from the client!");
    },
    function(error) {  // Error callback
        console.error('STOMP error:', error);
    }
);


function refreshContentOrPage() {
    location.reload();
}