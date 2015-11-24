<?php
include('Slim/Slim.php');
\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim();
$app->map('/viewAllJobs/', function() use ($app)
{
	
	include("connect.php");
	$sel_oath=$mysqli->prepare("SELECT * FROM tb_user WHERE oauthToken=? AND user_id=?");
	$sel_oath->bind_param("si",$app->request()->post('oauthToken'),$app->request()->post('userid'));
	$sel_oath->execute();
	$sel_oath->store_result();
	if($sel_oath->num_rows>0)
	{ 
	  	date_default_timezone_set('UTC');
		$date=date('Y-m-d H:i:s');
		$activity='viewed all jobs';
		$address = str_replace(" ", "+", $app->request()->post('fromaddress'));
		  $json = file_get_contents("http://maps.google.com/maps/api/geocode/json?address=$address&sensor=false");
		  $json = json_decode($json);
		  $mapLat = $json->{'results'}[0]->{'geometry'}->{'location'}->{'lat'};
		  $mapLong = $json->{'results'}[0]->{'geometry'}->{'location'}->{'lng'};
		
		  $update_data=$mysqli->prepare("UPDATE tb_user SET `Labels`=?,`Jobdescription`=? WHERE user_id=?");
		  $update_data->bind_param("ssi",$app->request()->post('label'),$app->request()->post('description'),$app->request()->post('userid'));
		  $update_data->execute();
		  $sql =$mysqli->prepare("SELECT a.`id`,a.`Role`,a.`start_date`,a.`end_date`,a.`working_days`,a.`location`,a.`distance`,a.`jobDescription`,a.`compony_id`,a.`lattitude`,a.`longitude`,a.`additional_information`, ( 3959 * acos( cos( radians('$mapLat') ) * cos( radians(a.`lattitude`) ) * cos( radians(a.`longitude`) - radians('$mapLong') ) + sin( radians('$mapLat') ) * sin( radians(a.`lattitude`) ) ) ) AS distances,b.user_name FROM `tb_jobpost` a  INNER JOIN `tb_user` b ON a.compony_id=b.user_id HAVING  a.`Role`=? ORDER BY a.`id` DESC");
		  $sql->bind_param("s",$app->request()->post('label'));
		  $sql->execute();
		  $sql->store_result();
		  if($sql->num_rows>0)
		  {
			 
				$sql->bind_result($id,$Role,$start_date,$end_date,$working_days,$location,$distance,$jobDescription,$compony_id,$lattitude,$longitude,$additional_information,$distances,$user_name);
				while($sql->fetch())
				{
					$jobs[] = array("id"=>$id,"Role" => $Role, "start_date" => $start_date, "end_date" => $end_date,"working_days"=>$working_days,"location"=>$location,"distance"=>$distance,"jobDescription"=>$jobDescription,"compony_id"=>$compony_id,"lattitude"=>$lattitude,"longitude"=>$longitude,"additional_information"=>$additional_information,"distances"=>$distances,"user_name"=>$user_name);
					
					
					  if($distances<$app->request()->post('distanceLimit')||$distances==null)
					  {
						 
						  if($distances==null)
						  {
							  $distances="0";
						  }
						   $sql_like=$mysqli->prepare("SELECT * FROM `tb_offerLike` WHERE `userid`=? AND `offerid`=?");
						   $sql_like->bind_param("ii",$app->request()->post('userid'),$id);
						   $sql_like->execute();
						   $sql_like->store_result();
						  
						  if($sql_like->num_rows==0)
						  {
							 $array=$jobs;
							 
				
						  }
					  }
					  
				}
				
				
				$update_datas=$mysqli->prepare("UPDATE tb_user SET Last_activity=?,Last_activity_date=? WHERE user_id=?");
				$update_datas->bind_param("ssi",$activity,$date,$app->request()->post('userid'));
				$update_datas->execute();
				$post["Result"]=true;
				$post["Details"]=$array;
		  }
		  else
		  {
			    $post["Result"]=false;
				$post["Details"]=array();
		  }
		  
		  
	}
	else
	{
		$post['Result']=false;
		$post['Error']="Invalid Call";
	}
	
	 echo json_encode($post);
})->via('POST');
$app->run();
