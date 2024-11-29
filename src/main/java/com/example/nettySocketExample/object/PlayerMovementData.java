package com.example.nettySocketExample.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class PlayerMovementData {
//    private String playerId;
    private float x;
    private float y;
//    private float rotation;

    public PlayerMovementData(
//            @JsonProperty("playerId") String playerId,
            @JsonProperty("x") float x,
            @JsonProperty("y") float y
//            @JsonProperty("rotation") float rotation
    ) {
//        this.playerId = playerId;
        this.x = x;
        this.y = y;
//        this.rotation = rotation;
    }
}
