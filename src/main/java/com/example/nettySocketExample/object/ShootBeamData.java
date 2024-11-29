package com.example.nettySocketExample.object;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ShootBeamData {
    private String playerId;
    private float x;
    private float y;
    private float targetX;
    private float targetY;

    public ShootBeamData(
            @JsonProperty("playerId") String playerId,
            @JsonProperty("x") float x,
            @JsonProperty("y") float y,
            @JsonProperty("targetX") float targetX,
            @JsonProperty("targetY") float targetY
    ) {
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.targetX = targetX;
        this.targetY = targetY;
    }
}
