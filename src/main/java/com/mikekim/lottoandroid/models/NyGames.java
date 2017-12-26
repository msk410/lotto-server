package com.mikekim.lottoandroid.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@IdClass(GamePrimaryKey.class)
public class NyGames {
    @Id
    private String name;
    @Id
    private String date;
    private String[] winningNumbers;
    private String bonus;
    private String extra;
    private String extraText;

    public NyGames(String name, String date, String[] winningNumbers, String bonus, String extra, String extraText) {
        this.name = name;
        this.date = date;
        this.winningNumbers = winningNumbers;
        this.bonus = bonus;
        this.extra = extra;
        this.extraText = extraText;
    }

    public NyGames() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String[] getWinningNumbers() {
        return winningNumbers;
    }

    public void setWinningNumbers(String[] winningNumbers) {
        this.winningNumbers = winningNumbers;
    }

    public String getBonus() {
        return bonus;
    }

    public void setBonus(String bonus) {
        this.bonus = bonus;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getExtraText() {
        return extraText;
    }

    public void setExtraText(String extraText) {
        this.extraText = extraText;
    }

    @Override
    public String toString() {
        String winningNumbers = "";
        for (String s : this.getWinningNumbers()) {
            winningNumbers += s + " ";
        }
        return this.getName() + " " + this.getDate() + " " + winningNumbers + " " + this.getBonus() + " " + this.getExtraText() + " " + this.getExtra();
    }
}
