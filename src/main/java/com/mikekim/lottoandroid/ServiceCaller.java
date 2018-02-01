package com.mikekim.lottoandroid;

import com.mikekim.lottoandroid.models.NyGames;
import com.mikekim.lottoandroid.repositories.ArLottoRepository;
import com.mikekim.lottoandroid.repositories.NyLottoRepository;
import com.mikekim.lottoandroid.repositories.OhLottoRepository;
import com.mikekim.lottoandroid.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;
import java.util.spi.CalendarDataProvider;

@Component
public class ServiceCaller {


    @Autowired
    AzLottoService azLottoService;

    @Autowired
    ArLottoService arLottoService;

    @Autowired
    CaLottoService caLottoService;

    @Autowired
    CoLottoService coLottoService;

    @Autowired
    CtLottoService ctLottoService;

    @Autowired
    DeLottoService deLottoService;

    @Autowired
    DcLottoService dcLottoService;

    @Autowired
    FlLottoService flLottoService;

    @Autowired
    GaLottoService gaLottoService;

    @Autowired
    IaLottoService iaLottoService;

    @Autowired
    IlLottoService ilLottoService;

    @Autowired
    IdLottoService idLottoService;

    @Autowired
    InLottoService inLottoService;

    @Autowired
    KsLottoService ksLottoService;

    @Autowired
    KyLottoService kyLottoService;

    @Autowired
    LaLottoService laLottoService;

    @Autowired
    MaLottoService maLottoService;

    @Autowired
    MiLottoService miLottoService;

    @Autowired
    MdLottoService mdLottoService;

    @Autowired
    MeLottoService meLottoService;

    @Autowired
    MnLottoService mnLottoService;

    @Autowired
    MsLottoService msLottoService;

    @Autowired
    MtLottoService mtLottoService;

    @Autowired
    MoLottoService moLottoService;

    @Autowired
    NcLottoService ncLottoService;

    @Autowired
    NdLottoService ndLottoService;

    @Autowired
    NeLottoService neLottoService;

    @Autowired
    NhLottoService nhLottoService;

    @Autowired
    NjLottoService njLottoService;

    @Autowired
    NmLottoService nmLottoService;

    @Autowired
    NyLottoService nyLottoService;

    @Autowired
    OhLottoService ohLottoService;

    @Autowired
    OkLottoService okLottoService;

    @Autowired
    OrLottoService orLottoService;

    @Autowired
    PaLottoService paLottoService;

    @Autowired
    RiLottoService riLottoService;

    @Autowired
    ScLottoService scLottoService;

    @Autowired
    SdLottoService sdLottoService;

    @Autowired
    TnLottoService tnLottoService;

    @Autowired
    TxLottoService txLottoService;

    @Autowired
    VaLottoService vaLottoService;

    @Autowired
    VtLottoService vtLottoService;

    @Autowired
    WaLottoService waLottoService;

    @Autowired
    WiLottoService wiLottoService;

    @Autowired
    WyLottoService wyLottoService;

    @Autowired
    WvLottoService wvLottoService;


 //   @Scheduled(fixedRate = 500000)
    public void callAll() {
        System.out.println("starting");
        azLottoService.getAll();    //over 1 minute    needs javascript
        arLottoService.getAll();    //5 secs

        caLottoService.getAll();
        coLottoService.getAll();    //15 secs
        ctLottoService.getAll();    //30ish seconds, alot of games
        deLottoService.getAll();       //15 secs
        dcLottoService.getAll();    //5 secs
        flLottoService.getAll();
        gaLottoService.getAll();    //over 1 min needs javascript
        iaLottoService.getAll();    //12 secs
        ilLottoService.getAll();    //15 sec
        idLottoService.getAll();    //5 secs
        inLottoService.getAll();    //over 1 min needs javascript
        ksLottoService.getAll();    //5 secs
        kyLottoService.getAll();    //15 secs
        laLottoService.getAll();    //5 secs
        maLottoService.getAll();    //15 secs
        miLottoService.getAll();    //30 secs??
        mdLottoService.getAll();    //10 secs
        meLottoService.getAll();    //15 secs
        mnLottoService.getAll();    //10 secs
        msLottoService.getAll();    //10 secs
        mtLottoService.getAll();    //todo 30 secs
        moLottoService.getAll();    //5 secs
        ncLottoService.getAll();    //5 secs
        ndLottoService.getAll();    //15 secs
        neLottoService.getAll();    //10 secs
        nhLottoService.getAll();    //15 secs
        njLottoService.getAll();    //todo 30 secs
        nmLottoService.getAll();    //todo fix pick 3
        nyLottoService.getAll();    //todo getting old numbers
        ohLottoService.getAll();    //8 secs //todo need to run two times day
        okLottoService.getAll();    //around 15 secs?
        orLottoService.getAll();    //10 secs
        paLottoService.getAll();    //10 secs
        riLottoService.getAll();    //15 secs
        scLottoService.getAll();    //5 secs
        sdLottoService.getAll();    //5 secs
        tnLottoService.getAll();    //5 secs
        txLottoService.getAll();
        vaLottoService.getAll();    //5 secs
        vtLottoService.getAll();    //10 secs
        waLottoService.getAll();    //5 secs
        wiLottoService.getAll();    //5 secs
        wyLottoService.getAll();    //5 secs
        wvLottoService.getAll();
    }
}


