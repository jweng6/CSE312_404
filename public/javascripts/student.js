

function check(input) {
    if (input.value !== document.getElementById('password').value) {
        input.setCustomValidity('Password Must be Matching.');
    } else {
        // input is valid -- reset the error message
        input.setCustomValidity('');
    }
}

function select(id,code){
    document.getElementById("current_select_question_id").value = id;
    location.href = '/course/'+code +'/question/'+id;
}

// Establish a WebSocket connection with the server
// Allow users to send messages by pressing enter instead of clicking the Send button




class websocket extends Object {
    constructor(courseId) {
        super();
        this.socket = new WebSocket('ws://' + window.location.host + '/ws'+courseId);
        // Called whenever data is received from the server over the WebSocket connection
        this.email = document.getElementById('current_user_email').innerHTML;

        this.socket.onmessage = function (ws_message) {
            const message = JSON.parse(ws_message.data);
            const messageType = message.messageType

            switch (messageType) {
                case 'chat':
                    addMessage(message);
                    break;
                case 'assign':
                    assign_question(message);
                    break;
                case 'status':
                    break;
                case 'answer':
                    break;
                case 'join':
                    addjoined(message);
                    break;
                default:
                    console.log("received an invalid WS messageType");
            }
        }
}
// Read the comment the user is sending to chat and send it to the server over the WebSocket as a JSON string
    sendMessage() {
        const chatBox = document.getElementById("chat-comment");
        const comment = chatBox.value;
        chatBox.value = "";
        chatBox.focus();
        if (comment !== "") {
            this.socket.send(JSON.stringify({'messageType':"chat",'email': this.email.toString(), 'comment': comment}));
        }
    }
    sendStatus() {
        const id = document.getElementById("current_select_question_id").value;
        this.socket.send(JSON.stringify({'messageType':"status", "live" : '0', "question": id}));
        return null;
    }
    sendAnswer() {
        const answerBox = document.getElementById("answerBox");
        const comment = answerBox.value.toUpperCase();
        answerBox.value = "";
        answerBox.focus();
        const id = document.getElementById("do_id").innerHTML;
        this.socket.send(JSON.stringify({'messageType':"answer", "email" : this.email, "question": id.toString(),"comment":comment}));
        return null;
    }
    sendJoined(){
        this.socket.send(JSON.stringify({'messageType':"join", "email" : this.email}));
    }

    sendTimeOut(id) {
        this.socket.send(JSON.stringify({'messageType':"timeOut", "question": id}));
    }
}

var join_code =document.getElementById("join_code").innerHTML.toString()
const ws = new websocket(join_code);


document.getElementById('chatSubmit_div').addEventListener("keypress", function (event) {
    if (event.code === "Enter") {
        ws.sendMessage();
    }
});

function addMessage(chatMessage) {
    const chat = document.getElementById('chat_all_message');
    let name = chatMessage.user.toLowerCase().split(" ");
    for (let i = 0; i < name.length; i++) {
        name[i] = name[i][0].toUpperCase() + name[i].substr(1);
    }
    let url = 'window.open("/profile/' +chatMessage.email.toString()+'")';

    name = name.join(" ");
    chat.innerHTML += '<div class="chat_message"><b class="pointer">' +'<a onclick=' + url + '>'+ name +'</a></b>' +' '+ chatMessage.current.slice(10, -3) +  '<div class="chat_message_white">'  + chatMessage.comment + ' </div>' +'<br>' +  '</div>';
    chat.scrollTop = chat.scrollHeight;
}

function addTimeUp(assign) {

    const chat = document.getElementById('chat_all_message');
    const now = new Date();
    const current = now.getHours() + ':' + now.getMinutes();
    chat.innerHTML += '<div class="chat_message">' + '<b>'+'Reminder </b>'+current+ '<div class="chat_message_white"> <b> Question:'  + assign.title + '</b><br>'+ 'Time UP!<br> The answers are graded'+ '</br>Click the show answer button will clear your chat message'+ ' </div>' +'<br>' +  '</div>';
    chat.scrollTop = chat.scrollHeight;
    ws.sendTimeOut(assign.question);

}



var showtime = function (endtime) {
    var nowtime = new Date()  //获取当前时间
    var lefttime = new Date(endtime).getTime() - nowtime.getTime(),  //距离结束时间的毫秒数
        lefth = Math.floor(lefttime/(1000*60*60)%24),  //计算小时数
        leftm = Math.floor(lefttime/(1000*60)%60),  //计算分钟数
        lefts = Math.floor(lefttime/1000%60);  //计算秒数
    return [lefth,leftm,lefts]   //返回倒计时的字符串
}

var timer = null;
var assignShowing = true;

function assign_question(assign){
    if (timer!==null){
        clearInterval(timer);
    }
    document.getElementById("show_answer_button").hidden = true;
    document.getElementById('left_retangle').classList.add('noClick');
    console.log(assign.title);
    const assignDiv = document.getElementById("assign_div");
    assignDiv.hidden = false;
    const showDiv = document.getElementById("show_div");
    showDiv.hidden = true;
    let time = document.getElementById('time_remaining');
    time.hidden = false;

    document.getElementById('left_retangle').classList.add('noClick');
    assignShowing = true;

    timer =setInterval (function (){
        let t =  showtime(assign.expire);
        if( t.reduce((a, b) => a + b, 0) >=  0) {
            time.innerHTML = t[0].toString() + ":" + t[1].toString()  + ":" + t[2].toString();
            const icon = '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="bi bi-chat-right" viewBox="0 0 16 16"> <pathd="M2 1a1 1 0 0 0-1 1v8a1 1 0 0 0 1 1h9.586a2 2 0 0 1 1.414.586l2 2V2a1 1 0 0 0-1-1H2zm12-1a2 2 0 0 1 2 2v12.793a.5.5 0 0 1-.854.353l-2.853-2.853a1 1 0 0 0-.707-.293H2a2 2 0 0 1-2-2V2a2 2 0 0 1 2-2h12z"/></svg>';
            document.getElementById("do_id").innerHTML = assign.question;
            document.getElementById('do_title').innerHTML = icon + assign.title;
            document.getElementById("do_details").value = assign.details;
            document.getElementById("do_a").innerHTML = assign.A;
            document.getElementById("do_b").innerHTML = assign.B;
            document.getElementById("do_c").innerHTML = assign.C;
            document.getElementById("do_d").innerHTML = assign.D;
        }
        else {
            setTimeout(function (){
                clearInterval(timer);
                document.getElementById("course_name").onclick;
                addTimeUp(assign);
                document.getElementById('left_retangle').classList.remove('noClick');
                document.getElementById("show_answer_button").hidden = false;
            },5);

            // setTimeout(function (){
            //     window.location.replace(document.location);
            // },1000*8);

        }
    } ,1000);

}

function show_answer(code){
    const show_answer_button = document.getElementById('show_answer_button');
    const id = document.getElementById('do_id').innerHTML;
    let url = '/course/'+code+'/question/'+id;
    window.location.replace(url);
}

function addjoined(user) {
    const chat = document.getElementById('chat_all_message');
    let name = user.name.toLowerCase().split(" ");
    for (let i = 0; i < name.length; i++) {
        name[i] = name[i][0].toUpperCase() + name[i].substr(1);
    }
    let url = 'window.open("/profile/' +ws.email.toString()+'")';
    name = name.join(" ");

    const now = new Date();
    const current = now.getHours() + ':' + now.getMinutes();
    chat.innerHTML += '<div class="chat_message" style="width:100%;text-align:center;display: inline-block;text-overflow: ellipsis;overflow: hidden;white-space: nowrap;" ><b class="pointer" style="color: #4A4A4A">' +'<a onclick=' + url + '>'+ name +'</a></b> Joined the chat' +'<br>' +  '</div>';
    chat.scrollTop = chat.scrollHeight;
}


ws.socket.addEventListener('open', (event) => {
    ws.sendJoined();
});

setInterval(function (){ws.sendStatus()}, 1000);
