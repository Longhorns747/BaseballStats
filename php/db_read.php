<?php
 
/*
 * Make a read request to the DB
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();

if (count($_GET) != 0) {
 
    // Make a request to the appropriate table
    $result = mysql_query("SELECT nameFirst, nameLast, (H / AB) AS AVG 
            FROM (master NATURAL JOIN batting) 
            WHERE (AVG >= .280) AND (yearID = 2012) AND (teamID = 'BOS') AND (AB > 50)");
 
    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
 
            $result = mysql_fetch_array($result);
 
            $stats = array();
            $stats["nameFirst"] = $result["nameFirst"];
            $stats["nameLast"] = $result["nameLast"];
            $stats["AVG"] = $result["AVG"];

            // success
            $response["success"] = 1;
 
            // user node
            $response["stats"] = array();
 
            array_push($response["stats"], $stats);
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no product found
            $response["success"] = 0;
            $response["message"] = "Bad stats search";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "Bad stats search";
 
        // echo no users JSON
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>