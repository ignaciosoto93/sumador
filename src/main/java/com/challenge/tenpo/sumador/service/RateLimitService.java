package com.challenge.tenpo.sumador.service;
import com.challenge.tenpo.sumador.exception.RateLimitExceededException;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RateLimitService {
    private final RateLimiter rateLimiter;

    public RateLimitService(@Value("${app.rateLimit}") double rateLimit) {
        this.rateLimiter = RateLimiter.create(rateLimit);
    }

    public void acquire() throws RateLimitExceededException {
        if (!rateLimiter.tryAcquire()) {
            throw new RateLimitExceededException("Rate limit exceeded. Try again later.");
        }
    }
}
