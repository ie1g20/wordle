<!DOCTYPE html>
<html>
<head>
    <title>Login - WORDLE</title>
    <link rel="stylesheet" href="${url.resourcesPath}/css/login.css">
</head>
<body>
<div class="login-container">
    <h1>WORDLE</h1>
    <form id="kc-form-login" action="${url.loginAction}" method="post">
        <input type="text" id="username" name="username" placeholder="Email or Username" autofocus required />
        <input type="password" id="password" name="password" placeholder="Password" required />
        <input type="submit" value="Login" />
    </form>
    <a class="forgot-password" href="${url.loginResetCredentialsUrl}">Forgot password?</a>
    <a class="register-link" href="${url.registrationUrl}">Register</a>
</div>
</body>
</html>

