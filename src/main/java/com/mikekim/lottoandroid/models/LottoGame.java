package com.mikekim.lottoandroid.models;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.util.Objects;

@Entity
@Data
@AllArgsConstructor
@Builder
@ToString
@IdClass(GamePrimaryKey.class)
public class LottoGame {
    @Id
    private String name;
    @Id
    private String date;
    @Id
    private String state;

    String[] winningNumbers;
    String bonus;
    String extra;
    String extraText;
    boolean showGame;

    public LottoGame() {
        this.name = "";
        this.date = "";
        this.state =  "";
        this.bonus = "";
        this.extra = "";
        this.extraText = "";
        this.showGame = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LottoGame lottoGame = (LottoGame) o;
        return Objects.equals(name, lottoGame.name) &&
                Objects.equals(date, lottoGame.date) &&
                Objects.equals(state, lottoGame.state);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), name, date, state);
    }
}
