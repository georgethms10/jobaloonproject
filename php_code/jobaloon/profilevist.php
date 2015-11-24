<?php
include('Slim/Slim.php');
\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim();
$app->map('/profilevisit/', function() use ($app)
{
	include("connect.php");
	$sel_oath=$mysqli->prepare("SELECT * FROM tb_user WHERE oauthToken=? AND user_id=?");
	$sel_oath->bind_param("si",$app->request()->post('oauthToken'),$app->request()->post('from_userid'));
	$sel_oath->execute();
	$sel_oath->store_result();
	if($sel_oath->num_rows>0)
	{ 
		date_default_timezone_set('UTC');
		$date=date('Y-m-d H:i:s');
		$insert=$mysqli->prepare("INSERT INTO tb_uservisit(`from_userid`,`to_userid`,`date`)VALUES(?,?,?)");
		$insert->bind_param("iis",$app->request()->post('from_userid'),$app->request()->post('to_userid'),$date);
		$insert->execute();
		$select_Data=$mysqli->prepare("SELECT deviceToken,profile_visitor,user_name FROM tb_user WHERE user_id=?");
		$select_Data->bind_param("i",$app->request()->post('to_userid'));
		$select_Data->execute();
		$select_Data->store_result();
		$select_Data->bind_result($deviceToken,$profile_visitor,$user_name);
		while($select_Data->fetch())
		{
			if($profile_visitor=="ON")
			{
				 //android push notificatiojn
			
				 $type='Profile Visit';
				 $title='A company just visited your profile';
				$api_key = 'AIzaSyBk3SMjp0EQfEP3qOOWAyGzYFOuzK3_rkQ';
				$registrationIDs = array($deviceToken);
				$url = 'https://android.googleapis.com/gcm/send';
				$fields = array(
					'registration_ids'  => $registrationIDs,
					'data'              => array( 'Title' => $title,'userid'=>$app->request()->post('to_userid'),'type'=>$type),
				);
		
				$headers = array(
					'Authorization: key=' . $api_key,
					'Content-Type: application/json');
		
				$ch = curl_init();
				curl_setopt($ch, CURLOPT_URL, $url);
				curl_setopt( $ch, CURLOPT_POST, true );
				curl_setopt( $ch, CURLOPT_HTTPHEADER, $headers);
				curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );
				curl_setopt( $ch, CURLOPT_POSTFIELDS, json_encode( $fields ) );
				$result = curl_exec($ch);
				curl_close($ch);
			
				 // end push notification
			}
			$activity="visited the profile of"."".'<b>'.$user_name.'<b>';
			$update_data=$mysqli->prepare("UPDATE tb_user SET Last_activity=?,Last_activity_date=? WHERE user_id=?");
			$update_date->bind_param("ssi",$activity,$date,$app->request()->post('from_userid'));
			$update_date->execute();
				
		}
		 $post['Result']=true;
	}
	else
	{
		$post['Result']=false;
		$post['Error']="Invalid User";
	}
	 echo json_encode($post);
})->via('POST');
$app->run();
