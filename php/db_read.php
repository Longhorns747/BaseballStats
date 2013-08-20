<?php
 
/*
 * Make a read request to the DB
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once 'db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();

if (count(array_values($_GET)) > 0) {
    $firstName = $_GET["firstName"];
    $lastName = $_GET["lastName"];
 
    // Make a request to the appropriate table
    $result = mysql_query("SELECT yearID, ROUND((H / AB), 3) AS AVG FROM (master NATURAL JOIN batting)
        WHERE nameFirst = '$firstName' AND nameLast = '$lastName' ORDER BY yearID DESC LIMIT 15;");
 
    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
            $response["stats"] = array();

            while ($row = mysql_fetch_array($result)) {
                $year = array();
                $year["yearID"] = $row["yearID"];
                $year["AVG"] = $row["AVG"];
                array_push($response["stats"], $year);
            }

            // success
            $response["success"] = 1;
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no product found
            $response["success"] = 0;
            $response["message"] = "Bad stats search A";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "Bad stats search B";
 
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