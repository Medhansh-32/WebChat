// Connect to the WebSocket server
var ws = new WebSocket("ws://localhost:8080/ws");

// Handle incoming messages
ws.onmessage = function(event) {
    var messages = document.getElementById("messages");
    var message = document.createElement("li");
    message.textContent = event.data;
    messages.appendChild(message);
};

// Send a message to a specific user
function sendMessage() {
    var userId = document.getElementById("userId").value;
    var contactId = document.getElementById("contact").value;
    var messageText = document.getElementById("message").value;

    if (userId && contactId && messageText) {
        // Format message as "recipientId:messageText"
        var message = contactId + ":" + messageText;
        ws.send(message);
        document.getElementById("message").value = '';
    }
}
