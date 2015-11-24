<?php
include('Slim/Slim.php');
\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim();
$app->map('/userRegistration/', function() use ($app)
{
	include("connect.php");
	$selt_mail=$mysqli->prepare("SELECT * FROM tb_user WHERE user_email=?");
	$selt_mail->bind_param('s', $app->request()->post('user_mail'));
    $selt_mail->execute();
	$selt_mail->store_result();
	if($selt_mail->num_rows==0)
	{
		  if(!empty($_FILES['video']['name']))
		  {
			  $video =$_FILES['video']['name'];
			  $path = "../uploads/videos/";
			  $Video = $path . $video;
			  move_uploaded_file($_FILES['video']['tmp_name'], $Video);
			  
		  }
		  else
		  {
			  $video ="";
		  }
		  if(!empty($_FILES['image']['name']))
		  {
			  $image =$_FILES['image']['name'];
			  $path = "../uploads/images/";
			  $image_orginal = $path . $image;
			  move_uploaded_file($_FILES['image']['tmp_name'], $image_orginal);
		  }
		  else
		  {
			  $image="";
		  }
		  $address = str_replace(" ", "+", $app->request()->post('user_address'));
		  $json = file_get_contents("http://maps.google.com/maps/api/geocode/json?address=$address&sensor=false");
		  $json = json_decode($json);
		  $lattitude = $json->{'results'}[0]->{'geometry'}->{'location'}->{'lat'};
		  $longitude = $json->{'results'}[0]->{'geometry'}->{'location'}->{'lng'};
		  
		  date_default_timezone_set('UTC');
		  $date=date('Y-m-d H:i:s');
		  $activity="registered";
		   $oauth_token   = sha1(str_shuffle(mt_rand(10000,99999).str_shuffle($date.str_shuffle($app->request()->post('user_mail')))));	
		  if($app->request()->post('user_type')=="Jobseeker")
		  {
				$sql=$mysqli->prepare("INSERT INTO `tb_user`(`user_name`,`user_email`,`password`,`user_address`,`chat_notification`,`profile_visitor`,`user_type`,`dateOfJoin`,`Last_activity`,`Last_activity_date`,`lattitude`,`longitude`,`video`,`image`,`deviceToken`,`oauthToken`,`user_status`,`experience1`,`experience2`,`experience3`,`distance`,`job_type`)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				$sql->bind_param("ssssssssssssssssssssss",$app->request()->post('user_name'),$app->request()->post('user_mail'),$app->request()->post('user_password'),$app->request()->post('user_address'),$app->request()->post('chat_notification'),$app->request()->post('profile_visitor'),$app->request()->post('user_type'),$date,$activity,$date,$lattitude,$longitude,$video,$image,$app->request()->post('deviceToken'),$oauth_token,$app->request()->post('userStatus'),$app->request()->post('experience1'),$app->request()->post('experience2'),$app->request()->post('experience3'),$app->request()->post('distance'),$app->request()->post('job_type'));
	   }
	   else
	   {
		   
				 $sql=$mysqli->prepare("INSERT INTO `tb_user`(`user_name`,`user_email`,`password`,`user_address`,`chat_notification`,`user_type`,`dateOfJoin`,`Last_activity`,`Last_activity_date`,`lattitude`,`longitude`,`video`,`image`,`deviceToken`,`oauthToken`,`user_status`,`distance`,`job_type`)VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				$sql->bind_param("ssssssssssssssssss",$app->request()->post('user_name'),$app->request()->post('user_mail'),$app->request()->post('user_password'),$app->request()->post('user_address'),$app->request()->post('chat_notification'),$app->request()->post('user_type'),$date,$activity,$date,$lattitude,$longitude,$video,$image,$app->request()->post('deviceToken'),$oauth_token,$app->request()->post('userStatus'),$app->request()->post('distance'),$app->request()->post('job_type'));
	   }
	  $sql->execute();
	  $uid=$mysqli->insert_id;
	  $get_data=$mysqli->query("SELECT * FROM tb_user WHERE user_id='$uid'");
	  $row = $get_data->fetch_assoc();
      $post['Result']=true;
	   $post['Details'] = $row;
		
	 

}

else
{
	$post['Result']=false;
}
header("content-type:application/json");
echo json_encode($post);
//echo $data;

})->via('POST');
$app->run();

?>
