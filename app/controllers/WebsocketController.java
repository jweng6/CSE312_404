package controllers;

import akka.actor.ActorSystem;
import akka.stream.Materializer;
import play.libs.F;
import play.libs.streams.ActorFlow;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.WebSocket;
import utility.MyWebSocketActor;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

public class WebsocketController extends Controller {
    private final ActorSystem actorSystem;
    private final Materializer materializer;

    @Inject
    public WebsocketController(ActorSystem actorSystem, Materializer materializer) {
        this.actorSystem = actorSystem;
        this.materializer = materializer;
    }
    public WebSocket socket(String code) {
        System.out.println(code);
        return WebSocket.Json.accept(
                request -> ActorFlow.actorRef(MyWebSocketActor::props, actorSystem, materializer));
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
