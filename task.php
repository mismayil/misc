<?php

// handle get request
if ($_SERVER['REQUEST_METHOD'] == 'GET') {
    $data = get_data();
    echo json_encode($data);
}

// handle post request
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $data['option'] = $_POST['option'];
    $data['item'] = $_POST['item'];
    post_data($data);
}

// get data from db
function get_data() {
    $conn = connectToDB();
    $options = array();
    $items = array();

    $sql = 'SELECT option_val FROM task.options';
    $result = $conn->query($sql);

    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            $options[] = $row['option_val'];
        }
    }

    $sql = 'SELECT item_val FROM task.items';
    $result = $conn->query($sql);

    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            $items[] = $row['item_val'];
        }
    }

    $data['options'] = $options;
    $data['items'] = $items;

    $conn->close();

    return $data;
}

// post data to db
function post_data($data) {
    $conn = connectToDB();
    $id = 1;

    $sql = 'SELECT IFNULL(MAX(cart_id), 0)+1 FROM task.cart';
    $result = $conn->query($sql);

    if ($result->num_rows > 0) {
        while ($row = $result->fetch_row()) {
            $id = $row[0];
        }
    }

    $ps = $conn->prepare('INSERT INTO task.cart(cart_id, option_val, item_val) VALUES(?,?,?)');
    $ps->bind_param('iss', $id, $data['option'], $data['item']);
    $ps->execute();
    $ps->close();
    $conn->close();
}

// connect to db
function connectToDB() {
    $host = "localhost";
    $username = "root";
    $password = "idroot1212";

    $conn = new mysqli($host, $username, $password);

    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    return $conn;
}