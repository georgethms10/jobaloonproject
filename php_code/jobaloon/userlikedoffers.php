<?php
include('Slim/Slim.php');
\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim();
$app->map('/userlikedoffers/', function() use ($app)
{
	include("connect.php");
	$sel_oath=$mysqli->prepare("SELECT * FROM tb_user WHERE oauthToken=? AND user_id=?");
	$sel_oath->bind_param("si",$app->request()->post('oauthToken'),$app->request()->post('userid'));
	$sel_oath->execute();
	$sel_oath->store_result();
	if($sel_oath->num_rows>0)
	{ 
		$get_data=$mysqli->prepare("SELECT a.id,a.Role,a.start_date,a.end_date,a.working_days,a.location,a.distance,a.jobDescription,a.compony_id,a.lattitude,a.longitude,a.additional_information FROM `tb_jobpost` a INNER JOIN `tb_offerLike` b ON a.id=b.offerid WHERE b.userid=? AND b.status='Like'");
		$get_data->bind_param("i",$app->request()->post('userid'));
		$get_data->execute();
		$get_data->store_result();
		
		if($get_data->num_rows>0)
		{
			
			$get_data->bind_result($id,$Role,$start_date,$end_date,$working_days,$location,$distance,$jobDescription,$compony_id,$lattitude,$longitude,$additional_information);
			while($get_data->fetch())
			{
				$getname=$mysqli->prepare("SELECT user_name FROM tb_user WHERE user_id=?");
				$getname->bind_param("i",$compony_id);
				$getname->execute();
				$getname->store_result();
				$getname->bind_result($comapnyName);
				while($getname->fetch())
				{
					$jobs[]=array("id"=>$id,"Role"=>$Role,"start_date"=>$start_date,"end_date"=>$end_date,"working_days"=>$working_days,"location"=>$location,"distance"=>$distance,"jobDescription"=>$jobDescription,"compony_id"=>$compony_id,"lattitude"=>$lattitude,"longitude"=>$longitude,"additional_information"=>$additional_information,"company_name"=>$comapnyName);
					
				}
				
			}
			
			$post['Result']=true;
			$post['Details']=$jobs;
		}
		else
		{
			$post['Result']=false;
			$post['Details']=array();
		}
	}
	else
	{
		$post['Result']=false;
		$post['Error']="InvalidCall";
	}
	echo json_encode($post);
})->via('POST');
$app->run();
