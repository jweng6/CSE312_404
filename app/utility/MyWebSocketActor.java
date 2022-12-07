package utility;
import akka.actor.*;
import com.fasterxml.jackson.databind.JsonNode;
import domain.Question;
import domain.User;
import play.libs.Json;
import service.QuestionService;
import service.UserService;
import service.impl.QuestionImpl;
import service.impl.UserImpl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MyWebSocketActor extends AbstractActor {

    public static Props props(ActorRef out) {
        return Props.create(MyWebSocketActor.class, out);
    }
    private final ActorRef out;
    private UserService userService = new UserImpl();
    private QuestionService qService = new QuestionImpl();
    public MyWebSocketActor(ActorRef out) {
        this.out = out;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, message -> {
                    Constant.list.add(this.out);
                    String messageType = Json.stringify(message.findPath("messageType")).replace("\"","");
                    LocalDateTime dateTime = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    if ("chat".equals(messageType)){
                        String email = Json.stringify(message.findPath("email"));
                        User user = userService.nowChat(email.replace("\"",""));
                        String comment = Json.stringify(message.findPath("comment"));
                        String fullName = user.getFirstname() + " " + user.getLastname();
                        comment = comment.replace("\"","");
                        String test = "{\"messageType\":\""+messageType+"\",\"user\":\""+fullName+"\"" + "," +
                                "\"comment\":\""+comment+"\",\"current\":\""+dateTime.format(formatter)+"\"}";
                        for (int i = 0; i<Constant.list.size();i++){
                            Constant.list.get(i).tell(Json.parse(test), self());
                        }
                    }else if ("assign".equals(messageType)){
                        //socket.send(JSON.stringify({'messageType':"assign",'question': 1}));
                        String question  = Json.stringify(message.findPath("question")).replace("\"","");
                        int min = Integer.parseInt(Json.stringify(message.findPath("min")).replace("\"",""));
                        dateTime = dateTime.plusMinutes(min);
                        String test = "{\"messageType\":\""+messageType+"\",\"question\":\""+question+"\"" + "," +
                                "\"expire\":\""+dateTime.format(formatter)+"\"}";
                        out.tell(message,self());
                    }else if("answer".equals(messageType)){
                        //socket.send(JSON.stringify({'messageType':"answer", 'email': email ,question:1, 'comment': comment }));
                        String email = Json.stringify(message.findPath("email")).replace("\"","");
                        String comment = Json.stringify(message.findPath("comment")).replace("\"","");
                        int question  = Integer.parseInt(Json.stringify(message.findPath("question")).replace("\"",""));
                        qService.answerQuestion(question,email,comment);

                    }else if ("status".equals(messageType)){
                        //socket.send(JSON.stringify({'messageType':"status", "live" : "1/0" "question": "0" ));   //0 = open  1= close
                    }
                })
                .build();
    }

}