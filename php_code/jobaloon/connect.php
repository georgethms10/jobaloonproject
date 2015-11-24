<?php
$mysqli=new mysqli("localhost","root","admin1!","app_JobBox");
 //mysqli_query("SET NAMES utf8");
mysqli_set_charset($mysqli, "utf8");
if(mysqli_connect_errno())
{
	printf("Connection Failed: %s\n".mysqli_connect_error());
	exit();
}
?>