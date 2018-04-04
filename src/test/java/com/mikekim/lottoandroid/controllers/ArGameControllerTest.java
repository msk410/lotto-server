//package com.mikekim.lottoandroid.controllers;
//
//import com.mikekim.lottoandroid.LottoAndroidApplication;
//import com.mikekim.lottoandroid.models.*;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = LottoAndroidApplication.class)
//@Transactional
//public class ArGameControllerTest {
//
//
//    @Autowired
//    AzGameController azGameController;
//
//    @Autowired
//    ArGameController arGameController;
//
//    @Autowired
//    CaGameController caGameController;
//
//    @Autowired
//    CoGameController coGameController;
//
//    @Autowired
//    CtGameController ctGameController;
//
//    @Autowired
//    DeGameController deGameController;
//
//    @Autowired
//    DcGameController dcGameController;
//
//    @Autowired
//    FlGameController flGameController;
//
//    @Autowired
//    GaGameController gaGameController;
//
//    @Autowired
//    IaGameController iaGameController;
//
//    @Autowired
//    IlGameController ilGameController;
//
//    @Autowired
//    IdGameController idGameController;
//
//    @Autowired
//    InGameController inGameController;
//
//    @Autowired
//    KsGameController ksGameController;
//
//    @Autowired
//    KyGameController kyGameController;
//
//    @Autowired
//    LaGameController laGameController;
//
//    @Autowired
//    MaGameController maGameController;
//
//    @Autowired
//    MiGameController miGameController;
//
//    @Autowired
//    MdGameController mdGameController;
//
//    @Autowired
//    MeGameController meGameController;
//
//    @Autowired
//    MnGameController mnGameController;
//
//    @Autowired
//    MtGameController mtGameController;
//
//    @Autowired
//    MoGameController moGameController;
//
//    @Autowired
//    NcGameController ncGameController;
//
//    @Autowired
//    NdGameController ndGameController;
//
//    @Autowired
//    NeGameController neGameController;
//
//    @Autowired
//    NhGameController nhGameController;
//
//    @Autowired
//    NjGameController njGameController;
//
//    @Autowired
//    NmGameController nmGameController;
//
//    @Autowired
//    NyGameController nyGameController;
//
//    @Autowired
//    OhGameController ohGameController;
//
//    @Autowired
//    OkGameController okGameController;
//
//    @Autowired
//    OrGameController orGameController;
//
//    @Autowired
//    PaGameController paGameController;
//
//    @Autowired
//    RiGameController riGameController;
//
//    @Autowired
//    ScGameController scGameController;
//
//    @Autowired
//    SdGameController sdGameController;
//
//    @Autowired
//    TnGameController tnGameController;
//
//    @Autowired
//    TxGameController txGameController;
//
//    @Autowired
//    VaGameController vaGameController;
//
//    @Autowired
//    VtGameController vtGameController;
//
//    @Autowired
//    WaGameController waGameController;
//
//    @Autowired
//    WiGameController wiGameController;
//
//    @Autowired
//    WyGameController wyGameController;
//
//    @Autowired
//    WvGameController wvGameController;
//
//    @Test
//    public void ar() {
//        arGameController.saveGames();
//        Iterable<ArGames> iterable = arGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(8);
//    }
//
//    @Test
//    public void az() {
//        azGameController.saveGames();
//        Iterable<AzGames> iterable = azGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(8);
//    }
//
//    @Test
//    public void ca() {
//        caGameController.saveGames();
//        Iterable<CaGames> iterable = caGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(7);
//    }
//
//    @Test
//    public void ct() {
//        ctGameController.saveGames();
//        Iterable<CtGames> iterable = ctGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(11);
//    }
//
//    @Test
//    public void de() {
//        deGameController.saveGames();
//        Iterable<DeGames> iterable = deGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(9);
//    }
//
//    @Test
//    public void fl() {
//        flGameController.saveGames();
//        Iterable<FlGames> iterable = flGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(14);
//    }
//
//    @Test
//    public void ga() {
//        gaGameController.saveGames();
//        Iterable<GaGames> iterable = gaGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(18);
//    }
//
//    @Test
//    public void id() {
//        idGameController.saveGames();
//        Iterable<IdGames> iterable = idGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(8);
//    }
//
//    @Test
//    public void il() {
//        ilGameController.saveGames();
//        Iterable<IlGames> iterable = ilGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(9);
//    }
//
//    @Test
//    public void in() {
//        inGameController.saveGames();
//        Iterable<InGames> iterable = inGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isGreaterThanOrEqualTo(3);
//    }
//
//    @Test
//    public void ks() {
//        ksGameController.saveGames();
//        Iterable<KsGames> iterable = ksGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isGreaterThanOrEqualTo(8);
//    }
//
//    @Test
//    public void ky() {
//        kyGameController.saveGames();
//        Iterable<KyGames> iterable = kyGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isGreaterThanOrEqualTo(9);
//    }
//
//    @Test
//    public void la() {
//        laGameController.saveGames();
//        Iterable<LaGames> iterable = laGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(6);
//    }
//
//    @Test
//    public void ma() {
//        maGameController.saveGames();
//        Iterable<MaGames> iterable = maGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isGreaterThanOrEqualTo(4);
//    }
//
//    @Test
//    public void md() {
//        mdGameController.saveGames();
//        Iterable<MdGames> iterable = mdGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isGreaterThanOrEqualTo(4);
//    }
//
//    @Test
//    public void me() {
//        meGameController.saveGames();
//        Iterable<MeGames> iterable = meGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isGreaterThanOrEqualTo(4);
//    }
//
//    @Test
//    public void mi() {
//        miGameController.saveGames();
//        Iterable<MiGames> iterable = miGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(11);
//    }
//
//    @Test
//    public void mn() {
//        mnGameController.saveGames();
//        Iterable<MnGames> iterable = mnGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isGreaterThanOrEqualTo(7);
//    }
//
//    @Test
//    public void mo() {
//        moGameController.saveGames();
//        Iterable<MoGames> iterable = moGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isGreaterThanOrEqualTo(9);
//    }
//
//    @Test
//    public void mt() {
//        mtGameController.saveGames();
//        Iterable<MtGames> iterable = mtGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(6);
//    }
//
//    @Test
//    public void nc() {
//        ncGameController.saveGames();
//        Iterable<NcGames> iterable = ncGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isGreaterThanOrEqualTo(6);
//    }
//
//    @Test
//    public void nd() {
//        ndGameController.saveGames();
//        Iterable<NdGames> iterable = ndGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(5);
//    }
//
//    @Test
//    public void ne() {
//        neGameController.saveGames();
//        Iterable<NeGames> iterable = neGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(7);
//    }
//
//    @Test
//    public void nh() {
//        nhGameController.saveGames();
//        Iterable<NhGames> iterable = nhGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isGreaterThanOrEqualTo(8);
//    }
//
//    @Test
//    public void nj() {
//        njGameController.saveGames();
//        Iterable<NjGames> iterable = njGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(10);
//    }
//
//    @Test
//    public void nm() {
//        nmGameController.saveGames();
//        Iterable<NmGames> iterable = nmGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isGreaterThanOrEqualTo(6);
//    }
//
//    @Test
//    public void ny() {
//        nyGameController.saveGames();
//        Iterable<NyGames> iterable = nyGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(10);
//    }
//
//    @Test
//    public void oh() {
//        ohGameController.saveGames();
//        Iterable<OhGames> iterable = ohGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(11);
//    }
//
//    @Test
//    public void ok() {
//        okGameController.saveGames();
//        Iterable<OkGames> iterable = okGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(6);
//    }
//
//    @Test
//    public void or() {
//        orGameController.saveGames();
//        Iterable<OrGames> iterable = orGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isGreaterThanOrEqualTo(6);
//    }
//
//    @Test
//    public void pa() {
//        paGameController.saveGames();
//        Iterable<PaGames> iterable = paGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(14);
//    }
//
//    @Test
//    public void ri() {
//        riGameController.saveGames();
//        Iterable<RiGames> iterable = riGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(6);
//    }
//
//    @Test
//    public void sc() {
//        scGameController.saveGames();
//        Iterable<ScGames> iterable = scGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isGreaterThanOrEqualTo(6);
//    }
//
//    @Test
//    public void sd() {
//        sdGameController.saveGames();
//        Iterable<SdGames> iterable = sdGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(5);
//    }
//
//    @Test
//    public void tn() {
//        tnGameController.saveGames();
//        Iterable<TnGames> iterable = tnGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isGreaterThanOrEqualTo(3);
//    }
//
//    @Test
//    public void tx() {
//        txGameController.saveGames();
//        Iterable<TxGames> iterable = txGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(18);
//    }
//
//    @Test
//    public void va() {
//        vaGameController.saveGames();
//        Iterable<VaGames> iterable = vaGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isGreaterThanOrEqualTo(8);
//    }
//    @Test
//    public void vt() {
//        vtGameController.saveGames();
//        Iterable<VtGames> iterable = vtGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isGreaterThanOrEqualTo(7);
//    }
//    @Test
//    public void wa() {
//        waGameController.saveGames();
//        Iterable<WaGames> iterable = waGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(7);
//    }
//    @Test
//    public void wi() {
//        wiGameController.saveGames();
//        Iterable<WiGames> iterable = wiGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(8);
//    }
//    @Test
//    public void wv() {
//        wvGameController.saveGames();
//        Iterable<WvGames> iterable = wvGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(6);
//    }
//    @Test
//    public void wy() {
//        wyGameController.saveGames();
//        Iterable<WyGames> iterable = wyGameController.getAll();
//        assertThat(iterable.spliterator().getExactSizeIfKnown()).isEqualTo(4);
//    }
//}