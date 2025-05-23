let stompClient = null;
let chatroomId = null;
let userId = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#chatroomId").prop("disabled", connected);
    $("#userId").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function login() {
    let uid = $("#userId").val();
    // let data = qs.stringify({
    //     'username': $("#userId").val(),
    //     'password': "1234"
    // });

    fetch("http://localhost:8080/login", {
        method: "POST",
        body: `username=${uid}&password=1234`,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        accept: "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
        credentials: "include"

    }).then((loginRes) => {
        if (loginRes.status === 200) {
            console.log("login success");
        }
        console.log(loginRes);

    }).catch((loginError) => {
        console.log("loginError ", loginError);
    });
}

function logout() {
    const requestOptions = {
        method: "GET",
        redirect: "follow"
    };

    fetch("http://localhost:8080/logout", requestOptions)
        .catch((error) => console.error(error));
}

function connect() {
    chatroomId = $("#chatroomId").val();
    userId = $("#userId").val();
    if(chatroomId.length === 0 || userId.length === 0) {
        return;
    }


    stompClient = new StompJs.Client({
        brokerURL: 'ws://localhost:8080/gs-guide-websocket',
        connectHeaders: {
            'chatroomId': chatroomId,
            'userId': userId
        }
    });

    stompClient.onConnect = (frame) => {
        setConnected(true);
        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/' + chatroomId , (message) => {
            let body = JSON.parse(message.body);
            $("#greetings").append("<tr><td>" + body.fromUser + ": " + body.message + "</td></tr>");
        }, { ack: 'client' });
        stompClient.subscribe('/user/queue/' + chatroomId + '.private.messages', (message) => {
            let body = JSON.parse(message.body);
            $("#greetings").append("<tr><td>" + body.fromUser + ": " + body.message + "</td></tr>");
        }, { ack: 'client' });

        // stompClient.subscribe('/topic/greetings', (greeting) => {
        //     showGreeting(JSON.parse(greeting.body).content);
        // });
    };

    stompClient.onWebSocketError = (error) => {
        console.error('Error with websocket', error);
    };

    stompClient.onStompError = (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
    };

    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.publish({
        destination: "/chatroom/send.message",
        body: JSON.stringify({'message': $("#name").val()})
    });
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $( "#login" ).click(() => login());
    $( "#logout" ).click(() => logout());
    $( "#connect" ).click(() => connect());
    $( "#disconnect" ).click(() => disconnect());
    $( "#send" ).click(() => sendName());
});