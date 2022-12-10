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




// Establish a WebSocket connection with the server
// Allow users to send messages by pressing enter instead of clicking the Send button

function addjoined(user) {
    const chat = document.getElementById('chat_all_message');
    let name = user.name.toLowerCase().split(" ");
    for (let i = 0; i < name.length; i++) {
        name[i] = name[i][0].toUpperCase() + name[i].substr(1);
    }
    let url = 'window.open("/profile/' +ws.email.toString()+'")';
    name = name.join(" ");

    chat.innerHTML += '<div class="chat_message" style="width:100%;text-align:center;display: inline-block;text-overflow: ellipsis;overflow: hidden;white-space: nowrap;" ><b class="pointer" style="color: #4A4A4A">' +'<a onclick=' + url + '>'+ name +'</a></b> Joined the chat' +'<br>' +  '</div>';
    chat.scrollTop = chat.scrollHeight;
}
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
    chat.innerHTML += '<div class="chat_message">' + '<b>'+'Reminder </b>'+current+ '<div class="chat_message_white"> <b> Question:'  + assign.title + '</b><br>'+ 'Time UP!<br> The answers are graded'+ ' </div>' +'<br>' +  '</div>';
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
    document.getElementById('gradebook_button').classList.add('noClick');
    document.getElementById('roster_button').classList.add('noClick');
    document.getElementById('add_question_button').classList.add('noClick');
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
                document.getElementById('gradebook_button').classList.remove('noClick');
                document.getElementById('roster_button').classList.remove('noClick');
                document.getElementById('add_question_button').classList.remove('noClick');
            },5);


            // setTimeout(function (){
            //     window.location.replace(document.location);
            // },1000*10);

        }
    } ,1000);
}



class websocket extends Object {
    constructor(courseId) {
        super();
        this.socket = new WebSocket('wss://' + window.location.host + '/wss'+courseId);
        this.email = document.getElementById('current_user_email').innerHTML;
        this.course = document.location.pathname.split('course/')[1];
        // Called whenever data is received from the server over the WebSocket connection
        console.log(this.email);
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
                case 'add_question':
                    addQuestion(message);
                    break;
                case 'show_this_question':
                    console.log(this.email);
                    console.log(message.email);
                    if (message.email ===ws.email){
                        showQuestion(message);
                        break;
                    }
                    break;
                case 'show_gradebook':
                    show_GradeBook(message);
                    break;
                case 'show_roster':
                    show_roster(message)
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

    sendAnswer() {
        const timeBox = document.getElementById("answerBox");
        const comment = timeBox.value;
        timeBox.value = "";
        timeBox.focus();

        const id = document.getElementById("current_select_question_id").value;
        this.socket.send(JSON.stringify({'messageType':"answer", "email" : this.email, "question": id,"comment":comment}));
        return null;
    }

    sendTimeOut(id) {

        this.socket.send(JSON.stringify({'messageType':"timeOut", "question": id.toString(),"email":this.email}));
    }

    sendAddQuestion() {
        const header = clearInput('header');
        const details = clearInput('detail');
        const answer = clearInput('answer');
        const grade = clearInput('grade');
        const a = clearInput('answerA');
        const b = clearInput('answerB');
        const c = clearInput('answerC');
        const d = clearInput('answerD');
        console.log("finishINPUT")
        if (header !== "" && details!== "" && answer!== "" && grade!== "" ) {
            this.socket.send(JSON.stringify({'messageType':"add_question", 'header': header,'details':details,'answer':answer,'cid':this.course,'grade':grade,'A':a,'B':b,'C':c,'D':d}));
        }
    }

    sendShowQuestion(id) {
        this.socket.send(JSON.stringify({'messageType':"show_this_question", 'email':this.email,'question': id}));
    }

    sendGradeBook() {
        this.socket.send(JSON.stringify({'messageType':"show_gradebook", 'cid':this.course}));
    }
    sendRoster() {
        this.socket.send(JSON.stringify({'messageType':"show_roster", 'cid':this.course}));
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
var add_question_form = document.getElementById("add_question_form");

function handleForm(event) { event.preventDefault();  ws.sendAssign();}
function handleAnsForm(event) {
    event.preventDefault();

    ws.sendAddQuestion();

}

if (assign_form!=null){
    assign_form.addEventListener('submit', handleForm);
}

if (add_question_form!=null){
    add_question_form.addEventListener('submit', handleAnsForm);
}


setInterval(function (){ws.sendStatus()}, 1000);

function showHide(id){
    const div= ['add_question_div','show_question_div','roster_div','gradebook_div'];
    for (let i = 0; i < div.length; i++) {
        if(id === div[i]){ //hide all others
            console.log(id)
            document.getElementById(div[i]).hidden =false;
        }
        else {
            document.getElementById(div[i]).hidden = true;
        }
    }
}
function showQuestion(message){
    showHide('show_question_div');
    document.getElementById("current_select_question_id").value = message.qid;

    document.getElementById('show_q_header').innerHTML = message.header;
    document.getElementById('show_q_details').value = message.details;
    document.getElementById('show_q_a').innerHTML = message.a;
    document.getElementById('show_q_b').innerHTML = message.b;
    document.getElementById('show_q_c').innerHTML = message.c;
    document.getElementById('show_q_d').innerHTML = message.d;
    document.getElementById('show_answer').innerHTML = "Correct Answer: "+ message.answer;
    //document.getElementById('show_grade').innerHTML = "Your grade: " + message.grade;
}

function clearInput(id){
    const e = document.getElementById(id);
    const comment = e.value;
    e.value="";
    e.focus();
    return comment;
}


function addQuestion(message){
    var table = document.getElementById("show_question_table");
    var row = table.insertRow(-1);
    var cell1 = row.insertCell(0);

    cell1.innerHTML = message.header;
    cell1.setAttribute("onclick","ws.sendShowQuestion("+message.qid+")")
    cell1.setAttribute("id","question_"+message.qid);
}

function clearChatMessage(){
    const chat = document.getElementById('chat_all_message').innerHTML = "";
}

function show_GradeBook(message){

    console.log(message);
    showHide('gradebook_div');
    var table = document.getElementById("gradebook_table");
    table.innerHTML="<tr><th>First Name</th><th>Last Name</th><th>Email</th><th>Grade</th></tr>";


    var listf = message.first;
    var listl = message.last;
    var liste = message.email;
    var listg = message.grade;

    for (let i = 0; i < message.first.length; i++) {
        var row = table.insertRow(-1);
        var first = row.insertCell(0);
        var last = row.insertCell(1);
        var email = row.insertCell(2);
        var grade = row.insertCell(3);
        first.innerHTML = listf[i];
        last.innerHTML = listl[i];
        email.innerHTML = liste[i];
        grade.innerHTML = listg[i];

    }
}


function show_roster(message) {
    showHide('roster_div');
    var table = document.getElementById("roster_table");
    table.innerHTML = "<tr><th>First Name</th><th>Last Name</th><th>Email</th></tr>";


    var listf = message.first;
    var listl = message.last;
    var liste = message.email;

    for (let i = 0; i < message.first.length; i++) {
        var row = table.insertRow(-1);
        var first = row.insertCell(0);
        var last = row.insertCell(1);
        var email = row.insertCell(2);
        first.innerHTML = listf[i];
        last.innerHTML = listl[i];
        email.innerHTML = liste[i];

    }
}