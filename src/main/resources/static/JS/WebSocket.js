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
                refreshContentOrPage();
            }
        });
    },
    function(error) {  // Error callback
        console.error('STOMP error:', error);
    }
);
function refreshContentOrPage() {
    location.reload();
}
//TODO: in order to update all elements, get a method that will save all elements