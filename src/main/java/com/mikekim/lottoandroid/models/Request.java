package com.mikekim.lottoandroid.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {
    String state;
    String url;
    Map<String, String> nameRegex;
    Map<String, Integer> nameGameSize;
    Map<String, Extra> nameExtra;
    Map<String, Integer> nameBonus;
    Map<String, String> jackpotPosition;
}
