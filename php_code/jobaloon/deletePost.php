<?php
include('Slim/Slim.php');
\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim();
$app->map('/deletePost/', function() use ($app)
{
	include("connect.php");
	$sel_oath=$mysqli->prepare("SELECT * FROM tb_user WHERE oauthToken=? AND user_id=?");
	$sel_oath->bind_param("si",$app->request()->post('oauthToken'),$app->request()->post('compony_id'));
	$sel_oath->execute();
	$sel_oath->store_result();
	if($sel_oath->num_rows>0)
	{ 
		date_default_timezone_set('UTC');
		$date=date('Y-m-d H:i:s');
		$activity="deleted the post";
		$data=$mysqli->prepare("DELETE FROM `tb_jobpost` WHERE `id`=?");
		$data->bind_param("i",$app->request()->post('postid'));
		$data->execute();
		$update_date=$mysqli->prepare("UPDATE tb_user SET Last_activity=?,Last_activity_date=? WHERE user_id=?");
		$update_date->bind_param("ssi",$activity,$date,$app->request()->post('compony_id'));
		$update_date->execute();
		$post['Result']=true;
	}
	else
	{
		$post['Result']=false;
		$post['Error']="Invalid user";
	}
	echo json_encode($post);
})->via('POST');
$app->run();
