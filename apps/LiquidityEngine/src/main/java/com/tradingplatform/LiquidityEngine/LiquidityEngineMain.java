package com.tradingplatform.LiquidityEngine;

import java.util.concurrent.atomic.AtomicBoolean;
import io.vertx.core.Vertx;

public class LiquidityEngineMain {
    private static AtomicBoolean running = new AtomicBoolean(false);
    private static Thread workerThread;

    public static void startServer(){
        Vertx vertx = Vertx.vertx();

        workerThread = new Thread(()->{
            OrderPoster poster = new OrderPoster(60);
            while(true){
                if (running.get()){
                        try {
                            poster.postOrder();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                } else {
                        System.out.println("Liquidity Engine thread is toggled to off");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        workerThread.setDaemon(true);
        workerThread.start();

        vertx.createHttpServer()
            .requestHandler(request -> {
                if("/LiquidityEngine/toggle".equals(request.path())){
                    boolean newState = !running.get();
                    running.set(newState);
                    request.response()
                        .putHeader("content-type","text/plain")
                        .end("Liquidity Engine is now " + (newState ? "running" : "stopped"));

                } else {
                    request.response().setStatusCode(404).end("Invlaid adress homie");
                }
            })
            .listen(8888, http -> {
                if(http.succeeded()){
                    System.out.println("Http server started on port 8888");
                } else {
                    System.out.println("Https server failed to start - Error : " + http.cause());
                }
            });
    }

    public static void main(String[] args){
        System.out.println("Working directory: " + System.getProperty("user.dir"));
        startServer();          
    }

}
