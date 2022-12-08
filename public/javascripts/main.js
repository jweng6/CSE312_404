history.pushState(null, null, document.URL);
window.addEventListener('popstate', function () {
    history.pushState(null, null, document.URL);
})

function check(input) {
    if (input.value !== document.getElementById('password').value) {
        input.setCustomValidity('Password Must be Matching.');
    } else {
        // input is valid -- reset the error message
        input.setCustomValidity('');
    }
}


function selects(id,code){
    document.getElementById("current_select_question_id").value = id;
    window.location.replace("/course/'+code.toString() +'/question/'+id.toString()") ;

}



// Establish a WebSocket connection with the server
// Allow users to send messages by pressing enter instead of clicking the Send button


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
    var currentDateTime = new Date();
    const now = new Date();
    const current = now.getHours() + ':' + now.getMinutes();
    chat.innerHTML += '<div class="chat_message">' + '<b>'+'Reminder</b>'+current+ '<div class="chat_message_white"> <b> Question:'  + assign.title + '</b><br>'+ 'Time UP!<br> The answers are graded'+ '</br>chat message will be clear in a second'+ ' </div>' +'<br>' +  '</div>';
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


function assign_question(assign){
    if (timer!==null){
        clearInterval(timer);
    }
    document.getElementById('left_retangle').classList.add('noClick');
    let time = document.getElementById('time_remaining');
    time.hidden = false;
    timer =setInterval (function (){
        let t =  showtime(assign.expire);

        if( t.reduce((a, b) => a + b, 0) >=  0) {
            time.innerHTML = t[0].toString() + ":" + t[1].toString()  + ":" + t[2].toString();


        }
        else {
            setTimeout(function (){
                clearInterval(timer);
                document.getElementById("course_name").onclick;
                addTimeUp(assign);
                document.getElementById('left_retangle').classList.remove('noClick');
            },5);

            setTimeout(function (){
                window.location.replace(document.location);
            },1000*10);

        }
    } ,1000);
}





class websocket extends Object {
    constructor(courseId) {
        super();
        this.socket = new WebSocket('ws://' + window.location.host + '/ws'+courseId);
        // Called whenever data is received from the server over the WebSocket connection

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

    sendStatus() {
        const id = document.getElementById("current_select_question_id").value;
        this.socket.send(JSON.stringify({'messageType':"status", "live" : "0", "question": id}));

    }

    sendAssign(){
        const timeBox = document.getElementById("timeBox");
        const comment = timeBox.value;
        timeBox.value = "";
        timeBox.focus();
        const id = document.getElementById('current_select_question_id').value;
        if (comment !== "") {
            this.socket.send(JSON.stringify({'messageType': "assign", 'question': id, 'min':comment}));
        }
        return null;
    }

    sendAnswer(email) {
        const timeBox = document.getElementById("answerBox");
        const comment = timeBox.value;
        timeBox.value = "";
        timeBox.focus();

        const id = document.getElementById("current_select_question_id").value;
        this.socket.send(JSON.stringify({'messageType':"answer", "email" : email, "question": id,"comment":comment}));
        return null;
    }

    sendTimeOut(id) {
        this.socket.send(JSON.stringify({'messageType':"timeOut", "question": id}));
    }



}
var join_code =document.getElementById("join_code").innerHTML.toString()
const ws = new websocket(join_code);
document.getElementById('chatSubmit_div').addEventListener("keypress", function (event) {
    if (event.code === "Enter") {
        var email = document.getElementById('current_user_email').innerHTML;
        ws.sendMessage(email);
    }
});



var assign_form = document.getElementById("assign_form");
function handleForm(event) { event.preventDefault();  ws.sendAssign();}

if (assign_form!=null){
    assign_form.addEventListener('submit', handleForm);
}

setInterval(function (){ws.sendStatus()}, 1000);




