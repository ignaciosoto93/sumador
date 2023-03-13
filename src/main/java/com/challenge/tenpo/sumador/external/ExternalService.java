package com.challenge.tenpo.sumador.external;

import com.challenge.tenpo.sumador.exception.ExternalServiceException;

public interface ExternalService {
    Double getPercentage() throws ExternalServiceException;
}

