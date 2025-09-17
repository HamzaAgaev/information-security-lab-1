package info.sec.lab1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CalculationResultResponse {
    private String expression;
    private Double result;
}
