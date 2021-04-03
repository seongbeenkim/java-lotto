package lotto.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class PurchaseAmountTest {

    @Test
    @DisplayName("구매 금액 객체 생성")
    public void create() throws Exception {
        PurchaseAmount purchaseAmount = new PurchaseAmount(1_000);
        assertThat(purchaseAmount).isEqualTo(new PurchaseAmount(1_000));
        purchaseAmount = new PurchaseAmount("1000");
        assertThat(purchaseAmount).isEqualTo(new PurchaseAmount("1000"));
    }

    @Test
    @DisplayName("1000원 미만일 시 예외")
    public void checkMinimum() throws Exception {
        assertThatIllegalArgumentException().isThrownBy(() -> new PurchaseAmount(999));
        assertThatIllegalArgumentException().isThrownBy(() -> new PurchaseAmount("999"));
    }

    @Test
    @DisplayName("구매금액에 따른 구매할 수 있는 로또 티켓 장수 구하기")
    public void dividedBy() throws Exception {
        //given
        PurchaseAmount purchaseAmount = new PurchaseAmount(1_000);

        //when
        int numberOfTicket = purchaseAmount.dividedBy(1_000);

        //then
        assertThat(numberOfTicket).isEqualTo(1);
    }

    @Test
    @DisplayName("총 당첨 금액을 구매 금액으로 나누기")
    public void divide() throws Exception {
        int amount = 1_000;
        PurchaseAmount purchaseAmount = new PurchaseAmount(amount);
        int totalPrize = 10_000;
        assertThat(purchaseAmount.divide(totalPrize)).isEqualTo((double) totalPrize / amount);
    }
}
