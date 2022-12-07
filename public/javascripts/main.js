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
// Allow users to send messages by pressing enter instead of clicking the Send button
document.addEventListener("keypress", function (event) {
    if (event.code === "Enter") {
        var email = document.getElementById('current_user_email').innerHTML;

        ws.sendMessage(email);
    }
});

function addMessage(chatMessage) {
    const chat = document.getElementById('chat_all_message');
    let name = chatMessage.user.toLowerCase().split(" ");
    for (let i = 0; i < name.length; i++) {
        name[i] = name[i][0].toUpperCase() + name[i].substr(1);
    }
    name = name.join(" ");
    chat.innerHTML += '<div class="chat_message">' + '<b>'+ name +'</b>' +' '+ chatMessage.current.slice(10, -3) +  '<div class="chat_message_white">'  + chatMessage.comment + ' </div>' +'<br>' +  '</div>';
    chat.scrollTop = chat.scrollHeight;
}


class websocket extends Object {
    constructor(chat) {
        super();
        this.socket = new WebSocket('ws://' + window.location.host + '/'+ chat);
        // Called whenever data is received from the server over the WebSocket connection

        this.socket.onmessage = function (ws_message) {
            const message = JSON.parse(ws_message.data);
            const messageType = message.messageType
            console.log(message)
            console.log(messageType)
            switch (messageType) {
                case 'chat':
                    addMessage(message);
                    break;
                default:
                    console.log("received an invalid WS messageType");
            }
        }

}
// Read the comment the user is sending to chat and send it to the server over the WebSocket as a JSON string
    sendMessage(email) {
        const chatBox = document.getElementById("chat-comment");
        const comment = chatBox.value;
        chatBox.value = "";
        chatBox.focus();
        if (comment !== "") {
            this.socket.send(JSON.stringify({'messageType':"chat",'email': email.toString(), 'comment': comment}));
            //socket.send(JSON.stringify({'messageType':"status", "live" : "1/0" "question": "0" ));   //0 = open  1= close
            //socket.send(JSON.stringify({'messageType':"assign",'question': 1}));
            //socket.send(JSON.stringify({'messageType':"answer", 'email': email ,question:1, 'comment': comment }));
        }
    }

    sendStatus(status) {
        const id = document.getElementById("current_select_question_id").value;
        this.socket.send(JSON.stringify({'messageType':"status", "live" : status, "question": id}));
    }

    sendAssign(id){
        this.socket.send(JSON.stringify({'messageType':"assign",'question': id}));
    }



// Renders a new chat message to the page

}
