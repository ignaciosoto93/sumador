package com.challenge.tenpo.sumador.service;

import com.challenge.tenpo.sumador.entities.History;
import com.challenge.tenpo.sumador.exception.ExternalServiceException;
import com.challenge.tenpo.sumador.exception.RateLimitExceededException;
import com.challenge.tenpo.sumador.external.ExternalService;
import com.challenge.tenpo.sumador.repository.HistoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@EnableRetry
@Service
public class CalculationServiceImpl implements CalculationService {

    private final ExternalService externalServiceClient;
    private final RateLimitService rateLimitService;
    private final HistoryRepository historyRepository;

    public CalculationServiceImpl(ExternalService externalServiceClient, RateLimitService rateLimitService,
                                  HistoryRepository historyRepository) {
        this.externalServiceClient = externalServiceClient;
        this.rateLimitService = rateLimitService;
        this.historyRepository = historyRepository;
    }

    @Retryable(value = {ExternalServiceException.class}, backoff = @Backoff(delay = 1000))
    @Override
    public Double addAndApplyPercentage(int num1, int num2) throws ExternalServiceException,
            RateLimitExceededException {
        // get the percentage increase from the external service
        rateLimitService.acquire();
        Double percentageIncrease;
        try {
            percentageIncrease = externalServiceClient.getPercentage();
        } catch (ExternalServiceException e) {
            // If the external service fails, return the last cached value
            Optional<History> lastHistory = historyRepository.findTopByOrderByIdDesc();
            if (lastHistory.isPresent()) {
                return lastHistory.get()
                                  .getResult();
            } else {
                // If there is no cached value, throw an exception
                throw new ExternalServiceException("External service is not available and there is no cached value");
            }
        }

        // add the two numbers and apply the percentage increase
        if (percentageIncrease == null) {
            throw new ExternalServiceException("External service returned null");
        }
        Double result = num1 + num2 + (num1 + num2) * percentageIncrease / 100;

        // save the calculation to the database
        History history = History.builder()
                                 .firstValue(num1)
                                 .secondValue(num2)
                                 .percentage(percentageIncrease)
                                 .result(result)
                                 .build();
        historyRepository.save(history);

        return result;
    }


    @Override
    public List<History> getHistory(int page, int size) {
        // calculate the offset and limit for pagination
        int offset = (page - 1) * size;

        // query the database for the history with pagination
        return historyRepository.findAll(PageRequest.of(offset, size, Sort.by("id")
                                                                          .descending()))
                                .getContent();
    }


}

