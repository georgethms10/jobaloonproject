<?php
include('Slim/Slim.php');
\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim();
$app->map('/likeoffer/', function() use ($app)
{
	include("connect.php");
	$sel_oath=$mysqli->prepare("SELECT * FROM tb_user WHERE oauthToken=? AND user_id=?");
	$sel_oath->bind_param("si",$app->request()->post('oauthToken'),$app->request()->post('userid'));
	$sel_oath->execute();
	$sel_oath->store_result();
	if($sel_oath->num_rows>0)
	{ 
	     
	    date_default_timezone_set('UTC');
		$date=date('Y-m-d H:i:s');
	    if($app->request()->post('type')=="")
		{
			
			  $seldata=$mysqli->prepare("SELECT * FROM `tb_offerLike` WHERE `userid`=? AND `offerid`=? AND `status`=?");
			  $seldata->bind_param("iis",$app->request()->post('userid'),$app->request()->post('offerid'),$app->request()->post('status'));  $seldata->execute();
			 $seldata->store_result();
			  if($seldata->num_rows==0)
			  {
				
				 $insert_data=$mysqli->prepare("INSERT INTO `tb_offerLike`( `userid`, `offerid`, `status`) VALUES (?,?,?)");
				 $insert_data->bind_param("iis",$app->request()->post('userid'),$app->request()->post('offerid'),$app->request()->post('status'));
				$insert_data->execute();
				  $select_offer_det=$mysqli->prepare("SELECT Role,compony_id FROM tb_jobpost WHERE id=?");
				  $select_offer_det->bind_param("i",$app->request()->post('offerid'));
				  $select_offer_det->execute();
				  $select_offer_det->store_result();
				  $select_offer_det->bind_result($role,$compony_id);
				  while($select_offer_det->fetch())
				  {
					  $select_comapny_det=$mysqli->prepare("SELECT user_name FROM tb_user WHERE user_id=?");
					  $select_comapny_det->bind_param("i",$compony_id);
					  $select_comapny_det->execute();
					  $select_comapny_det->store_result();
					  $select_comapny_det->bind_result($user_name);
					  while($select_comapny_det->fetch())
					  {
							
							$activity=$app->request()->post('status')." "."the offer".'<b>'.$role.'<b>'." "."of the comapny"." ".'<b>'.$user_name.'</b>';
						$updatedata=$mysqli->prepare("UPDATE tb_user SET Last_activity=?,Last_activity_date=? WHERE user_id=?");
						$updatedata->bind_param("ssi",$activity,$date,$app->request()->post('userid'));
						$updatedata->execute();
					  }
				  }
	  
				  $post['Result']=true;

				  
			  }
			  else
			  {
				   $post['Result']=false;
				   $post['Error']="you already"." ".$app->request()->post('status');

			  }
		}
		else if($app->request()->post('type')=="delete")
		{
			date_default_timezone_set('UTC');
			$date=date('Y-m-d H:i:s');
			$delete=$mysqli->prepare("DELETE FROM `tb_offerLike` WHERE `userid`=? AND `offerid`=?");
			$delete->bind_param("ii",$app->request()->post('userid'),$app->request()->post('offerid'));
			$delete->execute();
            $activity="deleted a offer";
            $updatedata=$mysqli->prepare("UPDATE tb_user SET Last_activity=?,Last_activity_date=? WHERE user_id=?");
			$updatedata->bind_param("ssi",$activity,$date,$app->request()->post('userid'));
			$updatedata->execute();
            $post['Result']=true;

            
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
?>