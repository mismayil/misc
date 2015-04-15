<?php
session_start();
session_unset('Error');
session_destroy();
header("location:index.php");
?>