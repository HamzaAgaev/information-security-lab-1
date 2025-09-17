package info.sec.lab1.controller;

import info.sec.lab1.dto.CalculationRequest;
import info.sec.lab1.dto.CalculationResultResponse;
import info.sec.lab1.service.AuthService;
import info.sec.lab1.service.CalculatorService;
import lombok.RequiredArgsConstructor;
import org.openjdk.nashorn.internal.runtime.ParserException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CalculationController {

    private final CalculatorService calculatorService;
    private final AuthService authService;

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException exception) {
        return "Illegal arguments were used";
    }

    @ExceptionHandler(ParserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleParserException(ParserException exception) {
        return "Cannot parse this expression";
    }

    @GetMapping("/data")
    public List<CalculationResultResponse> getResultsByUser() {
        return calculatorService.getResultsByUser(authService.getCurrentUser())
                .stream().map(result -> new CalculationResultResponse(result.getExpression(), result.getResult()))
                .toList();
    }

    @PostMapping("/calculate")
    public Double calculate(@RequestBody CalculationRequest calculationRequest) throws Exception {
        return calculatorService.calculateAndSaveResult(calculationRequest.getExpression(), authService.getCurrentUser());
    }
}
