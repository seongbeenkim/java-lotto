package lotto.controller;

import lotto.domain.*;
import lotto.domain.Dto.RankCountDto;
import lotto.enums.WinningRank;
import lotto.utils.SplitUtil;
import lotto.view.InputView;
import lotto.view.ResultView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LottoController {
    private final InputView inputView;
    private final ResultView resultView;

    public LottoController(InputView inputView, ResultView resultView) {
        this.inputView = inputView;
        this.resultView = resultView;
    }

    public void run() {
        PurchaseAmount purchaseAmount = createPurchaseAmount();
        LottoTickets lottoTickets = createLottoTickets(purchaseAmount);
        printLottoTickets(lottoTickets);

        WinningNumbers winningNumbers = createWinningNumbers();
        BonusBall bonusBall = createBonusBall(winningNumbers);
        RanksCount ranksCount = createRanksCount(winningNumbers, lottoTickets);
        matchWith(ranksCount, bonusBall);
        printStatistics(createRanksCountDtos(ranksCount));

        ProfitRate profitRate = createProfitRate(ranksCount, purchaseAmount);
        printProfitRate(profitRate);
    }

    protected PurchaseAmount createPurchaseAmount() {
        try {
            String purchaseAmount = inputView.purchaseAmount();
            return new PurchaseAmount(purchaseAmount);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return createPurchaseAmount();
        }
    }

    protected LottoTickets createLottoTickets(PurchaseAmount purchaseAmount) {
        LottoTicketPrice lottoTicketPrice = new LottoTicketPrice();
        TicketOffice ticketOffice = new TicketOffice(lottoTicketPrice);

        TotalNumberOfTicket totalNumberOfTicket = new TotalNumberOfTicket(purchaseAmount, lottoTicketPrice);
        ManualNumberOfTicket manualNumberOfTicket = manualNumberOfTicket(totalNumberOfTicket);
        AutoNumberOfTicket autoNumberOfTicket = new AutoNumberOfTicket(manualNumberOfTicket, totalNumberOfTicket);

        LottoTickets manualLottoTickets = createManualLottoTickets(manualNumberOfTicket);
        LottoTickets autoLottoTickets = createAutoLottoTickets(totalNumberOfTicket, manualNumberOfTicket);
        resultView.purchaseTickets(manualNumberOfTicket.numberOfTicket(), autoNumberOfTicket.numberOfTicket());

        return ticketOffice.totalTickets(manualLottoTickets, autoLottoTickets);
    }

    private ManualNumberOfTicket manualNumberOfTicket(TotalNumberOfTicket totalNumberOfTicket) {
        try {
            return new ManualNumberOfTicket(inputView.manualLottoTicketCount(), totalNumberOfTicket);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return manualNumberOfTicket(totalNumberOfTicket);
        }
    }

    private LottoTickets createManualLottoTickets(ManualNumberOfTicket manualNumberOfTicket) {
        List<LottoTicket> manualLottoTickets = new ArrayList<>();

        inputView.manualLottoNumberPhrase();
        for (int i = 0; i < manualNumberOfTicket.numberOfTicket(); i++) {
            LottoTicket manualLottoTicket = manualLottoTicket();
            manualLottoTickets.add(manualLottoTicket);
        }

        return new LottoTickets(manualLottoTickets);
    }

    private LottoTicket manualLottoTicket() {
        try {
            List<String> manualLottoNumbers = SplitUtil.splitByComma(inputView.manualLottoNumbers());
            return new LottoTicket(LottoNumbers.from(manualLottoNumbers));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return manualLottoTicket();
        }
    }

    private LottoTickets createAutoLottoTickets(TotalNumberOfTicket totalNumberOfTicket, ManualNumberOfTicket manualNumberOfTicket) {
        LottoTickets autoLottoTickets = new LottoTickets(new ArrayList<>());

        for (int i = 0; i < totalNumberOfTicket.minus(manualNumberOfTicket); i++) {
            autoLottoTickets.add(new LottoTicket(new LottoNumbers()));
        }

        return autoLottoTickets;
    }

    protected void printLottoTickets(LottoTickets lottoTickets) {
        for (LottoTicket lottoTicket : lottoTickets.lottoTickets()) {
            resultView.lottoNumbers(lottoTicket.lottoNumbers());
        }
    }

    protected WinningNumbers createWinningNumbers() {
        try {
            List<String> winningNumbers = SplitUtil.splitByComma(inputView.winningNumbers());
            return WinningNumbers.from(winningNumbers);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return createWinningNumbers();
        }
    }

    protected BonusBall createBonusBall(WinningNumbers winningNumbers) {
        try {
            LottoNumber bonusNumber = LottoNumber.of(inputView.bonusBall());
            winningNumbers.check(bonusNumber);
            return new BonusBall(bonusNumber);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return createBonusBall(winningNumbers);
        }
    }

    private RanksCount createRanksCount(WinningNumbers winningNumbers, LottoTickets lottoTickets) {
        return new RanksCount(winningNumbers, lottoTickets);
    }

    protected void matchWith(RanksCount ranksCount, BonusBall bonusBall) {
        ranksCount.count(bonusBall);
    }

    protected List<RankCountDto> createRanksCountDtos(RanksCount ranksCount) {
        List<RankCountDto> rankCountDtos = new ArrayList<>();

        for (Map.Entry<WinningRank, Integer> rank : ranksCount.entrySet()) {
            RankCountDto rankCountDto = new RankCountDto(rank.getKey(), rank.getValue());
            rankCountDtos.add(rankCountDto);
        }

        return rankCountDtos;
    }

    protected void printStatistics(List<RankCountDto> ranksCount) {
        resultView.statistics(ranksCount);
    }

    private ProfitRate createProfitRate(RanksCount ranksCount, PurchaseAmount purchaseAmount) {
        return new ProfitRate(ranksCount.totalPrize(), purchaseAmount);
    }

    protected void printProfitRate(ProfitRate profitRate) {
        if (profitRate.isPositive()) {
            resultView.positiveProfitRate(profitRate.profitRate());
        }

        if (!profitRate.isPositive()) {
            resultView.negativeProfitRate(profitRate.profitRate());
        }
    }
}
