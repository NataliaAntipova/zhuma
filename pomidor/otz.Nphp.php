<?php
    require_once('dbN.php');

    $name = $_POST['name'];
    $email = $_POST['email'];
    $message = $_POST['message'];

    $sql = "INSERT INTO `connect` (name, email, message) VALUES ('$name', '$email', '$message')";

    if ($conn -> query($sql) === TRUE) {
        echo "Успешная отправка";
    }
    else {
        echo "Ошибка отправки";
    }
?>