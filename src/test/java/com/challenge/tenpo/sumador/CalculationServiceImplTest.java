package com.challenge.tenpo.sumador;

import com.challenge.tenpo.sumador.entities.History;
import com.challenge.tenpo.sumador.exception.ExternalServiceException;
import com.challenge.tenpo.sumador.exception.RateLimitExceededException;
import com.challenge.tenpo.sumador.external.ExternalService;
import com.challenge.tenpo.sumador.repository.HistoryRepository;
import com.challenge.tenpo.sumador.service.CalculationService;
import com.challenge.tenpo.sumador.service.RateLimitService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CalculationServiceImplTest {

    @Autowired
    private CalculationService calculationService;

    @MockBean
    private ExternalService externalServiceClient;

    @MockBean
    private HistoryRepository historyRepository;

    @MockBean
    private RateLimitService rateLimitService;


    @Test
    public void shouldReturnCorrectValueTest() {
        // Given
        int number1 = 5;
        int number2 = 5;
        double percentage = 10.0;
        double expected = 11.0;
        when(externalServiceClient.getPercentage()).thenReturn(percentage);

        // When
        double actual = calculationService.addAndApplyPercentage(number1, number2);

        // Then
        Assert.assertEquals(expected, actual, 0.0);
    }

    @Test
    public void shouldRetryAndReturnCorrectValue() {
        // Given
        int number1 = 5;
        int number2 = 5;
        double percentage = 10.0;
        double expected = 11.0;
        when(externalServiceClient.getPercentage()).thenThrow(new ExternalServiceException("Service unavailable"))
                                                   .thenThrow(new ExternalServiceException("Service unavailable"))
                                                   .thenReturn(percentage);

        // When
        double actual = calculationService.addAndApplyPercentage(number1, number2);

        // Then
        Assert.assertEquals(expected, actual, 0.0);
    }

    @Test
    public void calculate_whenInputIsValid_shouldReturnExpectedResult() {
        // Given
        int number1 = 10;
        int number2 = 20;
        double percentage = 15;
        double expectedResult = 34.5;
        when(externalServiceClient.getPercentage()).thenReturn(percentage);

        // When
        double result = calculationService.addAndApplyPercentage(number1, number2);

        // Then
        Assert.assertEquals(expectedResult, result, 0.0);
    }

    @Test
    public void calculate_whenExternalServiceFails_shouldReturnLastValue() {
        // Given
        int number1 = 10;
        int number2 = 20;
        double percentage = 15;
        double expectedResult = 34.5;
        when(externalServiceClient.getPercentage()).thenThrow(new ExternalServiceException());

        History callHistory = History.builder()
                                     .firstValue(number1)
                                     .secondValue(number2)
                                     .percentage(percentage)
                                     .result(expectedResult)
                                     .build();
        when(historyRepository.findTopByOrderByIdDesc()).thenReturn(Optional.of(callHistory));

        // When
        Double result = calculationService.addAndApplyPercentage(number1, number2);

        // Then
        Assert.assertEquals(expectedResult, result, 0.0);
    }

    @Test
    public void calculate_whenExternalServiceFailsAndNoLastValue_shouldThrowException() {
        // Given
        int number1 = 10;
        int number2 = 20;
        when(externalServiceClient.getPercentage()).thenThrow(new ExternalServiceException());
        when(historyRepository.findTopByOrderByIdDesc()).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> calculationService.addAndApplyPercentage(number1, number2)).isInstanceOf(ExternalServiceException.class);
    }

    @Test
    public void calculate_whenExternalServiceFailsAndMaxRetriesReached_shouldThrowException() {
        // Given
        int number1 = 10;
        int number2 = 20;
        when(externalServiceClient.getPercentage()).thenThrow(new ExternalServiceException());

        // Then
        assertThatThrownBy(() -> calculationService.addAndApplyPercentage(number1, number2)).isInstanceOf(ExternalServiceException.class);
        verify(externalServiceClient, times(3)).getPercentage();
    }

    @Test
    public void addAndApplyPercentage_whenSumIsZero_shouldReturnZero() {
        // Given
        int number1 = 0;
        int number2 = 0;
        when(externalServiceClient.getPercentage()).thenReturn(10.0);

        // When
        double result = calculationService.addAndApplyPercentage(number1, number2);

        // Then
        Assert.assertEquals(0.0, result, 0.0);
    }

    @Test
    public void addAndApplyPercentage_whenExternalServiceReturnsNull_shouldThrowException() {
        // Given
        int number1 = 10;
        int number2 = 20;
        when(externalServiceClient.getPercentage()).thenReturn(null);

        // Then
        assertThatThrownBy(() -> calculationService.addAndApplyPercentage(number1, number2))
                .isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("External service returned null");
    }

    @Test
    public void addAndApplyPercentage_whenRateLimitExceeded_shouldThrowException() throws ExternalServiceException {
        // Given
        int number1 = 10;
        int number2 = 20;
        when(externalServiceClient.getPercentage()).thenReturn(10.0);
        doThrow(RateLimitExceededException.class).when(rateLimitService)
                                                 .acquire();

        // Then
        assertThatThrownBy(() -> calculationService.addAndApplyPercentage(number1, number2))
                .isInstanceOf(RateLimitExceededException.class);
    }

    @Test
    public void addAndApplyPercentage_whenResultNotInCache_shouldSaveToCache() throws ExternalServiceException {
        // Given
        int number1 = 10;
        int number2 = 20;
        double percentage = 10.0;
        when(externalServiceClient.getPercentage()).thenReturn(percentage);
        when(historyRepository.findTopByOrderByIdDesc()).thenReturn(Optional.empty());

        // When
        double result = calculationService.addAndApplyPercentage(number1, number2);

        // Then
        Assert.assertEquals(number1 + number2 + (number1 + number2) * percentage / 100, result, 0.0);
        verify(historyRepository, times(1)).save(any(History.class));
    }

}
