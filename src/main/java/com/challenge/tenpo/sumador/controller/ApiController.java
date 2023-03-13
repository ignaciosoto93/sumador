package com.challenge.tenpo.sumador.controller;

import com.challenge.tenpo.sumador.entities.History;
import com.challenge.tenpo.sumador.exception.RateLimitExceededException;
import com.challenge.tenpo.sumador.service.CalculationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final CalculationService apiService;

    public ApiController(CalculationService apiService) {
        this.apiService = apiService;
    }

    @PostMapping("/add-numbers")
    public ResponseEntity<Double> addNumbersWithPercentageIncrease(@RequestParam int num1, @RequestParam int num2) throws RateLimitExceededException {
        double result = apiService.addAndApplyPercentage(num1, num2);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/history")
    public ResponseEntity<List<History>> getApiHistory(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        List<History> apiHistoryPage = apiService.getHistory(page,size);
        return ResponseEntity.ok(apiHistoryPage);
    }
}
