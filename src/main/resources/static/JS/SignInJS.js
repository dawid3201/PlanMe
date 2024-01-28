function validatePassword() {
    var password = document.getElementById('password').value;

    // Password length check
    if (password.length < 10 || password.length > 12) {
    alert("Password must be between 10 and 12 characters.");
    return false;
}

    // Password complexity check
    var pattern = /^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).*$/;
    if (!pattern.test(password)) {
    alert("Password must contain at least 1 uppercase letter and special characters.");
    return false;
    }

    return true;
}
