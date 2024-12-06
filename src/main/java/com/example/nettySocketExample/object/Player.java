package com.example.nettySocketExample.object;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class Player {
//    private float rotation;
    private String playerId;
    private float x, y;
    private int hp;
//    private String team;


    public Player(String playerId, float x, float y, int hp) {
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.hp = hp;
    }
}
