package app.web.dto;

import lombok.*;

import java.math.BigDecimal;


@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Summary {

    private BigDecimal totalRevenue;
    private int totalBookedDays;
    private int totalGuestsVisited;

    public static Summary combine(Summary s1, Summary s2) {
        return new Summary(
                s1.totalRevenue.add(s2.totalRevenue),
                s1.totalBookedDays + s2.totalBookedDays,
                s1.totalGuestsVisited + s2.totalGuestsVisited
        );
    }
}
