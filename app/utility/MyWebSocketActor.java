package utility;
import akka.actor.*;
import com.fasterxml.jackson.databind.JsonNode;
import domain.Question;
import domain.User;
import domain.socketActor;
import play.libs.Json;
import service.QuestionService;
import service.UserService;
import service.impl.QuestionImpl;
import service.impl.UserImpl;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MyWebSocketActor extends AbstractActor {

    public static Props props(ActorRef out) {
        return Props.create(MyWebSocketActor.class, out);
    }
    private final ActorRef out;
    private String code ;
    private List<ActorRef> clients;
    private UserService userService = new UserImpl();
    private QuestionService qService = new QuestionImpl();
    public MyWebSocketActor(ActorRef out) {
        this.out = out;
        this.code = Constant.currentServer;
        this.clients = helper();

    }
    private List<ActorRef> helper(){
        for (int i =0; i<Constant.ClientList.size(); i++){
            if (this.code.equals(Constant.ClientList.get(i).getCode())){
                return Constant.ClientList.get(i).getActorList();
            }
        }
        return new ArrayList<ActorRef>();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, message -> {
                    if (!clients.contains(this.out)){
                        clients.add(this.out);
                    }
                    String test = Json.stringify(message);
                    String messageType = Json.stringify(message.findPath("messageType")).replace("\"","");
                    LocalDateTime dateTime = LocalDateTime.now();
                    ZoneOffset zoneOffset = OffsetDateTime.now().getOffset();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    if ("chat".equals(messageType)){
                        String email = Json.stringify(message.findPath("email"));
                        User user = userService.nowChat(email.replace("\"",""));
                        String comment = Json.stringify(message.findPath("comment"));
                        String fullName = user.getFirstname() + " " + user.getLastname();
                        comment = comment.replace("\"","");
                        test = "{\"messageType\":\""+messageType+"\",\"user\":\""+fullName+"\"" + "," +
                                "\"email\":\""+email.replace("\"","")+"\",\"comment\":\""+comment+"\",\"current\":\""+dateTime.format(formatter)+"\"}";
                    }
                    if ("assign".equals(messageType)){
                        //socket.send(JSON.stringify({'messageType':"assign",'question': 1}));
                        String question  = Json.stringify(message.findPath("question")).replace("\"","");
                        int min = Integer.parseInt(Json.stringify(message.findPath("min")).replace("\"",""));
                        System.out.println(Integer.parseInt(question));
                        Question q = qService.getQuestion(Integer.parseInt(question));
                        dateTime = dateTime.plusMinutes(min);
                        qService.expires(Integer.parseInt(question),dateTime.toEpochSecond(zoneOffset));
                        test = "{\"messageType\":\""+messageType+"\",\"question\":\""+question+"\"" + "," +
                                "\"title\":\""+q.getHeader()+"\"," +
                                "\"details\":\""+q.getDetail()+"\"," +
                                "\"A\":\""+q.getAnswerA()+"\"," +
                                "\"B\":\""+q.getAnswerB()+"\"," +
                                "\"C\":\""+q.getAnswerC()+"\"," +
                                "\"D\":\""+q.getAnswerD()+"\"," +
                                "\"expire\":\""+dateTime.format(formatter)+"\"}";
                    }if("answer".equals(messageType)){
                        //socket.send(JSON.stringify({'messageType':"answer", 'email': email ,question:1, 'comment': comment}));
                        String email = Json.stringify(message.findPath("email")).replace("\"","");
                        String comment = Json.stringify(message.findPath("comment")).replace("\"","");
                        int question  = Integer.parseInt(Json.stringify(message.findPath("question")).replace("\"",""));
                        long expireTime = qService.getExpire(question);
                        long nowTime = dateTime.toEpochSecond(zoneOffset);
                        if (nowTime <= expireTime){
                            qService.answerQuestion(question,email,comment);
                        }
                        test = "{\"messageType\":\""+messageType+"\"}";
                    }if ("status".equals(messageType)){
                        //socket.send(JSON.stringify({'messageType':"status", "live" : "1/0" "question": "0" ));   //0 = open  1= close
//                        String live = Json.stringify(message.findPath("live")).replace("\"","");
                        test = "{\"messageType\":\""+messageType+"\"}";
                    }

                    for (int i = 0; i<clients.size();i++){
                        clients.get(i).tell(Json.parse(test), this.self());
                    }
                })
                .build();
    }

}