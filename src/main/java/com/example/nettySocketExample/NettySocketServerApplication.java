package com.example.nettySocketExample;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import com.example.nettySocketExample.object.Player;
import com.example.nettySocketExample.object.PlayerHitData;
import com.example.nettySocketExample.object.PlayerMovementData;
import com.example.nettySocketExample.object.ShootBeamData;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;



@Component
public class NettySocketServerApplication implements CommandLineRunner {

    public final SocketIOServer socketIOServer;
    Map<String, Player> playerMap = new HashMap<>();
    //Map<String, Player> playerMap = new ConcurrentHashMap();  //for 동기화


    public NettySocketServerApplication(SocketIOServer socketIOServer) {
        this.socketIOServer = socketIOServer;
    }

    @Override
    public void run(String... args) {

        socketIOServer.start();

        // broadcast each 50ms
        new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(50);

                    //+time sync+
                    long severTime = System.currentTimeMillis();    //현재 서버 시간(ms)
                    Map<String, Object> stateData = new HashMap<>();
                    stateData.put("players", playerMap);    //플레이어 상태
                    stateData.put("serverTime", severTime);    //서버 시간 추가


                    socketIOServer.getBroadcastOperations().sendEvent("state", playerMap);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();

        socketIOServer.addEventListener("shootBeam", ShootBeamData.class, new DataListener<ShootBeamData>() {
            @Override
            public void onData(SocketIOClient client, ShootBeamData data, AckRequest ackRequest){
                String playerId = client.getSessionId().toString();
                System.out.println("playerId = " + playerId);

                ShootBeamData beamData = new ShootBeamData(playerId, data.getX(), data.getY(), data.getTargetX(), data.getTargetY() );

                client.getNamespace().getBroadcastOperations().sendEvent("shootBeam", beamData);

            }
        });

        //+time sync+ client가 server time 요청하는 event listener
        socketIOServer.addEventListener("requestServerTime", Long.class, new DataListener<Long>() {
            @Override
            public void onData(SocketIOClient client, Long clientTime, AckRequest ackRequest){               
                long serverTime = System.currentTimeMillis();
                if(ackRequest.isAckRequested()){
                    ackRequest.sendAckData(serverTime);
                }
            }
        });

        // first connection with client
        socketIOServer.addConnectListener(client -> {
            System.out.println("Client connected: " + client.getSessionId());

            Player player = Player.builder()
                    .playerId(client.getSessionId().toString())
                    .x(600)
                    .y(600)
                    .hp(100)
                    .build();

            playerMap.put(client.getSessionId().toString(), player);
            client.sendEvent("currentPlayer", player);
        });

        socketIOServer.addEventListener("playerHit", PlayerHitData.class, new DataListener<PlayerHitData>() {
            @Override
            public void onData(SocketIOClient client, PlayerHitData data, AckRequest ackRequest) {
                System.out.println("Player hit: " + data.getPlayerId() + " Damage: " + data.getDamage());

                //+hp+ 피격받은 player찾기
                Player hitPlayer = playerMap.get(data.getPlayerId());
                if(hitPlayer != null){
                    int damage = Math.max(data.getDamage(), 0); //음수 데미지 방지
                    hitPlayer.setHp(Math.max(hitPlayer.getHp() - damage, 0));  // hp감소(최소값 보장)
                    System.out.println("HitPlayer SetUp:" + hitPlayer.getHp());

                    //hp가 0이면 제거
                    if(hitPlayer.getHp() == 0){
                        System.out.println("Player" + data.getPlayerId() + "eliminated!!");
                        playerMap.remove(data.getPlayerId());
                        client.getNamespace().getBroadcastOperations().sendEvent("playerEliminated", data.getPlayerId());
                    }
                    else{
                        client.getNamespace().getBroadcastOperations().sendEvent("playerHit", hitPlayer);
                    }
                }

                // // 피격 정보를 모든 클라이언트에 브로드캐스트
//                 client.getNamespace().getBroadcastOperations().sendEvent("playerHit", data);
            }
        });

        socketIOServer.addEventListener("playerMove", PlayerMovementData.class,
                (client, data, ackSender) -> {
            String playerId = client.getSessionId().toString();
            Player player = playerMap.get(playerId);

            if(player != null) {
                player.setX(data.getX());
                player.setY(data.getY());
                playerMap.put(playerId, player);
            }
        });

        socketIOServer.addDisconnectListener(client -> {
            playerMap.remove(client.getSessionId().toString());
            System.out.println("Client.disconnected: " + client.getSessionId());
        });

        
    }

    @PreDestroy
    public void stop() {
        System.out.println("System stop");
        socketIOServer.stop();
    }
}