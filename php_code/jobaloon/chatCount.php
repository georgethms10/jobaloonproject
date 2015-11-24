<?php
include('Slim/Slim.php');
\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim();
$app->map('/chatCount/', function() use ($app)
{
	include("connect.php");
	$id=$app->request()->post('userid');
	$sel_data=$mysqli->prepare("SELECT * FROM tb_user WHERE oauthToken=? AND user_id=?");
	$sel_data->bind_param("si",$app->request()->post('oauthToken'),$app->request()->post('userid'));
	$sel_data->execute();
	$sel_data->store_result();
	if($sel_data->num_rows>0)
	{
		
		$post['result']=true;
		$sel_chatCount=$mysqli->prepare("SELECT * FROM `tb_chat` WHERE `toId`=? AND `readUnread`='0'");
		$sel_chatCount->bind_param("i",$app->request()->post('userid'));
		$sel_chatCount->execute();
		$sel_chatCount->store_result();
		
		$post['Count']=$sel_chatCount->num_rows;
		$sel_visitCount=$mysqli->prepare("SELECT * FROM `tb_uservisit` WHERE `to_userid`=? AND `view_status`='0'");
		$sel_visitCount->bind_param("i",$app->request()->post('userid'));
		$sel_visitCount->execute();
		$sel_visitCount->store_result();
		$post['VisitorCount']=$sel_visitCount->num_rows;
		$sql=$mysqli->query("SELECT * FROM tb_user WHERE user_id='$id'");
		while($rows_user = $sql->fetch_assoc())
	    {
			$post['Details'] =$rows_user;
		}
		
	}
	else
	{
		$post['result']=false;
		$post['error']="Invalid Call";
	}
	//$data="content-type:application/json";
	echo json_encode($post);
	//echo $data;

})->via('POST');
$app->run();

?>