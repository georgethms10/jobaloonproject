<?php
include('Slim/Slim.php');
\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim();
$app->map('/samplevideo/', function() use ($app)
{
	include("connect.php");
    $sel_oath=$mysqli->prepare("SELECT * FROM tb_user WHERE oauthToken=? AND user_id=?");
	$sel_oath->bind_param("si",$app->request()->post('oauthToken'),$app->request()->post('userid'));
	$sel_oath->execute();
	$sel_oath->store_result();
	if($sel_oath->num_rows>0)
	{ 
		$select_video=$mysqli->query("SELECT sampleAppvideo,sampleVideoThumb FROM `tb_intialpage`");
		if(mysqli_num_rows($select_video)>0)
		{
			while($row = $select_video->fetch_assoc())
		   {
			  $video=$row;
		   }
		   $post['Result']=true;
		   $post['Details']=$video;
		}
		else
		{
			$post['Result']=false;
		    $post['Details']="";
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
