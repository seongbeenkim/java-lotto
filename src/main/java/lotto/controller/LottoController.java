package lotto.controller;

import lotto.domain.*;
import lotto.domain.Dto.RankCountDto;
import lotto.utils.SplitUtil;
import lotto.view.InputView;
import lotto.view.ResultView;

import java.util.ArrayList;
import java.util.List;

public class LottoController {

    public void run() {
        PurchaseAmount purchaseAmount = createPurchaseAmount();
        LottoTickets lottoTickets = createLottoTickets(purchaseAmount);
        printLottoTickets(lottoTickets);

        WinningNumbers winningNumbers = createWinningNumbers();
        BonusBall bonusBall = createBonusBall(winningNumbers);
        RanksCount ranksCount = createRanksCount(winningNumbers, lottoTickets);
        matchWith(ranksCount, bonusBall);
        printStatistics(createRanksCountDto(ranksCount));

        ProfitRate profitRate = createProfitRate(ranksCount, purchaseAmount);
        printProfitRate(profitRate);
    }

    private PurchaseAmount createPurchaseAmount() {
        try {
            String purchaseAmount = InputView.purchaseAmount();
            return new PurchaseAmount(purchaseAmount);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return createPurchaseAmount();
        }
    }

    private LottoTickets createLottoTickets(PurchaseAmount purchaseAmount) {
        LottoTicketPrice lottoTicketPrice = new LottoTicketPrice();
        TicketOffice ticketOffice = new TicketOffice(lottoTicketPrice);

        TotalNumberOfTicket totalNumberOfTicket = new TotalNumberOfTicket(purchaseAmount, lottoTicketPrice);
        ManualNumberOfTicket manualNumberOfTicket = manualNumberOfTicket(totalNumberOfTicket);
        AutoNumberOfTicket autoNumberOfTicket = new AutoNumberOfTicket(manualNumberOfTicket, totalNumberOfTicket);

        LottoTickets manualLottoTickets = createManualLottoTickets(manualNumberOfTicket);
        LottoTickets autoLottoTickets = createAutoLottoTickets(totalNumberOfTicket, manualNumberOfTicket);
        ResultView.purchaseTickets(manualNumberOfTicket.numberOfTicket(), autoNumberOfTicket.numberOfTicket());

        return ticketOffice.totalTickets(manualLottoTickets, autoLottoTickets);
    }

    private ManualNumberOfTicket manualNumberOfTicket(TotalNumberOfTicket totalNumberOfTicket) {
        try {
            return new ManualNumberOfTicket(InputView.manualLottoTicketCount(), totalNumberOfTicket);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return manualNumberOfTicket(totalNumberOfTicket);
        }
    }

    private LottoTickets createManualLottoTickets(ManualNumberOfTicket manualNumberOfTicket) {
        List<LottoTicket> manualLottoTickets = new ArrayList<>();

        InputView.manualLottoNumberPhrase();
        for (int i = 0; i < manualNumberOfTicket.numberOfTicket(); i++) {
            LottoTicket manualLottoTicket = manualLottoTicket();
            manualLottoTickets.add(manualLottoTicket);
        }

        return new LottoTickets(manualLottoTickets);
    }

    private LottoTicket manualLottoTicket() {
        try {
            List<String> manualLottoNumbers = SplitUtil.splitByComma(InputView.manualLottoNumbers());
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

    private void printLottoTickets(LottoTickets lottoTickets) {
        for (LottoTicket lottoTicket : lottoTickets.lottoTickets()) {
            ResultView.lottoNumbers(lottoTicket.lottoNumbers());
        }
    }

    private WinningNumbers createWinningNumbers() {
        try {
            List<String> winningNumbers = SplitUtil.splitByComma(InputView.winningNumbers());
            return WinningNumbers.from(winningNumbers);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return createWinningNumbers();
        }
    }

    private BonusBall createBonusBall(WinningNumbers winningNumbers) {
        try {
            LottoNumber bonusNumber = LottoNumber.of(InputView.bonusBall());
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

    private void matchWith(RanksCount ranksCount, BonusBall bonusBall) {
        ranksCount.count(bonusBall);
    }

    private List<RankCountDto> createRanksCountDto(RanksCount ranksCount) {
        return ranksCount.dtos();
    }

    private void printStatistics(List<RankCountDto> ranksCount) {
        ResultView.statistics(ranksCount);
    }

    private ProfitRate createProfitRate(RanksCount ranksCount, PurchaseAmount purchaseAmount) {
        return new ProfitRate(ranksCount.totalPrize(), purchaseAmount);
    }

    private void printProfitRate(ProfitRate profitRate) {
        if (profitRate.isPositive()) {
            ResultView.positiveProfitRate(profitRate.profitRate());
        }

        if (!profitRate.isPositive()) {
            ResultView.negativeProfitRate(profitRate.profitRate());
        }
    }
}
