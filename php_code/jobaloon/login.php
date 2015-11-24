<?php
include('Slim/Slim.php');
\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim();
$app->map('/login/', function() use ($app)
{
	include("connect.php");
	$sel_data=$mysqli->prepare("SELECT user_id,user_email FROM tb_user WHERE user_email=? AND password=?");
	$sel_data->bind_param("ss",$app->request()->post('user_email'),$app->request()->post('user_password'));
	$sel_data->execute();
	$sel_data->store_result();
	
	if($sel_data->num_rows>0)
	{
		$sel_data->bind_result($db1,$db2);
		while($sel_data->fetch())
		{
			$id=$db1;
			$email=$db2;
		}
		
		$activity = "Login";
		date_default_timezone_set('UTC');
		$date=date('Y-m-d H:i:s');
		
      $oauth_token   = sha1(str_shuffle(mt_rand(10000,99999).str_shuffle($date.str_shuffle($email))));
	  
		$update_data=$mysqli->prepare("UPDATE tb_user SET deviceToken=?,Last_activity=?,Last_activity_date=?,oauthToken=? WHERE user_id=?");
		$update_data->bind_param("ssssi",$app->request()->post('deviceToken'),$activity,$date,$oauth_token,$id);
		$update_data->execute();
		$sql=$mysqli->query("SELECT * FROM tb_user WHERE user_id='$id'");
		while($rows_user = $sql->fetch_assoc())
	    {
		    $check_jobpost=$mysqli->query("SELECT * FROM `tb_jobpost` WHERE `compony_id`='$id'");
            if(mysqli_num_rows($check_jobpost))
            {
            	$rows_user['Jobpost']=true;
            }
            else
			{
                $rows_user['Jobpost']=false;
            }
            $sql_jobpost_Label=$mysqli->query("SELECT * FROM  `tb_jobpost` WHERE `id`='".$rows_user['JobId']."'");
            if(mysqli_num_rows($sql_jobpost_Label))
            {
				while($rows_jobpost_Label = $sql_jobpost_Label->fetch_assoc())
	    		{
					$rows_user['JobLabel']=$rows_jobpost_Label['Role'];
                	$rows_user['distance']=$rows_jobpost_Label['distance'];
                	$rows_user['location']=$rows_jobpost_Label['location'];
				}
               
            }
            else
			{
                $rows_user['JobLabel']="";
                $rows_user['distance']="";
                $rows_user['location']="";
            }
            $sql_get_last_searchid=$mysqli->query("SELECT * FROM `tb_search` WHERE `compony_id`='$id' ORDER BY `id` DESC LIMIT 1 ");
            if(mysqli_num_rows($sql_get_last_searchid))
            {
				while($rows_get_last = $sql_get_last_searchid->fetch_assoc())
				{
					$rows_user['lastSearchid']=$rows_get_last['id'];

				}
              
            }
            else
			{
                $rows_user['lastSearchid']="";

            }
			$details =$rows_user;
	    }
		$post['Result'] = true;
        $post['Details'] = $details;
		
	}
	else
	{
		$post['result']=false;
		$post['error']="invalid username and password";
	}
	
	//header("content-type:application/json");
	echo json_encode($post);
	//echo $data;

})->via('POST');
$app->run();
