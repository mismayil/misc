<?php
session_start();
if (!isset($_SESSION['txtuser'])) {
   //if (isset($_POST['txtuser'])) {
     $con=mysqli_connect("localhost","root","","users");

     $result=mysqli_query($con,"SELECT * FROM users WHERE ((Username='$_POST[txtuser]')&&(Password='$_POST[txtpswd]'))");
	 $count=mysqli_num_rows($result);
     if ($count==1) {
	    $_SESSION['username']=$_POST['txtuser'];
		$_SESSION['password']=$_POST['txtpswd'];
		$_SESSION['error']=false;
        header("location:profile.php");
     } else {
        header("location:index.php");
		$_SESSION['error']=true;
     }
	 } else {
	   echo "You are already logged in.";
	 }
?>