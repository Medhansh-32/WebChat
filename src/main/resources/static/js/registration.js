const img = document.createElement("img");
document.getElementById('profilePicture').addEventListener('change', (e) => {
    const profilePicture = e.target.files[0];
    if (profilePicture) {

        img.src = URL.createObjectURL(profilePicture);
        img.style.height = "80px";
        img.style.width = "80px";
        img.style.borderRadius = "50%";
        img.style.marginTop = "10px";

        // Remove any previously appended images
        const form = document.getElementById("registrationForm");
        const existingImg = form.querySelector("img");
        if (existingImg) {
            form.removeChild(existingImg);
        }

        // Append the new image
        form.appendChild(img);
    }
});

// Function to request OTP
async function requestOtp(event) {
    event.preventDefault(); // Prevent form submission

    const email = document.getElementById('email').value;
    console.log(email);

    if (email) {
        const sendingOtp=document.createElement("p");
        sendingOtp.innerText="Sending Otp.....";
        sendingOtp.style.fontStyle="italic";
        sendingOtp.style.color="white";
        const form=document.getElementById("registrationForm");
        form.appendChild(sendingOtp);
        fetch(`users/getOtp?email=${encodeURIComponent(email)}`, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        })
            .then(response => {
                console.log("request send to",email);
                if (response.ok) {
                    sendingOtp.innerText="Sending Otp.....";
                    const form = document.getElementById("registrationForm");
                    form.style.display="none";
                    document.getElementById('message').textContent = 'OTP has been sent to your email.';
                    document.getElementById('otpSection').style.display = 'block';
                    console.log("ok resposne");
                    sendingOtp.innerText="";
                    img.style.display="none"
                } else {
                    alert('Error sending OTP.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error sending OTP.');
            });
    } else {
        alert('Please enter a valid email.');
    }
}

// Function to verify OTP and proceed with registration
async function verifyOtp(event) {
    event.preventDefault(); // Prevent form submission

    const email = document.getElementById('email').value;
    const otp = document.getElementById('otp').value;

    if (email && otp) {
        const formRegister=document.getElementById("registrationForm");
        formRegister.style.display="none"
        const requestData = { email, otp };
        const sendingOtp=document.createElement("p");
        sendingOtp.innerText="Verifying Otp.....";
        sendingOtp.style.fontStyle="italic";
        sendingOtp.style.color="white";
        const form=document.getElementById("otpForm");
        form.appendChild(sendingOtp);
        fetch('users/verifyOtp', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestData)
        })
            .then(response => {
                if (response.ok) {
                    registerUser(); // Only register after OTP is verified
                } else {
                    sendingOtp.innerText="";
                    alert('Invalid OTP or email.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error verifying OTP.');
            });
    } else {
        alert('Please enter both email and OTP.');
    }
}

// Function to register the user
async function registerUser() {
    const formData = new FormData();
    formData.append("username", document.getElementById("username").value);
    formData.append("password", document.getElementById("password").value);
    formData.append("email", document.getElementById("email").value);
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
            window.location.href = "/"; // Redirect to home after successful registration
        } else {
            document.getElementById('message').textContent = 'Registration failed';
            document.getElementById('message').style.color = 'red';
        }
    } catch (error) {
        console.error("Error:", error);
        document.getElementById('message').textContent = 'An error occurred while registering. Please try again.';
        document.getElementById('message').style.color = 'red';
    }
}