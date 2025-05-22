var ws;

function connect() {
    var username = document.getElementById("username").value;
    var wsserver = document.getElementById("wsserver").value;
    var url = wsserver + username;
    //var url = "ws://echo.websocket.org";

    ws = new WebSocket(url);

    ws.onmessage = function(event) { // Called when client receives a message from the server
        console.log(event.data);

        // display on browser
        var log = document.getElementById("log");
        log.innerHTML += "message from server: " + event.data + "\n";
    };

    ws.onopen = function(event) { // called when connection is opened
        var log = document.getElementById("log");
        log.innerHTML += "Connected to " + event.currentTarget.url + "\n";
    };
<<<<<<< HEAD:Experiments/aishaM/UNIT3/tutorials-unit3_1_one_websocket/Client_JS/websocket.js
=======
    //CHANGES BELOW:
        //having issues connecting to server? send message to user
    ws.onerror = function(event) {
            var log = document.getElementById("log");
            log.innerHTML += "Error: Could not connect to " + wsserver + "\n";
            console.error("WebSocket error:", event);
    };
        //display whether user is connected to server or not
    ws.onclose = function(event) {
            var log = document.getElementById("log");
            log.innerHTML += "Disconnected from server.\n";
<<<<<<< HEAD:Experiments/aishaM/UNIT3/tutorials-unit3_1_one_websocket/Client_JS/websocket.js
        };
>>>>>>> demo3_soma:Backend/src/main/java/backend/Chat/client/websocket.js
=======
    };
>>>>>>> soma_tests:Backend/src/main/java/backend/Chat/client/websocket.js
}

function send() {  // this is how to send messages
    var content = document.getElementById("msg").value;
    ws.send(content);
}
function sendGroupAction() {
    var groupAction = document.getElementById("groupAction").value;
    ws.send(groupAction);
}


