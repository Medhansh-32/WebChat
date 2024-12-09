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
function changeStyle(){
    const htmlWidth = document.documentElement.clientWidth;
    const navbarLogo=document.getElementById("navbar-logo");

    if (htmlWidth < 462) {
        if (chatWindow.style.display === 'none' || userList.style.display === '') {

            userList.style.display = 'none';
            chatWindow.style.display = 'block';
            chatWindow.style.width="100%";
            toggleButton.style.display="block";
            navbarLogo.style.fontSize="1.5rem";
            toggleButton.textContent = 'Show Contacts';
            chatWindow.style.overflowY="auto";
        } else {
            userList.style.display = 'block';
            userList.style.width="100%";
            navbarLogo.style.fontSize="2rem";
            chatWindow.style.display = 'none';
            toggleButton.style.display="none";
            userList.style.overflowY="auto";
        }  // Call the changeStyle function if the condition is met
    }

}

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
  //  changeStyle();
}

async function fetchUsers() {
    const response = await fetch("/users/data");
    const users = await response.json();

    const userList = document.getElementById("users");
    userList.innerHTML = ""; // Clear previous list

    users.forEach(user => {
        unreadMessages[user.contactName] = 0;  // Initialize unread message count for each user

        // Create the list item element
        const userElement = document.createElement("li");
        userElement.setAttribute("id", `user-${user.contactName}`); // Unique ID for the user element
        userElement.classList.add("user-item");

        // Create profile picture element
        const profileImg = document.createElement("img");
        profileImg.setAttribute("src", user.profilePictureLink);
        profileImg.setAttribute("alt", `${user.contactName}'s profile picture`);
        profileImg.classList.add("profile-picture");

        // Create badge element
        const badge = document.createElement("span");
        badge.classList.add("badge");
        badge.style.display = "none"; // Initially hide the badge
        badge.textContent = unreadMessages[user.contactName];  // Set unread message count
        userElement.appendChild(badge); // Add badge to user element, not profile container

        // Add contact name element
        const userName = document.createElement("span");
        userName.textContent = user.contactName;
        userName.classList.add("user-name");

        // Create delete button
        const deleteButton = document.createElement("i");
        deleteButton.classList.add("fa-solid", "fa-trash");
        deleteButton.classList.add("delete-icon");
        deleteButton.onclick = async () => {
            const deleteResponse = await fetch(`/users/deleteContact/${user.contactName}`, {
                method: "DELETE"
            });
            if (deleteResponse.ok) {
                userElement.remove(); // Remove user from the list
                document.getElementById("messages").innerHTML=""
                document.getElementById("chat-with").textContent = "Select a contact to start chatting";
            } else {
                alert("Failed to delete contact.");
            }
        };

        // Handle user selection
        userElement.addEventListener("click", () => {
            selectUser(user.contactName);
            selectedUser = user.contactName;
            const htmlWidth = document.documentElement.clientWidth;
      changeStyle();
        });

        // Append profile picture, username, and delete button to the user element
        userElement.appendChild(profileImg);
        userElement.appendChild(userName);
        userElement.appendChild(deleteButton);

        // Append the user element to the user list
        userList.appendChild(userElement);


    });
}


// Handle incoming messages
// Handle incoming messages
function handleIncomingMessage(message) {
    const sender = message.sender;

    if (selectedUser !== sender) {
        // Increment unread count for the sender
        unreadMessages[sender] = (unreadMessages[sender] || 0) + 1;
        updateBadge(sender);  // Ensure badge gets updated even when not selected
    } else {
        // Directly display the message if the user is already chatting with the sender
        displayMessage(message, false);
    }
}

// Update badge with the unread message count
function updateBadge(username) {
    const userElement = document.getElementById(`user-${username}`);
    if (!userElement) return; // Return if the user element doesn't exist
    const badge = userElement.querySelector(".badge");

    // Update the badge content and display
    badge.innerHTML = `${unreadMessages[username]} <i class="fa-regular fa-message"></i>`;
    badge.style.display = unreadMessages[username] > 0 ? "inline-block" : "none";
}

// Select a contact
function selectUser(username) {
    selectedUser = username;

    // Find the selected user's profile picture and username
    const selectedUserElement = document.getElementById(`user-${username}`);
    const profileImgSrc = selectedUserElement.querySelector("img").src; // Get the profile picture link

    // Get the chat-with element where we want to display the profile picture and name
    const chatWithElement = document.getElementById("chat-with");

    // Clear previous content in chat-with and add new content
    chatWithElement.innerHTML = ""; // Clear previous content

    // Create a new container for the profile picture and username
    const profileContainer = document.createElement("div");
    profileContainer.classList.add("profile-container");

    // Create image element for profile picture
    const profileImg = document.createElement("img");
    profileImg.setAttribute("src", profileImgSrc); // Set the profile picture URL
    profileImg.setAttribute("alt", `${username}'s profile picture`); // Set the alt attribute
    profileImg.classList.add("profile-picture"); // Optional: Add a class for styling

    // Create span for the username
    const userNameElement = document.createElement("span");
    userNameElement.textContent = username; // Set the username
    userNameElement.classList.add("user-name"); // Optional: Add a class for styling
    userNameElement.style.fontSize="24px";

    // Append profile picture and username to the container
    profileContainer.appendChild(profileImg);
    profileContainer.appendChild(userNameElement);

    // Append the container to the chat-with element
    chatWithElement.appendChild(profileContainer);

    // Clear previous messages and fetch chat history
    document.getElementById("messages").innerHTML = "";

    // Reset unread message count for the selected user
    unreadMessages[username] = 0;
    updateBadge(username); // Hide the badge for this user

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

// Format time in 12-hour clock with AM/PM
    const formattedTime = timestamp.toLocaleTimeString([], {
        hour: '2-digit',
        minute: '2-digit',
        hour12: true
    });

// Format date
    let formattedDate = timestamp.toLocaleDateString([], {
        weekday: 'long',
        month: 'long',
        day: 'numeric'
    });

// Add a comma after the day (you can customize this part)
    const dayIndex = formattedDate.lastIndexOf(' '); // Find the space after the day
    formattedDate = formattedDate.slice(0, dayIndex) + ',' + formattedDate.slice(dayIndex); // Add comma


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
    const messageInputActive=document.getElementById("message-input");
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
            rotate.classList.add("loader");
            rotate.style.marginLeft="auto";
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
    messageInputActive.focus();

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
        document.getElementById("fileName").textContent = `Img`;
        document.getElementById("fileName").style.fontSize="12px"
    }
});
// Add rotating loader for image upload
document.addEventListener('DOMContentLoaded', () => {
    // Attach click event listener to images in the chat messages
    document.getElementById('messages').addEventListener('click', (event) => {
        if (event.target.tagName === 'IMG' && event.target.alt === 'Image message') {
            const imageUrl = event.target.src; // Extract the image URL
            downloadImage(imageUrl);
        }
    });
});

function downloadImage(imageUrl) {
    // Call the Spring Boot API to download the image
    fetch(`/image/download?imageUrl=${encodeURIComponent(imageUrl)}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    })
        .then((response) => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.blob(); // Get the image as a Blob
        })
        .then((blob) => {
            // Create a temporary link element to trigger the download
            const link = document.createElement('a');
            link.href = URL.createObjectURL(blob);
            link.download = extractFileName(imageUrl); // Set the filename
            document.body.appendChild(link);
            link.click(); // Trigger the download
            document.body.removeChild(link); //
            alert("Image Downloaded....")
            // Remove the link after download
        })
        .catch((error) => {
            console.error('Error downloading the image:', error);
        });
}

function extractFileName(url) {
    return url.substring(url.lastIndexOf('/') + 1);
}
const toggleButton=document.getElementById("toggle-contacts");
const userList=document.getElementById("user-list");
const chatWindow=document.getElementById("chat-window");

toggleButton.addEventListener('click', () => {
   changeStyle();
});
