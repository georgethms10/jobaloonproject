<?php
include('Slim/Slim.php');
\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim();
$app->map('/viewjobs/', function() use ($app)
{
	$index=$app->request()->post('index');
	include("connect.php");
	$sel_data=$mysqli->prepare("SELECT * FROM tb_user WHERE oauthToken=? AND user_id=?");
	$sel_data->bind_param("si",$app->request()->post('oauthToken'),$app->request()->post('userid'));
	$sel_data->execute();
	$sel_data->store_result();
	if($sel_data->num_rows>0)
	{
		$activity='viewed all jobs';
	    date_default_timezone_set('UTC');
		$date=date('Y-m-d H:i:s');
		$sel_job=$mysqli->prepare("SELECT `id`,`Role`,`start_date`,`end_date`,`working_days`,`location`,`distance`,`jobDescription`,`compony_id`,`lattitude`,`longitude`,`additional_information` FROM tb_jobpost WHERE compony_id=?");
		$sel_job->bind_param("i",$app->request()->post('componyid'));
		$sel_job->execute();
		$sel_job->store_result();
		if($sel_job->num_rows>0)
		{
			$sel_job->bind_result($id,$Role,$start_date,$end_date,$working_days,$location,$distance,$jobDescription,$compony_id,$lattitude,$longitude,$additional_information);
			while($sel_job->fetch())
			{
			   
					$jobs[] = array("id"=>$id,"Role" => $Role, "start_date" => $start_date, "end_date" => $end_date,"working_days"=>$working_days,"location"=>$location,"distance"=>$distance,"jobDescription"=>$jobDescription,"compony_id"=>$compony_id,"lattitude"=>$lattitude,"longitude"=>$longitude,"additional_information"=>$additional_information);
					$update_data=$mysqli->prepare("UPDATE tb_user SET Last_activity=?,Last_activity_date=? WHERE user_id=?");
					$update_data->bind_param("ssi",$activity,$date,$app->request()->post('componyid'));
					$update_data->execute();
					$post=array("result"=>true,"Details"=>$jobs);
					
			}
		
	   }
		else
		{
			$post['result']=false;
			$post['Error']="No data found";
		}
}
else
{
	$post['result']=false;
	$post['Error']="Invalid Call";
}
//$data="content-type:application/json";
	echo json_encode($post);
	//echo $data;
})->via('POST');
$app->run();
