<?php
include('Slim/Slim.php');
\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim();
$app->map('/viewProfileVisitors/', function() use ($app)
{
	include("connect.php");
	$sel_oath=$mysqli->prepare("SELECT * FROM tb_user WHERE oauthToken=? AND user_id=?");
	$sel_oath->bind_param("si",$app->request()->post('oauthToken'),$app->request()->post('userid'));
	$sel_oath->execute();
	$sel_oath->store_result();
	if($sel_oath->num_rows>0)
	{ 
		$activity='viewed the profile visitors';
	    date_default_timezone_set('UTC');
		$date=date('Y-m-d H:i:s');
		$sql=$mysqli->prepare("SELECT DISTINCT (`from_userid`)FROM  `tb_uservisit` WHERE  `to_userid` =? AND `from_userid`!=? ORDER BY  `date` DESC LIMIT ?,10");
		$sql->bind_param("iii",$app->request()->post('userid'),$app->request()->post('userid'),$app->request()->post('index'));
		$sql->execute();
		$sql->store_result();
		if($sql->num_rows>0)
		{
			$sql->bind_result($from_userid);
			while($sql->fetch())
			{
				
				
				$get_data=$mysqli->prepare("SELECT user_name FROM `tb_user` WHERE `user_id`=?");
				$get_data->bind_param("i",$from_userid);
				$get_data->execute();
				$get_data->store_result();
				
				if($get_data->num_rows>0)
				{
						$get_data->bind_result($user_name);
						while($get_data->fetch())
						{
							
							$gtdate=$mysqli->prepare("SELECT `date` FROM `tb_uservisit` WHERE `from_userid`=? AND `to_userid`=? ORDER BY `id` DESC LIMIT 1");
							$gtdate->bind_param("ii",$from_userid,$app->request()->post('userid'));
							$gtdate->execute();
							$gtdate->store_result();
							
							$gtdate->bind_result($dates);
							while($gtdate->fetch())
							{
								
								$date = new DateTime($dates, new DateTimeZone($app->request()->post('fromtimezone')));
								$date->setTimezone(new DateTimeZone($app->request()->post('timezone')));
								$time= $date->format('Y-m-d H:i:s A');
								
								$unseen_count=$mysqli->prepare("SELECT * FROM `tb_uservisit` WHERE `from_userid`=? AND `to_userid`=? AND `view_status`='0'");
								$unseen_count->bind_param("ii",$from_userid,$app->request()->post('userid'));
								$unseen_count->execute();
								$unseen_count->store_result();
								$count=$unseen_count->num_rows;
								$array[]=array("from_userid"=>$from_userid,"user_name"=>$user_name,"date"=>$time,"count"=>$count);
								$updatedata=$mysqli->prepare("UPDATE tb_user SET Last_activity=?,Last_activity_date=? WHERE user_id=?");
								$updatedata->bind_param("ssi",$activity,$date,$app->request()->post('userid'));
								$updatedata->execute();
								
							}
						}
						
				}
				
				
			}
			if(empty($array))
			{
				$post['Result']=false;
				$post['Details']=array();
			}
			else
			{
				$post['Result']=true;
				$post['Details']=$array;
			}
		}
		
		else
		{
			$post['Result']=false;
			$post['Error']="No data found";
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
