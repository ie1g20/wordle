<!DOCTYPE html>
<html>
<head>
    <title>Register - WORDLE</title>
    <link rel="stylesheet" href="${url.resourcesPath}/css/login.css">
</head>
<body>
<div class="login-container">
    <h1>WORDLE</h1>
    <form id="kc-form-register" action="${url.registrationAction}" method="post">
        <input type="text" id="username" name="username" placeholder="Username" required />
        <input type="email" id="email" name="email" placeholder="Email" required />
        <input type="text" id="firstName" name="firstName" placeholder="First Name" required />
        <input type="text" id="lastName" name="lastName" placeholder="Last Name" required />
        <input type="password" id="password" name="password" placeholder="Password" required />
        <input type="password" id="password-confirm" name="password-confirm" placeholder="Confirm Password" required />
        <input type="submit" value="Register" />
    </form>
    <a class="forgot-password" href="${url.loginUrl}">Back to Login</a>
</div>
</body>
</html>

