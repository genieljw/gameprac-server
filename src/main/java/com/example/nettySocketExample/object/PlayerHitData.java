package com.example.nettySocketExample.object;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PlayerHitData {
  private String playerId; // 맞은 유저의 ID
    private int damage;      // 총알의 대미지

    public PlayerHitData(
      @JsonProperty("playerId") String playerId,
      @JsonProperty("damage") int damage
    )
    {
      this.playerId = playerId;
      this.damage = damage;
    }
}
