package com.mikekim.lottoandroid.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GamePrimaryKey implements Serializable {
    private String name;
    private String date;
    private String state;
}
