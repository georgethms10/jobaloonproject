<?php
include('Slim/Slim.php');
\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim();
//IntialPage
$app->map('/logout/', function() use ($app)
{
	include("connect.php");
    $sel_oath=$mysqli->prepare("SELECT * FROM tb_user WHERE oauthToken=? AND user_id=?");
	$sel_oath->bind_param("si",$app->request()->post('oauthToken'),$app->request()->post('userid'));
	$sel_oath->execute();
	$sel_oath->store_result();
	if($sel_oath->num_rows>0)
	{
		$activity='Logout';
	    date_default_timezone_set('UTC');
		$date=date('Y-m-d H:i:s');
		$update_deviceToken=$mysqli->prepare("UPDATE `tb_user` SET deviceToken='',oauthToken='',Last_activity=?,Last_activity_date=? WHERE `user_id`=?");
		$update_deviceToken->bind_param("ssi",$activity,$date,$app->request()->post('userid'));
		$update_deviceToken->execute();
		$post['Result']=true;
	}
	else
	{
		$post['Result']=false;
		$post['Error']="Invalid Call";
	}
	echo json_encode($post);
})->via('POST');
$app->run();

	    	 
