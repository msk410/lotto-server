package com.mikekim.lottoandroid.services;


import com.mikekim.lottoandroid.models.NyGames;
import com.mikekim.lottoandroid.repositories.NyLottoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class NyLottoServiceTest {
    @Mock
    NyLottoRepository nyLottoRepository;

    @InjectMocks
    NyLottoService nyLottoService;

    @Test
    public void test() {
        when(nyLottoRepository.findByNameAndDate(anyString(), anyString())).thenReturn(null);
        nyLottoService.getPowerball();
        NyGames nyGames = new NyGames();
        nyGames.setName("Powerball");
        nyGames.setWinningNumbers(new String[]{"32", "30", "36", "28", "58"});
        nyGames.setBonus("06");
        nyGames.setExtra("3");
        nyGames.setExtraText(" x ");
        List<NyGames> nyGamesList = new ArrayList<>();
        nyGamesList.add(nyGames);
        verify(nyLottoRepository, times(30)).findByNameAndDate(anyString(), anyString());
    }
}