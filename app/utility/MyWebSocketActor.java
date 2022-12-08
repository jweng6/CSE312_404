package utility;
import akka.actor.*;
import com.fasterxml.jackson.databind.JsonNode;
import domain.Question;
import domain.User;
import domain.socketActor;
import org.apache.commons.lang3.StringUtils;
import play.libs.Json;
import service.QuestionService;
import service.UserService;
import service.impl.QuestionImpl;
import service.impl.UserImpl;

import javax.sound.midi.Soundbank;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        List<ActorRef> res = new ArrayList<>();
        for (int i =0; i<Constant.ClientList.size(); i++){
            if (this.code.equals(Constant.ClientList.get(i).getCode())){
                res = Constant.ClientList.get(i).getActorList();
            }
        }
        return res;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, message -> {
                    if (!clients.contains(this.out)){
                        clients.add(this.out);
                        for (int i = 0; i < Constant.ClientList.size(); i++){

                        }
                    }
//                    for (int i = 0; i < Constant.ClientList.size(); i++){
//                        socketActor temp = Constant.ClientList.get(i);
//                        if (this.code.equals(temp.getCode())){
//                            if (!temp.getActorList().contains(this.out)){
//                                Constant.ClientList.get(i).getActorList().add(this.out);
//                            }
//                            this.clients = Constant.ClientList.get(i).getActorList();
//                            break;
//                        }
//                    }
                    String test = Json.stringify(message);
                    String messageType = Json.stringify(message.findPath("messageType")).replace("\"","");
                    LocalDateTime dateTime = LocalDateTime.now();
                    ZoneOffset zoneOffset = OffsetDateTime.now().getOffset();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    if ("chat".equals(messageType)){
                        String email = Json.stringify(message.findPath("email")).replace("\"","");
                        User user = userService.nowChat(email);
                        String comment = Json.stringify(message.findPath("comment"));
                        String fullName = user.getFirstname() + " " + user.getLastname();
                        comment = comment.replace("\"","");
                        comment = Constant.injection(comment);
                        test = "{\"messageType\":\""+messageType+"\",\"user\":\""+fullName+"\"" + "," +
                                "\"email\":\""+email+"\",\"comment\":\""+comment+"\",\"current\":\""+dateTime.format(formatter)+"\"}";
                    }else if ("assign".equals(messageType)){
                        //socket.send(JSON.stringify({'messageType':"assign",'question': 1}));
                        String question  = Json.stringify(message.findPath("question")).replace("\"","");
                        String  min = Constant.injection(Json.stringify(message.findPath("min")).replace("\"",""));
                        if (StringUtils.isNumeric(min)){
                            Question q = qService.getQuestion(Integer.parseInt(question));
                            dateTime = dateTime.plusMinutes(Integer.parseInt(min));
                            qService.expires(Integer.parseInt(question),dateTime.toEpochSecond(zoneOffset));
                            qService.assignQuestion(q);
                            test = "{\"messageType\":\""+messageType+"\",\"question\":\""+question+"\"" + "," +
                                    "\"title\":\""+q.getHeader()+"\"," +
                                    "\"details\":\""+q.getDetail()+"\"," +
                                    "\"A\":\""+q.getAnswerA()+"\"," +
                                    "\"B\":\""+q.getAnswerB()+"\"," +
                                    "\"C\":\""+q.getAnswerC()+"\"," +
                                    "\"D\":\""+q.getAnswerD()+"\"," +
                                    "\"expire\":\""+dateTime.format(formatter)+"\"}";
                        }
                    }else if("answer".equals(messageType)){
                        //socket.send(JSON.stringify({'messageType':"answer", 'email': email ,question:1, 'comment': comment}));
                        String email = Json.stringify(message.findPath("email")).replace("\"","");
                        String comment = Json.stringify(message.findPath("comment")).replace("\"","");
                        if (comment.equalsIgnoreCase("A")
                                ||comment.equalsIgnoreCase("B")
                                || comment.equalsIgnoreCase("C")
                                || comment.equalsIgnoreCase("D")) {
                            int question  = Integer.parseInt(Json.stringify(message.findPath("question")).replace("\"",""));
                            long expireTime = qService.getExpire(question);
                            Question q = qService.getQuestion(question);
                            long nowTime = dateTime.toEpochSecond(zoneOffset);
                            if (nowTime <= expireTime){
                                qService.answerQuestion(q.getFrom(),email,comment);
                            }
                            test = "{\"messageType\":\""+messageType+"\"}";
                        }

                    }else if ("status".equals(messageType)){
                        //socket.send(JSON.stringify({'messageType':"status", "live" : "1/0" "question": "0" ));   //0 = open  1= close
//                        String live = Json.stringify(message.findPath("live")).replace("\"","");
                        test = "{\"messageType\":\""+messageType+"\"}";
                    }else if("timeOut".equals(messageType)){
                        //messageType|question id|
                        String qid = Json.stringify(message.findPath("question")).replace("\"","");
                        qService.grading(Integer.parseInt(qid));
                        test = "{\"messageType\":\""+messageType+"\",\"question\":\""+qid+"\"}";
                    }

                    for (int i = 0; i<clients.size();i++){
                        clients.get(i).tell(Json.parse(test), this.self());
                    }
                })
                .build();
    }

}