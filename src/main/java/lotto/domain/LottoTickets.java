package lotto.domain;

import lotto.enums.WinningRank;
import lotto.utils.SplitUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LottoTickets {
    private final List<LottoTicket> lottoTickets;

    public LottoTickets() {
        this(new ArrayList<>());
    }

    public LottoTickets(LottoTicket lottoTicket) {
        this(Collections.singletonList(lottoTicket));
    }

    public LottoTickets(LottoTickets manualLottoTickets, LottoTickets autoLottoTickets) {
        this(combine(manualLottoTickets, autoLottoTickets));
    }

    public LottoTickets(List<LottoTicket> lottoTickets) {
        this.lottoTickets = new ArrayList<>(lottoTickets);
    }

    public static LottoTickets createBy(List<String> allLottoNumbers) {
        LottoTickets lottoTickets = new LottoTickets();

        for (String lottoNumbers : allLottoNumbers) {
            List<String> splitedLottoNumbers = SplitUtil.splitByComma(lottoNumbers);
            lottoTickets.add(new LottoTicket(LottoNumbers.from(splitedLottoNumbers)));
        }

        return lottoTickets;
    }

    public static LottoTickets createBy(int numberOfTicket) {
        LottoTickets lottoTickets = new LottoTickets();

        for (int i = 0; i < numberOfTicket; i++) {
            lottoTickets.add(new LottoTicket(new LottoNumbers()));
        }

        return lottoTickets;
    }

    public static List<LottoTicket> combine(LottoTickets manualLottoTickets, LottoTickets autoLottoTickets) {
        List<LottoTicket> allLottoTickets = new ArrayList<>();
        allLottoTickets.addAll(manualLottoTickets.lottoTickets());
        allLottoTickets.addAll(autoLottoTickets.lottoTickets());

        return allLottoTickets;
    }

    public void add(LottoTicket lottoTicket) {
        lottoTickets.add(lottoTicket);
    }

    public RanksCount ranksCount(WinningNumbers winningNumbers) {
        RanksCount ranksCount = new RanksCount();

        for (LottoTicket lottoTicket : lottoTickets) {
            WinningRank winningRank = winningNumbers.matchWith(lottoTicket);
            ranksCount.add(winningRank);
        }

        return ranksCount;
    }

    public List<LottoTicket> lottoTickets() {
        return Collections.unmodifiableList(lottoTickets);
    }
}
