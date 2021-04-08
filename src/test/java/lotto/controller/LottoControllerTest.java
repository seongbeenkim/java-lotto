package lotto.controller;

import lotto.domain.*;
import lotto.domain.Dto.RankCountDto;
import lotto.enums.WinningRank;
import lotto.service.LottoService;
import lotto.view.InputView;
import lotto.view.ResultView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LottoControllerTest {
    LottoController systemUnderTest;
    List<String> record;

    @BeforeEach
    void setUp() {
        record = new ArrayList<>();
        systemUnderTest = new LottoController(new TestingInputView(), new TestingResultView(), new LottoService());
    }

    @Test
    @DisplayName("입력 뷰에서 구매 금액을 입력받아 구매 금액 객체를 생성한다.")
    public void createPurchaseAmount() throws Exception {
        PurchaseAmount purchaseAmount = systemUnderTest.createPurchaseAmount();
        assertThat(String.join(" -> ", record)).isEqualTo("purchaseAmount");
        assertThat(purchaseAmount).isEqualTo(new PurchaseAmount(10_000));
    }

    @Test
    @DisplayName("구매 금액을 전달받아 수동, 자동을 합친 구매할 수 있는 총 티켓을 생성한다.")
    public void createLottoTickets() throws Exception {
        LottoTickets lottoTickets = systemUnderTest.createLottoTickets(new PurchaseAmount(10_000));
        assertThat(String.join(" -> ", record)).isEqualTo("manualLottoTicketCount -> manualLottoNumberPhrase -> manualLottoNumbers -> purchaseTickets");
        assertThat(lottoTickets.lottoTickets()).hasSize(10);
    }

    @Test
    @DisplayName("생성한 총 로또 티켓 번호들을 출력한다.")
    public void printLottoTickets() throws Exception {
        systemUnderTest.printLottoTickets(new LottoTickets(new LottoTicket(new LottoNumbers())));
        assertThat(String.join(" -> ", record)).isEqualTo("lottoNumbers");
    }

    @Test
    @DisplayName("입력 뷰에서 당첨 번호를 입력받아 당첨 번호 생성한다.")
    public void createWinningNumbers() throws Exception {
        WinningNumbers winningNumbers = systemUnderTest.createWinningNumbers();
        assertThat(String.join(" -> ", record)).isEqualTo("winningNumbers");
        assertThat(winningNumbers).isEqualTo(WinningNumbers.from(1, 2, 3, 4, 5, 6));
    }

    @Test
    @DisplayName("입력 뷰에서 보너스 볼 번호를 입력받아 보너스 볼 생성한다.")
    public void createBonusBall() throws Exception {
        BonusBall bonusBall = systemUnderTest.createBonusBall(WinningNumbers.from(1, 2, 3, 4, 5, 6));
        assertThat(String.join(" -> ", record)).isEqualTo("bonusBall");
        assertThat(bonusBall).isEqualTo(new BonusBall(7));
    }

    @Test
    @DisplayName("당첨 등수 객체를 인자로 받아 당첨 등수 DTO 생성한다.")
    public void createRanksCountDtos() throws Exception {
        int[] lottoNumbers = {1, 2, 3, 4, 5, 6};
        List<RankCountDto> ranksCountDtos = systemUnderTest.createRanksCountDtos(
                new RanksCount(WinningNumbers.from(lottoNumbers),
                        new LottoTickets(new LottoTicket(LottoNumbers.from(lottoNumbers)))));
        assertThat(ranksCountDtos).hasSize(WinningRank.values().length - 1);
    }

    @Test
    @DisplayName("당첨 등수 DTO를 인자로 받아 당첨 등수 출력한다.")
    public void printStatistics() throws Exception {
        List<RankCountDto> rankCountDtos = Collections.singletonList(new RankCountDto(WinningRank.FIRST_PLACE, 6));
        systemUnderTest.printStatistics(rankCountDtos);
        assertThat(String.join(" -> ", record)).isEqualTo("statistics");
    }

    @Test
    @DisplayName("수익률을 인자로 받아 수익률을 출력한다. ")
    public void printProfitRate() throws Exception {
        systemUnderTest.printProfitRate(new ProfitRate(1));
        systemUnderTest.printProfitRate(new ProfitRate(0.99));
        assertThat(String.join(" -> ", record)).isEqualTo("positiveProfitRate -> negativeProfitRate");
    }

    class TestingInputView extends InputView {
        @Override
        public String purchaseAmount() {
            record.add("purchaseAmount");
            return "10000";
        }

        @Override
        public String manualLottoTicketCount() {
            record.add("manualLottoTicketCount");
            return "1";
        }

        @Override
        public void manualLottoNumberPhrase() {
            record.add("manualLottoNumberPhrase");
        }

        @Override
        public String manualLottoNumbers() {
            record.add("manualLottoNumbers");
            return "1, 2, 3, 4, 5, 6";
        }

        @Override
        public String winningNumbers() {
            record.add("winningNumbers");
            return "1, 2, 3, 4, 5, 6";
        }

        @Override
        public String bonusBall() {
            record.add("bonusBall");
            return "7";
        }
    }

    class TestingResultView extends ResultView {
        @Override
        public void purchaseTickets(int manualNumberOfTicket, int autoNumberOfTickets) {
            record.add("purchaseTickets");
        }

        @Override
        public void lottoNumbers(List<LottoNumber> lottoNumbers) {
            record.add("lottoNumbers");
        }

        @Override
        public void statistics(List<RankCountDto> ranksCount) {
            record.add("statistics");
        }

        @Override
        public void positiveProfitRate(double profitRate) {
            record.add("positiveProfitRate");
        }

        @Override
        public void negativeProfitRate(double profitRate) {
            record.add("negativeProfitRate");
        }
    }
}