<?php
include('Slim/Slim.php');
\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim();
//IntialPage
$app->map('/intial/', function() use ($app)
{
	include("connect.php");
	$sql=$mysqli->query("SELECT * FROM tb_intialpage");
    if(mysqli_num_rows($sql)>0)
	{
		 while($row = $sql->fetch_assoc())
         {
			$post['Result']="Success";
			$post['Details'] = $row;
		 }
		
	}
	else
	{
		$post['Result']="Failed";
	}
	$data="content-type:application/json";
	$data=json_encode($post);
	echo $data;

	
})->via('POST');
$app->run();

// userRegistration

include('Slim/Slim.php');
\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim();
$app->map('/Registration/', function() use ($app)
{
	echo "ha";
	//echo $app->request()->post('user_name');
})->via('POST');
$app->run();



/*$app->map('/user/Registration/', function() use ($app)
{
	//include("connect.php");
	//,`password`,`user_address`,`chat_notification`,`profile_visitor`,`user_type`,`dateOfJoin`,`Last_activity`,`Last_activity_date`,`lattitude`,`longitude`,`video`,`image`,`deviceToken`,`user_status`,`experience1`,`experience2`,`experience3`,`distance`
	//,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?  ,$app->request()->post('user_password'),$app->request()->post('user_address'),$app->request()->post('chat_notification'),$app->request()->post('profile_visitor'),$app->request()->post('user_type'),$app->request()->post('date'),$app->request()->post('deviceToken'),$app->request()->post('userStatus'),$app->request()->post('video'),$app->request()->post('image'),$app->request()->post('experience1'),$app->request()->post('experience2'),$app->request()->post('experience3'),$app->request()->post('distance'),$app->request()->post('job_type')
	echo $app->request()->post('user_name');
	/*$sql=$mysqli->prepare("INSERT INTO `tb_user`(`user_name`,`user_email`) VALUES(?,?)");
	$sql->bind_param("ss",$app->request()->post('user_name'),$app->request()->post('user_mail'));
$sql->execute();
	*/
	
/*})->via('POST');
$app->run();*/

?>