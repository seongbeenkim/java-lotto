package lotto.domain;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LottoTicket {
    private static final int PRICE = 1000;
    private final LottoNumbers lottoNumbers;

    public LottoTicket(LottoNumbers lottoNumbers) {
        this.lottoNumbers = lottoNumbers;
    }

    public List<Integer> lottoNumbers() {
        return Collections.unmodifiableList(lottoNumbers.lottoNumbers());
    }

    public int price() {
        return PRICE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LottoTicket that = (LottoTicket) o;
        return Objects.equals(lottoNumbers, that.lottoNumbers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lottoNumbers);
    }
}
