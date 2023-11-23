<?php
    require_once('dbN.php');

    $login = $_POST['login'];
    $password = $_POST['password'];

    $sql = "SELECT * FROM `users` WHERE login = '$login' AND password = '$password'";
    $result = $conn->query($sql);

    if ($result->num_rows > 0) 
    {
        while($row = $result->fetch_assoc()){
            echo "Добро пожаловать, " . $row['name'];
        }
    }
    else{
        echo "Нет такого пользователя";
    }
?>