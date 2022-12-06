function check(input) {
    if (input.value !== document.getElementById('password').value) {
        input.setCustomValidity('Password Must be Matching.');
    } else {
        // input is valid -- reset the error message
        input.setCustomValidity('');
    }
}

function select(id,code){
    document.getElementById("current_select_question_id").value = id
    location.href = '/course/'+code +'/question/'+id
}

// Establish a WebSocket connection with the server
const socket = new WebSocket('ws://' + window.location.host + '/ws');

let webRTCConnection;

// Allow users to send messages by pressing enter instead of clicking the Send button
document.addEventListener("keypress", function (event) {
    if (event.code === "Enter") {
        sendMessage();
    }
});



// Read the comment the user is sending to chat and send it to the server over the WebSocket as a JSON string
function sendMessage(email) {
    console.log(email);
    const chatBox = document.getElementById("chat-comment");
    const comment = chatBox.value;
    chatBox.value = "";
    chatBox.focus();
    if (comment !== "") {
        socket.send(JSON.stringify({'messageType':"chat",'email': email.toString(), 'comment': comment}));
        //socket.send(JSON.stringify({'messageType':"status", "live" : "1/0" "question": "0" ));   //0 = open  1= close
        //socket.send(JSON.stringify({'messageType':"assign",'question': 1}));
        //socket.send(JSON.stringify({'messageType':"answer", 'email': email ,question:1, 'comment': comment }));
    }
}

function sendStatus(socket,status) {
    const id = document.getElementById("current_select_question_id").value;
    socket.send(JSON.stringify({'messageType':"status", "live" : status, "question": id}));
}

function sendAssign(id){
    socket.send(JSON.stringify({'messageType':"assign",'question': id}));
}


// Renders a new chat message to the page
function addMessage(chatMessage) {
    let chat = document.getElementById('chat_all_message');
    chat.innerHTML += '<div className="chat_message">' + '<p><b>'+ chatMessage["user"] +'</b>' +' '+ chatMessage["current"].slice(10, -3) + '</p>'+ '<div className="chat_message_white">' + '<p>' + chatMessage["comment"] + '</p> </div> </div>'
}



// Called whenever data is received from the server over the WebSocket connection
socket.onmessage = function (ws_message) {
    const message = JSON.parse(ws_message.data);
    const messageType = message.messageType
    console.log(message)

    switch (messageType) {
        case 'chatMessage':
            addMessage(message);
            break;
        default:
            console.log("received an invalid WS messageType");
    }
}
