package com.challenge.tenpo.sumador.external;

import com.challenge.tenpo.sumador.exception.ExternalServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling

public class MockExternalService implements ExternalService {

    private static final Logger logger = LoggerFactory.getLogger(MockExternalService.class);

    private final double percentage;

    public MockExternalService(@Value("${mock.percentage}") double percentage) {
        this.percentage = percentage;
    }

    @Override
    @Cacheable("percentageCache")
    public Double getPercentage() throws ExternalServiceException {
        logger.info("Returning fixed percentage from external service {}", percentage);
        return percentage;
    }

    @CacheEvict(allEntries = true, value = "percentageCache")
    @Scheduled(fixedDelay = 30 * 60 * 1000, initialDelay = 500)
    public void reportCacheEvict() {
        System.out.println("Flush Cache ");
    }
}
