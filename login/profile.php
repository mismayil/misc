<?php
session_start();
?>
<!DOCTYPE html>
<html>
<head>
<title>My Profile</title>
<style>
span {
color: red;
}
</style>
</head>
<body>
<h3>My Profile</h3>
<?php
$con=mysqli_connect("localhost","root","","users");

$result=mysqli_query($con,"SELECT * FROM users WHERE ((Username='$_SESSION[username]')&&(Password='$_SESSION[password]'))");
$row=mysqli_fetch_array($result);
echo "<span>User: </span>";
echo $row['Username']; 
echo "<br />";
echo "<span>Name: </span>";
echo $row['Firstname']." ".$row['Lastname'];
echo "<br />";
echo "<span>Email: </span>";
echo $row['Email'];
echo "<br />";
echo "<span>Address: </span>";
echo $row['Country']." ".$row['City']." ".$row['Address']." ".$row['ZipCode'];
echo "<br />";
echo "<span>Phone Number: </span>";
echo $row['PhoneNumber'];
 
?>
<p><a href="logout.php">Log out</a></p>
</body>
</html>