<?php
include('Slim/Slim.php');
\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim();
$app->map('/updateJobID/', function() use ($app)
{
	include("connect.php");
	$sel_oath=$mysqli->prepare("SELECT * FROM tb_user WHERE oauthToken=? AND user_id=?");
	$sel_oath->bind_param("si",$app->request()->post('oauthToken'),$app->request()->post('companyid'));
	$sel_oath->execute();
	$sel_oath->store_result();
	if($sel_oath->num_rows>0)
	{ 
		$activity='updated a job';
		date_default_timezone_set('UTC');
		$date=date('Y-m-d H:i:s');
		$select_job=$mysqli->prepare("SELECT `jobDescription` FROM tb_jobpost WHERE id=?");
		$select_job->bind_param("i",$app->request()->post('jobid'));
		$select_job->execute();
		$select_job->store_result();
		$select_job->bind_result($jobDescription);
		while($select_job->fetch())
		{
			$update_date=$mysqli->prepare("UPDATE `tb_user` SET  `JobId`=?,`Jobdescription`=?,`Last_activity`=?,`Last_activity_date`=? WHERE `user_id`=?");
			$update_date->bind_param("isssi",$app->request()->post('jobid'),$jobDescription,$activity,$date,$app->request()->post('companyid'));
			$update_date->execute();
			
		}
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
