<?php

/**
* A class for connecting to the DB
*/

class DB_CONNECT {
 
    function __construct() {
        //Connect to database
        $this->connect();
    }

    function __destruct() {
        //Close db connection
        $this->close();
    }

    function connect() {
        //Import database connection variables
        require_once 'db_config.php';
 
        //Connecting to mysql database
        $con = mysql_connect(DB_SERVER, DB_USER, DB_PASSWORD) or die(mysql_error());
 
        //Selecing database
        $db = mysql_select_db(DB_DATABASE) or die(mysql_error()) or die(mysql_error());
 
        return $con;
    }

    function close() {
        //Close db connection
        mysql_close();
    }
 
}
 
?>