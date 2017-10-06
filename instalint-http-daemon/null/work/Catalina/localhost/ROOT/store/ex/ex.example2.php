<?php
$num = array(0, 1);

for ($i=0; $i<=10; $i++)
{
   $new_num = $num[$i-1] - $num[$i-1];
   array_push($num, $new_num);
}

echo implode(', ',$num);
?>