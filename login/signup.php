<!DOCTYPE html>
<html>
<head>
<title>Sign up</title>
<script src="script.js"></script>
<style>
body {
  font-family: Verdana;
}
table {
  color: green;
}
#signup {
  position: absolute;
  top: 100px;
  left: 500px;
}
#btnsignup {
  height: 30px;
  color: white;
  cursor: pointer;
  background-color: green;
}
</style>
</head>

<body>

<div id="signup">

<form action="register.php" method="post" id="register">
<table>
<tr>
<td>First name</td>
<td><input type="text" name="txtname" required></td>
</tr>
<tr>
<td>Last name</td>
<td><input type="text" name="txtlast" required></td>
</tr>
<tr>
<td>Email</td>
<td><input type="text" name="txtemail" required></td>
</tr>
<tr>
<td>Address</td>
<td><input type="text" name="txtaddr" required></td>
</tr>
<tr>
<td>Country</td>
<td><input type="text" name="txtcntr" required></td>
</tr>
<tr>
<td>City</td>
<td><input type="text" name="txtcity" required></td>
</tr>
<tr>
<td>ZIP code</td>
<td><input type="text" name="txtzip" required></td>
</tr>
<td>Phone number</td>
<td><input type="text" name="txtphone" required></td>
</tr>
<tr>
<td>Username</td>
<td><input type="text" name="txtusname" required></td>
</tr>
<tr>
<td>Password</td>
<td><input type="password" name="txtpswrd" id="txtpswrd" required></td>
</tr>
<tr>
<td>Confirm Password</td>
<td><input type="password" name="txtcnfrm" id="txtcnfrm" onchange="checkMatch()" required></td>
<td id="confirmError"></td>
</tr>
<tr>
<td></td>
<td><input type="submit" name="btnsignup" value="Sign up" id="btnsignup"></td>
</tr>
</table>
</form>

</div>

</body>
</html>