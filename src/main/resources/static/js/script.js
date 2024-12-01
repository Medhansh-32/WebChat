const user = document.getElementById("online_user").innerText;
const btn = document.getElementById("activate");

// Add the click event listener first
btn.addEventListener("click", () => {
    console.log("Clicking...");
    connect();
});

// Trigger the click programmatically
btn.click();

// Hide the button after triggering the click
btn.style.display = "none";

let stompClient = null;
let selectedUser = null;
const unreadMessages = {}; // Track unread message counts for each user

// Connect to WebSocket
async function connect() {
    await fetchUsers();
    const socket = new SockJS('/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, () => {
        console.log('Connected to WebSocket');
        stompClient.subscribe('/user/queue/messages', (message) => {
            const messageData = JSON.parse(message.body);
            handleIncomingMessage(messageData);
        });
    });
}

// Fetch user list from backend
async function fetchUsers() {
    const response = await fetch("/users/data");
    const users = await response.json();

    const userList = document.getElementById("users");
    userList.innerHTML = ""; // Clear previous list

    users.forEach(user => {
        unreadMessages[user] = 0; // Initialize unread counter
        const userElement = document.createElement("li");
        userElement.setAttribute("id", `user-${user}`); // Unique ID for the user element
        userElement.textContent = user;
        userElement.onclick = () => selectUser(user);

        // Add badge for unread messages
        const badge = document.createElement("span");
        badge.classList.add("badge");
        badge.style.display = "none"; // Hide badge initially
        badge.textContent = unreadMessages[user];
        userElement.appendChild(badge);

        userList.appendChild(userElement);
    });
}

// Handle incoming messages
function handleIncomingMessage(message) {
    const sender = message.sender;

    if (selectedUser !== sender) {
        // Increment unread count for the sender
        unreadMessages[sender] = (unreadMessages[sender] || 0) + 1;
        updateBadge(sender);
    } else {
        // Directly display the message if the user is already chatting with the sender
        displayMessage(message, false);
    }
}

// Update badge with the unread message count
function updateBadge(username) {
    const userElement = document.getElementById(`user-${username}`);
    const badge = userElement.querySelector(".badge");
    badge.innerHTML = `${unreadMessages[username]} <i class="fa-regular fa-message"></i>`;
    badge.style.display = unreadMessages[username] > 0 ? "inline-block" : "none";
}

// Select a contact
function selectUser(username) {
    selectedUser = username;
    document.getElementById("chat-with").textContent = `Chatting with: ` + username;

    // Reset unread count for the selected user
    unreadMessages[username] = 0;
    updateBadge(username);

    document.getElementById("messages").innerHTML = ""; // Clear previous messages
    fetchChatHistory(username);
}

// Fetch chat history with the selected user
async function fetchChatHistory(username) {
    const response = await fetch(`/messages/${username}`);
    const messages = await response.json();
    console.log(messages)
    const messagesDiv = document.getElementById("messages");
    messagesDiv.innerHTML = ""; // Clear previous messages

    messages.forEach(message => {
        const isSent = message.sender === user;
        displayMessage(message, isSent);
    });
}

let lastMessageDate = null; // Track the last displayed date globally

function displayMessage(message, isSent) {
    if ((isSent && message.receiver !== selectedUser) || (!isSent && message.sender !== selectedUser)) {
        return; // Do not display the message if the selected user is not involved
    }

    const messagesDiv = document.getElementById("messages");
    const messageElement = document.createElement("div");
    messageElement.classList.add("message");
    messageElement.classList.add(isSent ? "sent" : "received");

    // Create a container for both content and timestamp
    const messageContent = document.createElement("div");

    if (!message.imageLink) {
        messageContent.textContent = message.content;
    } else {
        const imgElement = document.createElement("img");
        imgElement.src = message.imageLink;
        imgElement.alt = "Image message";
        imgElement.style.maxWidth = "200px";  // You can control the size of the image
        imgElement.style.borderRadius = "10px"; // Optional: Style the image (e.g., rounded corners)
        imgElement.style.marginTop = "10px";
        messageContent.appendChild(imgElement);
        if (message.content) {
            const messageWithImage = document.createElement("div");
            messageWithImage.textContent = message.content;
            messageContent.appendChild(messageWithImage);
        }
    }

    // Append message content to the message element
    messageElement.appendChild(messageContent);

    // Create and append the timestamp
    const timestamp = new Date(message.timestamp); // Assuming message.timestamp is in a valid format
    const formattedTime = timestamp.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    const formattedDate = timestamp.toLocaleDateString([], { weekday: 'long', month: 'long', day: 'numeric' });

    // Check if the date is different from the last displayed date
    if (lastMessageDate !== formattedDate) {
        const dateElement = document.createElement("div");
        dateElement.classList.add("date");
        dateElement.textContent = formattedDate;
        dateElement.style.textAlign = "center";
        dateElement.style.margin = "10px 0"; // Add spacing for the date
        dateElement.style.fontWeight = "bold";
        dateElement.style.fontSize = "16px";
        messagesDiv.appendChild(dateElement);
        lastMessageDate = formattedDate; // Update the last displayed date
    }

    // Append the timestamp
    const timestampElement = document.createElement("span");
    timestampElement.classList.add("timestamp");
    timestampElement.textContent = formattedTime;
    timestampElement.style.fontStyle = "italic";
    timestampElement.style.fontSize = "12px";

    messageElement.appendChild(timestampElement);

    // Append the entire message to the message div
    messagesDiv.appendChild(messageElement);

    // Scroll to the bottom of the message container
    messagesDiv.scrollTop = messagesDiv.scrollHeight; // Scroll to the latest message
}



// Send a message
async function sendMessage() {
    const messageInput = document.getElementById("message-input").value;
    const imageInput = document.getElementById("image-input").files[0];
    if (!messageInput && !imageInput) {
        alert("Please enter a message or select an image to send.");
        return; // Stop the function from proceeding
    }
    if (!selectedUser) {
        alert("Please select a user to chat with!");
        return;
    }

    let imageUrl = null;

    // Step 1: Upload image if selected
    if (imageInput) {
        const formData = new FormData();
        formData.append("file", imageInput);

        try {
            const rotate =document.createElement("div");
            rotate.classList.add("rotating-circle");
            document.getElementById("messages").appendChild(rotate);
            const response = await fetch("/image/upload", {
                method: "POST",
                body: formData,
            });
            if(response.ok){
                const data = await response.text();
                imageUrl = data;
                rotate.style.display="none";
            }
        } catch (error) {
            console.error("Error uploading image:", error);
            alert("Failed to upload image.");
            return;
        }
    }
    stompClient.send("/app/private-message", {}, JSON.stringify({
        sender: user,
        receiver: selectedUser,
        content: messageInput,
        imageLink: imageUrl, // Include image URL in the message payload

    }));
    displayMessage({
        sender: user,
        receiver: selectedUser,
        content: messageInput,
        imageLink: imageUrl,
        timestamp:new Date()
    }, true);
    document.getElementById("image-input").value="";
    document.getElementById("message-input").value = ""; // Clear input field
    document.getElementById("fileName").innerText="";
}


// Add contact functionality
document.getElementById("add-contact").addEventListener("click", async () => {
    const newContactInput = document.getElementById("new-contact-input");
    const newContact = newContactInput.value.trim();

    if (newContact === "") {
        alert("Please enter a valid username.");
        return;
    }

    try {
        const response = await fetch('/users/addContact', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ newContact: newContact })
        });

        if (response.ok) {
            alert(`${newContact} added successfully!`);
            fetchUsers(); // Refresh the user list
            newContactInput.value = ""; // Clear input
        } else {
            alert("Failed to add contact. Please try again.");
        }
    } catch (error) {
        console.error("Error adding contact:", error);
        alert("An error occurred while adding the contact.");
    }
});
// Get the file input element and the Font Awesome icon
const fileInput = document.getElementById("image-input");
const fileInputIcon = document.getElementById("fileInputIcon");
fileInputIcon.style.fontSize="30px";
fileInputIcon.style.marginLeft="5px"

// Trigger the file input when the Font Awesome icon is clicked
fileInputIcon.addEventListener("click", () => {
    fileInput.click(); // This opens the file chooser dialog
});

// When a file is selected, show the file name or handle the file
fileInput.addEventListener("change", (event) => {
    const selectedFile = event.target.files[0];
    if (selectedFile) {
        document.getElementById("fileName").textContent = `Selected file: ${selectedFile.name}`;
    }
});
// Add rotating loader for image upload

