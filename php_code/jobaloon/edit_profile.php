<?php
include('Slim/Slim.php');
\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim();
$app->map('/editProfile/', function() use ($app)
{
	include("connect.php");
	$sel_data=$mysqli->prepare("SELECT * FROM tb_user WHERE oauthToken=? AND user_id=?");
	$sel_data->bind_param("si",$app->request()->post('oauthToken'),$app->request()->post('userid'));
	$sel_data->execute();
	$sel_data->store_result();
	
	if($sel_data->num_rows>0)
	{
		$activity='profile updated';
	    date_default_timezone_set('UTC');
		$date=date('Y-m-d H:i:s');
		$address = str_replace(" ", "+", $app->request()->post('user_address'));
		  $json = file_get_contents("http://maps.google.com/maps/api/geocode/json?address=$address&sensor=false");
		  $json = json_decode($json);
		  $lattitude = $json->{'results'}[0]->{'geometry'}->{'location'}->{'lat'};
		  $longitude = $json->{'results'}[0]->{'geometry'}->{'location'}->{'lng'};
		$update_data=$mysqli->prepare("UPDATE tb_user SET user_name=?,user_email=?,password=?,user_address=?,chat_notification=?,lattitude=?,longitude=?,Last_activity=?,Last_activity_date=? WHERE user_id=?");
		$update_data->bind_param("sssssssssi",$app->request()->post('user_name'),$app->request()->post('user_email'),$app->request()->post('password'),$app->request()->post('user_address'),$app->request()->post('chat_notification'),$lattitude,$longitude,$activity,$date,$app->request()->post('userid'));
		$update_data->execute();
		$id=$app->request()->post('userid');
		$sql=$mysqli->query("SELECT * FROM tb_user WHERE user_id='$id'");
		while($rows_user = $sql->fetch_assoc())
	    {
		    
			$details =$rows_user;
	    }
		$post['Result'] = true;
        $post['Details'] = $details;
		
	}
	else
	{
		$post['result']=false;
		$post['error']="Validation Failed";
	}
	
	
	echo json_encode($post);
	

})->via('POST');
$app->run();
