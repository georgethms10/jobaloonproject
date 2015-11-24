<?php
include('Slim/Slim.php');
\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim();
$app->map('/jobseekers/', function() use ($app)
{
	
	include("connect.php");
	$sel_data=$mysqli->prepare("SELECT * FROM tb_user WHERE oauthToken=? AND user_id=?");
	$sel_data->bind_param("si",$app->request()->post('oauthToken'),$app->request()->post('userid'));
	$sel_data->execute();
	$sel_data->store_result();
	if($sel_data->num_rows>0)
	{
		  $address = str_replace(" ", "+", $app->request()->post('location'));
		  $json = file_get_contents("http://maps.google.com/maps/api/geocode/json?address=$address&sensor=false");
		  $json = json_decode($json);
		  $lattitude = $json->{'results'}[0]->{'geometry'}->{'location'}->{'lat'};
		  $longitude = $json->{'results'}[0]->{'geometry'}->{'location'}->{'lng'};
		  $uid=$app->request()->post('userid');
		  $jobtype=$app->request()->post('jobtype');
		  $distance=$app->request()->post('distance');
		  $location=$app->request()->post('location');
		  if(!empty($uid))
		  {
			  
				 $sel_users=$mysqli->prepare("SELECT to_userid FROM tb_Like WHERE from_userid=?");
				 $sel_users->bind_param("i",$app->request()->post('userid'));
				 $sel_users->execute();
				 $sel_users->store_result();
				 if($sel_users->num_rows>0)
				 {
					  $sel_users->bind_result($db1);
					  while($sel_users->fetch())
					  {
						  
						  $touid[]=$db1;
					  }
					  $ToID=array_unique($touid);
					  if(count($ToID)>1)
					  {
						  $seekerId=join(',',$ToID);
					  }
					  else
					  {
						  $seekerId=$ToID[0];
					  }
			}
			else
			{
				$seekerId="";
			}
		  }
		  
		  if($seekerId!="")
		  {
			  
			  $sel_seekers=$mysqli->query("SELECT * FROM tb_user WHERE user_address='$location' AND job_type='$jobtype' AND user_id NOT IN($seekerId) AND user_type='jobseeker'");
		  }
		  else
		  {
			  
			  $sel_seekers=$mysqli->query("SELECT * FROM tb_user WHERE user_address='$location' AND job_type='$jobtype' AND user_type='jobseeker'");
		  }
		  ;
	  if(mysqli_num_rows($sel_seekers)>0)
	  {
		  while($row=$sel_seekers->fetch_assoc())
		  {
			  
			$Distance=getDistanceBetweenPointsNew($lattitude, $longitude, $row['lattitude'], $row['longitude'], 'Mi');
		   
			if($Distance<=$distance)
			{
				$row['distance']=$Distance;
				$Seekers[]=$row;
			}
		  }
		  $post=array("result"=>"success","Seekers"=>$Seekers);
	  }
	  else
	  {
		  $post['result']="failed";
		  $post['Error']="No data found";
	  }
}
else
{
	$post['result']="failed";
	$post['Error']="Invalid Call";
}
	
echo json_encode($post);
})->via('POST');
$app->run();

function getDistanceBetweenPointsNew($latitude1, $longitude1, $latitude2, $longitude2, $unit = 'Mi')
    {
        $latitude1  = doubleval($latitude1);
        $longitude1 = doubleval($longitude1);
        $latitude2  = doubleval($latitude2);
        $longitude2 = doubleval($longitude2);

        $theta = $longitude1 - $longitude2;
        $distance = (sin(deg2rad($latitude1)) * sin(deg2rad($latitude2))) +
            (cos(deg2rad($latitude1)) * cos(deg2rad($latitude2)) *
                cos(deg2rad($theta)));
        $distance = acos($distance);
        $distance = rad2deg($distance);
        $distance = $distance * 60 * 1.1515;
        switch($unit) {
            case 'Mi':
                break;
            case 'Km' :
                $distance = $distance * 1.609344;
        }
        $explode_distance=explode(".",$distance);
        if($distance<1)
        {
            return $distance;
        }
        if($explode_distance[1]>=7)
        {
            return (ceil($distance));
        }
        else{
            return (round($distance));
        }
    }
?>