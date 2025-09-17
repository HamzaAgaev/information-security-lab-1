package info.sec.lab1.service;

import info.sec.lab1.entity.CalculationResult;
import info.sec.lab1.entity.User;
import info.sec.lab1.repository.CalculationResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CalculatorService {

    private final CalculationResultRepository calculationResultRepository;

    private static final Pattern EXPRESSION_PATTERN =
            Pattern.compile("^[0-9\\s+\\-*/().eE]+$");

    public Double calculateAndSaveResult(String expression, User user) throws ScriptException {
        Double result = calculate(expression);
        CalculationResult calculationResult = new CalculationResult();
        calculationResult.setResult(result);
        calculationResult.setUser(user);
        calculationResult.setExpression(expression);
        calculationResultRepository.save(calculationResult);
        return result;
    }

    private Double calculate(String expression) throws ScriptException {
        if (!isValidMathExpression(expression)) {
            throw new IllegalArgumentException("Invalid expression");
        }

        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");

        Object result = engine.eval(expression);

        return Double.parseDouble(result.toString());
    }

    private boolean isValidMathExpression(String expression) {
        return EXPRESSION_PATTERN.matcher(expression.trim()).matches();
    }

    public List<CalculationResult> getResultsByUser(User user) {
        return calculationResultRepository.findAllByUser(user);
    }
}
