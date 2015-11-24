<?php
include('Slim/Slim.php');
\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim();
$app->map('/likeuser/', function() use ($app)
{
	include("connect.php");
	$sel_oath=$mysqli->prepare("SELECT * FROM tb_user WHERE oauthToken=? AND user_id=?");
	$sel_oath->bind_param("si",$app->request()->post('oauthToken'),$app->request()->post('fromid'));
	$sel_oath->execute();
	$sel_oath->store_result();
	
	if($sel_oath->num_rows>0)
	{ 
		$sql=$mysqli->prepare("SELECT * FROM `tb_Like` WHERE `from_userid`=? AND `to_userid`=? AND `offerid`=? AND `status`=?");
		$sql->bind_param("iiis",$app->request()->post('fromid'),$app->request()->post('toid'),$app->request()->post('offerid'),$app->request()->post('status'));
		$sql->execute();
		$sql->store_result();
		
		if($sql->num_rows==0)
		{
			
			$insert_data=$mysqli->prepare("INSERT INTO tb_Like(`from_userid`,`to_userid`,`offerid`,`status`)VALUES(?,?,?,?)");
			$insert_data->bind_param("iiis",$app->request()->post('fromid'),$app->request()->post('toid'),$app->request()->post('offerid'),$app->request()->post('status'));
			$insert_data->execute();
			$select_searchRequest = $mysqli->prepare("SELECT a.user_name,b.id,b.JobProfile,b.Schedule,b.Weekdays,b.HourlySalary,b.DistanceLimit,b.FromAddress,b.UrgentMode,b.compony_id,b.type FROM `tb_user` a INNER JOIN `tb_search` b ON a.`user_id`=b.`compony_id` WHERE b.`id`=?");
			$select_searchRequest->bind_param("i",$app->request()->post('searchid'));
			$select_searchRequest->execute();
			$select_searchRequest->store_result();
		  
			$select_searchRequest->bind_result($user_name,$id,$JobProfile,$Schedule,$Weekdays,$HourlySalary,$DistanceLimit,$FromAddress,$UrgentMode,$compony_id,$type);
				while($select_searchRequest->fetch())
				{
					
					$message = "Hola, somos " . $user_name . ",y estamos buscando 1 " . $JobProfile . " para " . $Weekdays . " , de  " . $Schedule . ". El trabajo tendrá lugar en  " . $FromAddress . ",  y el salario será de " . $HourlySalary . " por hora. Te necesitamos " . $UrgentMode . ".";
				}
				
				$select_user=$mysqli->prepare("SELECT user_name FROM tb_user WHERE user_id =?");
				$select_user->bind_param("i",$app->request()->post('toid'));
				$select_user->execute();
				$select_user->store_result();
				$select_user->bind_result($db1);
				$activity=$app->request()->post('status')." "."the user".'<b>'.$db1.'</b>';
				date_default_timezone_set('UTC');
				$date=date('Y-m-d H:i:s');
				$update_data=$mysqli->prepare("UPDATE tb_user SET Last_activity=?,Last_activity_date=? WHERE user_id=?");
				$update_data->bind_param("ssi",$activity,$date,$app->request()->post('fromid'));
				$update_data->execute();
				$post['Result'] = true;
				$post['Details']=$message;
		   
		}
		else
		{
				$post['Result'] = false;
				$post['Details']="";
		   
		}
}
else
{
$post['Result']="false";
$post['Error']="Invalid user";
}
		//$data="content-type:application/json";
		echo json_encode($post);
		//echo $data;
	
		
})->via('POST');
$app->run();
