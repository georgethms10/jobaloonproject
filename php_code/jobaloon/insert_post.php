<?php
include('Slim/Slim.php');
\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim();
$app->map('/insertPost/', function() use ($app)
{
	include("connect.php");
	$sel_oath=$mysqli->prepare("SELECT * FROM tb_user WHERE oauthToken=? AND user_id=?");
	$sel_oath->bind_param("si",$app->request()->post('oauthToken'),$app->request()->post('compony_id'));
	$sel_oath->execute();
	$sel_oath->store_result();
	if($sel_oath->num_rows>0)
	{ 
		$activity="upload a job post";
		date_default_timezone_set('UTC');
		$date=date('Y-m-d H:i:s');
		$address = str_replace(" ", "+", $app->request()->post('location'));
		$json = file_get_contents("http://maps.google.com/maps/api/geocode/json?address=$address&sensor=false");
		$json = json_decode($json);
		$lattitude = $json->{'results'}[0]->{'geometry'}->{'location'}->{'lat'};
		$longitude = $json->{'results'}[0]->{'geometry'}->{'location'}->{'lng'};
		$data=$mysqli->prepare("INSERT INTO `tb_jobpost`( `Role`, `start_date`, `end_date`, `working_days`, `location`, `distance`, `jobDescription`, `compony_id`,`lattitude`,`longitude`,`additional_information`) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
		$data->bind_param("sssssssisss",$app->request()->post('Role'),$app->request()->post('start_date'),$app->request()->post('end_date'),$app->request()->post('working_days'),$app->request()->post('location'),$app->request()->post('distance'),$app->request()->post('jobDescription'),$app->request()->post('compony_id'),$lattitude,$longitude,$app->request()->post('additional_information'));
		$data->execute();
		$pid=$mysqli->insert_id;
		$update_date=$mysqli->prepare("UPDATE tb_user SET Last_activity=?,Last_activity_date=? WHERE user_id=?");
		$update_date->bind_param("ssi",$activity,$date,$app->request()->post('compony_id'));
		$update_date->execute();
		$post['Result']=true;
		$post['id']=$pid;
		echo json_encode($post);
	}
	else
	{
		$post['Result']=false;
		$post['Error']="Invalid Call";
	}
})->via('POST');
$app->run();
