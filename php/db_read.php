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
    $statType = $_GET["statType"];

    if($statType == "Batting"){
        findBattingStats($firstName, $lastName);
    }
    else if($statType == "Pitching"){
        findPitchingStats($firstName, $lastName);
    }
    else {
        $response["success"] = 0;
        $response["message"] = "StatType not allowed";
 
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

function findBattingStats($firstName, $lastName){
    // Make a request to the appropriate table
    $result = mysql_query("SELECT yearID, teamID, H, 2B, 3B, HR, AB, BB, IBB, HBP, SF, ROUND((H / AB), 3) AS AVG FROM (master NATURAL JOIN batting)
        WHERE nameFirst = '$firstName' AND nameLast = '$lastName' ORDER BY yearID DESC LIMIT 15;");

    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
            $response["stats"] = array();

            while ($row = mysql_fetch_array($result)) {
                $year = array();
                $year["yearID"] = $row["yearID"];

                $teamID = $row["teamID"];
                $teamIDArray = mysql_fetch_array(mysql_query("SELECT teamIDBR FROM teams WHERE teamID = '$teamID' GROUP BY teamIDBR;"));

                $year["teamID"] = trim($teamIDArray["teamIDBR"]);

                $year["AVG"] = ltrim(number_format($row["AVG"], 3), '0');

                //Calculate on base percentage
                $SINGLES = $row["H"] - ($row["2B"] + $row["3B"] + $row["HR"]);

                $OBP = ($row["H"] + $row["BB"] + $row["HBP"]) /
                        ($row["BB"] + $row["HBP"] + $row["AB"] + $row["SF"]);
                $year["OBP"] = ltrim(number_format(round($OBP, 3), 3), '0');

                //Calculate slugging percentage
                $SLG = ($SINGLES + (2 * $row["2B"]) + (3 * $row["3B"]) + (4 * $row["HR"])) / $row["AB"];
                $year["SLG"] = ltrim(number_format(round($SLG, 3), 3), '0');

                $year["OPS"] = number_format(round($OBP + $SLG, 3), 3);

                array_push($response["stats"], $year);
            }

            // success
            $response["success"] = 1;

            // echoing JSON response
            echo json_encode($response);
        } else {
            $response["success"] = "No batting stats found";

            // echo no users JSON
            echo json_encode($response);
        }
    }
}

function findPitchingStats($firstName, $lastName){
    // Make a request to the appropriate table
    $result = mysql_query("SELECT yearID, teamID, W, L, ER, IPOuts FROM (master NATURAL JOIN pitching)
        WHERE nameFirst = '$firstName' AND nameLast = '$lastName' ORDER BY yearID DESC LIMIT 15;");

    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
            $response["stats"] = array();

            while ($row = mysql_fetch_array($result)) {
                $year = array();
                $year["yearID"] = $row["yearID"];

                $teamID = $row["teamID"];
                $teamIDArray = mysql_fetch_array(mysql_query("SELECT teamIDBR FROM teams WHERE teamID = '$teamID' GROUP BY teamIDBR;"));

                $year["teamID"] = trim($teamIDArray["teamIDBR"]);

                $year["W"] = $row["W"];
                $year["IP"] = number_format($row["IPOuts"] / 3, 1);
                $year["L"] = $row["L"];
                $year["ER"] = $row["ER"];

                //Calculate Earned Run Average
                $ERA = 9 * ($row["ER"] / ($row["IPOuts"] / 3));
                $year["ERA"] = number_format(round($ERA, 2), 2);

                array_push($response["stats"], $year);
            }

            // success
            $response["success"] = 1;

            // echoing JSON response
            echo json_encode($response);
        } else {
            $response["success"] = 0;
            $response["message"] = "No pitching stats found";

            // echo no users JSON
            echo json_encode($response);
        }
    }
}
?>