<?php
session_start();
session_destroy();
echo 
'<!DOCTYPE html>
<html>
<head>
<title>Registration</title>
<style>
body {
  font-family: verdana;
}
#success {
  color: green;
  height: 30px;
}
div {
  position: absolute;
  top: 50px;
  left: 500px;
}
</style>
</head>
<body>
<div>
<p id="success">You successfully signed up!</p>
<a href="index.php">Back to login page</a>
</div>';
//Create connection
$con=mysqli_connect("localhost","root","","users");


//Check connection
if (mysqli_connect_errno($con)) {
  echo "Failed to connect Mysql:".mysql_connect_error();
}

//Insert data
$sql="INSERT INTO users(Firstname,Lastname,Email,Address,Country,City,ZipCode,PhoneNumber,Username,Password) VALUES('$_POST[txtname]','$_POST[txtlast]','$_POST[txtemail]','$_POST[txtaddr]','$_POST[txtcntr]','$_POST[txtcity]','$_POST[txtzip]','$_POST[txtphone]','$_POST[txtusname]','$_POST[txtpswrd]')";

mysqli_query($con,$sql);


?>
</body>
</html>
