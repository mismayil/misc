<?php
session_start();
if (!isset($_SESSION['error'])) {
$_SESSION['error']=false;
}
echo 
'<!DOCTYPE html>
<html>
<head>
<title>Login</title>
<style>
body {
  font-family: Verdana;
}
#error {
  color: red;
}
#login {
  position: absolute;
  top: 100px;
  left: 500px;
}
.info {
  color: green;
  font-family: Verdana;
}
#btnsignin {
  height: 30px;
  color: white;
  background-color: blue;
  border-radius: 5px;
  cursor: pointer;
}
</style>
</head>

<body>

<div id="login">

<form action="checkLogin.php" method="post">';

if ($_SESSION['error']) {
   echo '<p id="error">Username or Password is incorrect</p>';
}
session_destroy();
?>
<p class="info"><b>Username</b></p>
<input type="text" name="txtuser" required>
<p class="info"><b>Password</b></p>
<input type="password" name="txtpswd" required>
<p><input type="submit" name="btnsignin" value="Sign in" id="btnsignin"></p>
<a href="signup.php">Sign up</a>
<br />
<p style="color:silver">powered by Mahammad</p>
</form>

</div>

</body>
</html>