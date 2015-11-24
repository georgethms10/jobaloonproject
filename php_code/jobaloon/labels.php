<?php
include('Slim/Slim.php');
\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim();
$app->map('/labels/', function() use ($app)
{
	include("connect.php");
	$sel_oath=$mysqli->prepare("SELECT * FROM tb_user WHERE oauthToken=? AND user_id=?");
	$sel_oath->bind_param("si",$app->request()->post('oauthToken'),$app->request()->post('userid'));
	$sel_oath->execute();
	$sel_oath->store_result();
	if($sel_oath->num_rows>0)
	{ 
		$Labels = array();
		$code=array();
		$result = false;
		$sql=$mysqli->query("SELECT DISTINCT(`Labels`) FROM `tb_labels` WHERE `type`='label'");
		if(mysqli_num_rows($sql)>0)
		{
			 while($row = $sql->fetch_assoc())
			 {
				$Labels[] = $row['Labels'];
			 }
			 $result = true;
		}
		 $sql_code=$mysqli->query("SELECT DISTINCT(`Labels`) FROM `tb_labels` WHERE `type`='code'");
		 if (mysqli_num_rows($sql_code) > 0)
		 {
				while ($rows = mysql_fetch_assoc($sql_code))
				{
					$code[] = $rows['Labels'];
				}
			   $result = true;
		  }
			$post['Result'] = $result;
			$post['Label'] = $Labels;
			$post['Code']  =$code;
	}
	else
	{
		$post['Result'] =false;
		$post['Error'] ="Invalid user";
		
	}
		echo json_encode($post);
})->via('POST');
$app->run();
