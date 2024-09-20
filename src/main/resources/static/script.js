function updateWebSocketConnection(newReceiver) {
    // Close the existing WebSocket connection if one exists
    if (window.socket) {
        window.socket.close();
    }

    // Get the sender (userId) from the cookies
    let cookies = document.cookie.split(";");
    let senderCookie = cookies.find(cookie => cookie.trim().startsWith("userId="));
    let sender = senderCookie ? decodeURIComponent(senderCookie.split("=")[1]) : null;

    // Decode the receiver (newReceiver) for safe WebSocket usage
    newReceiver = decodeURIComponent(newReceiver);

    // Update the displayed receiver name (if applicable)
    const receiverH1 = document.getElementById('receiverH1');
    if (receiverH1) {
        receiverH1.textContent = newReceiver;
    }

    // Establish a new WebSocket connection with sender and receiver
    window.socket = new WebSocket("ws://localhost:8080/ws?sessionId=" + sender + "&receiver=" + newReceiver);

    // Handle WebSocket 'open' event
    window.socket.onopen = function () {
        addMessageToChatBox('Connected to WebSocket server with session ID: ' + sender + ' and receiver: ' + newReceiver, 'sent');
    };

    // Handle WebSocket 'message' event (when a new message is received)
    window.socket.onmessage = function (event) {
        addMessageToChatBox('Received: ' + event.data, 'received');
    };

    // Handle WebSocket 'close' event
    window.socket.onclose = function () {
        addMessageToChatBox('Connection closed with receiver: ' + newReceiver, 'received');
    };

    // Handle WebSocket 'error' event
    window.socket.onerror = function (error) {
        console.error('WebSocket error:', error);
        addMessageToChatBox('WebSocket error occurred. Please try again.', 'received');
    };
}

// Helper function to add a message to the chatbox
function addMessageToChatBox(message, type) {
    const chatBox = document.getElementById('chat-box');
    const messageElement = document.createElement('div');
    messageElement.classList.add('message', type);
    messageElement.textContent = message;
    chatBox.appendChild(messageElement);
    chatBox.scrollTop = chatBox.scrollHeight;  // Auto-scroll to the bottom
}
