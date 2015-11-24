<?php
include('Slim/Slim.php');
\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim();
//IntialPage
$app->map('/jobseekerEdit/', function() use ($app)
{
	include("connect.php");
    $sel_oath=$mysqli->prepare("SELECT * FROM tb_user WHERE oauthToken=? AND user_id=?");
	$sel_oath->bind_param("si",$app->request()->post('oauthToken'),$app->request()->post('userid'));
	$sel_oath->execute();
	$sel_oath->store_result();
	if($sel_oath->num_rows>0)
	{ 
	    
		  $activity='profile updated';
	      date_default_timezone_set('UTC');
		  $date=date('Y-m-d H:i:s');
		  $address = str_replace(" ", "+", $app->request()->post('user_address'));
		  $json = file_get_contents("http://maps.google.com/maps/api/geocode/json?address=$address&sensor=false");
		  $json = json_decode($json);
		  $lattitude = $json->{'results'}[0]->{'geometry'}->{'location'}->{'lat'};
		  $longitude = $json->{'results'}[0]->{'geometry'}->{'location'}->{'lng'};
		 
		  if(!empty($_FILES['video']['name']))
		  {
			  $video =$_FILES['video']['name'];
			  $path = "../uploads/videos/";
			  $Video = $path . $video;
			  move_uploaded_file($_FILES['video']['tmp_name'], $Video);
			  $update_video=$mysqli->prepare("UPDATE tb_user SET video=? WHERE user_id=?");
			  $update_video->bind_param("si",$video,$app->request()->post('userid'));
			  $update_video->execute();
			  
		  }
		  
		  if(!empty($_FILES['image']['name']))
		  {
			 
			  $image =$_FILES['image']['name'];
			  $path = "../uploads/images/";
			  $image_orginal = $path . $image;
			  move_uploaded_file($_FILES['image']['tmp_name'], $image_orginal);
			  $update_img=$mysqli->prepare("UPDATE tb_user SET image=? WHERE user_id=?");
			  $update_img->bind_param("si",$image,$app->request()->post('userid'));
			  $update_img->execute();
		  }
		  
		  
		$update_data=$mysqli->prepare("UPDATE tb_user SET user_name=?,user_email=?,password=?,user_address=?,chat_notification=?,profile_visitor=?,Jobdescription=?,code=?,Labels=?,lattitude=?,longitude=?,Last_activity=?,Last_activity_date=? WHERE user_id=?");
		$update_data->bind_param("sssssssssssssi",$app->request()->post('user_name'),$app->request()->post('user_email'),$app->request()->post('password'),$app->request()->post('user_address'),$app->request()->post('chat_notification'),$app->request()->post('profile_visitor'),$app->request()->post('Jobdescription'),$app->request()->post('code'),$app->request()->post('Labels'),$lattitude,$longitude,$activity,$date,$app->request()->post('userid'));
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
		$post['Result']=false;
		$post['Error']="InvalidCall";
	}
	echo json_encode($post);
})->via('POST');
$app->run();

