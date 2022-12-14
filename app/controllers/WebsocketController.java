package controllers;

import akka.actor.ActorSystem;
import akka.stream.Materializer;

import domain.socketActor;
import play.libs.streams.ActorFlow;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.WebSocket;
import utility.Constant;
import utility.MyWebSocketActor;
import javax.inject.Inject;
import java.util.ArrayList;



public class WebsocketController extends Controller {
    private final ActorSystem actorSystem;
    private final Materializer materializer;

    @Inject
    public WebsocketController(ActorSystem actorSystem, Materializer materializer) {
        this.actorSystem = actorSystem;
        this.materializer = materializer;
    }
    public WebSocket socket(String code) {
        Constant.currentServer = code;
        if (Constant.ClientList.isEmpty()){
            Constant.ClientList.add(new socketActor(code,new ArrayList<>()));
        }else {
            boolean flag = true;
            for (int i = 0; i<Constant.ClientList.size(); i++){
                if (code.equals(Constant.ClientList.get(i).getCode())){
                    flag = false;
                    break;
                }
            }
            if (flag){
                socketActor temp = new socketActor(code,new ArrayList<>());
                Constant.ClientList.add(temp);
            }
        }
        System.out.println(code);
        System.out.println(Constant.currentServer);

        return WebSocket.Json.accept(
                request -> ActorFlow.actorRef(MyWebSocketActor::props, actorSystem, materializer));

//        return WebSocket.Json.acceptOrResult(
//                request ->
//                        CompletableFuture.completedFuture(
//                                request.session()
//                                        .get("connecting")
//                                        .map(
//                                                user ->
//                                                        F.Either.<Result, Flow<JsonNode, JsonNode, ?>>Right(
//                                                                ActorFlow.actorRef(
//                                                                        MyWebSocketActor::props, actorSystem, materializer)))
//                                        .orElseGet(() -> F.Either.Left(forbidden()))));


    }
//    public WebSocket socket(String code) {
//        System.out.println(code);
//        return WebSocket.Json.accept(
//                request -> ActorFlow.actorRef(MyWebSocketActor::props, actorSystem, materializer));
//    }
//    public WebSocket socket() {
//        return WebSocket.Json.accept(
//                request -> CompletableFuture.completedFuture(
//
//                        F.Either.Right(f.apply(request)))
//        );
////                request -> ActorFlow.actorRef(MyWebSocketActor::props, actorSystem, materializer));
//    }




}
