package com.challenge.tenpo.sumador.service;

import com.challenge.tenpo.sumador.entities.History;
import com.challenge.tenpo.sumador.exception.ExternalServiceException;
import com.challenge.tenpo.sumador.exception.RateLimitExceededException;

import java.util.List;

public interface CalculationService {


    Double addAndApplyPercentage(int num1, int num2) throws ExternalServiceException, RateLimitExceededException;

    List<History> getHistory(int page, int size);
}
