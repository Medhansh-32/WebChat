document.getElementById('registrationForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const formData = new FormData();
    formData.append("username", document.getElementById("username").value);
    formData.append("password", document.getElementById("password").value);

    const profilePicture = document.getElementById("profilePicture").files[0];
    if (profilePicture) {
        formData.append("profilePicture", profilePicture);
    }

    try {
        const response = await fetch('/users/register', {
            method: 'POST',
            body: formData,
        });

        if (response.ok) {
            document.getElementById('message').textContent = 'Registration successful!';
            document.getElementById('message').style.color = 'green';
        } else {
            document.getElementById('message').textContent =  'Registration failed';
            document.getElementById('message').style.color = 'red';
        }
    } catch (error) {
        console.error("Error:", error);
        document.getElementById('message').textContent = 'An error occurred while registering. Please try again.';
        document.getElementById('message').style.color = 'red';
    }
});
