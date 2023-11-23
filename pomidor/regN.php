<?php
require_once('dbN.php');

$name = $_POST['name'];
$login = $_POST['login'];
$password = $_POST['password'];

$sql = "INSERT INTO `users` (name, login, password) VALUES ('$name', '$login', '$password')";

if ($conn -> query($sql) === TRUE) {
    echo "Успешная регистрация";
}
else {
    echo "Ошибка регистрации";
}



?>