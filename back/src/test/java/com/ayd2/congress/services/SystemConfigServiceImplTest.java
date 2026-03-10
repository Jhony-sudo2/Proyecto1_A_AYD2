package com.ayd2.congress.services;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ayd2.congress.dtos.SysConfig.SysConfigResponse;
import com.ayd2.congress.dtos.SysConfig.SysConfigUpdate;
import com.ayd2.congress.exceptions.InvalidPriceException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.mappers.ConfigMapper;
import com.ayd2.congress.models.SystemConfigEntity;
import com.ayd2.congress.repositories.SystemConfigRepository;
import com.ayd2.congress.services.systemconfig.SystemConfigServiceImpl;

@ExtendWith(MockitoExtension.class)
public class SystemConfigServiceImplTest {

    private static final Long CONFIG_ID = 1L;
    private static final Double CURRENT_PRICE = 100.0;
    private static final Double CURRENT_PERCENTAGE = 15.0;

    private static final Double UPDATED_PRICE = 150.0;
    private static final Double UPDATED_PERCENTAGE = 20.0;

    private static final Double INVALID_PRICE = 50.0;

    @Mock
    private SystemConfigRepository repository;

    @Mock
    private ConfigMapper mapper;

    @InjectMocks
    private SystemConfigServiceImpl systemConfigService;

    private SystemConfigEntity configEntity;
    private SysConfigResponse configResponse;

    @BeforeEach
    void setUp() {
        configEntity = new SystemConfigEntity();
        configEntity.setId(CONFIG_ID);
        configEntity.setPrice(CURRENT_PRICE);
        configEntity.setPercentage(CURRENT_PERCENTAGE);

        configResponse = new SysConfigResponse(CURRENT_PRICE, CURRENT_PERCENTAGE);
    }

    @Test
    void testUpdateConfiguration() throws Exception {
        // Arrange
        SysConfigUpdate request = new SysConfigUpdate(UPDATED_PRICE, UPDATED_PERCENTAGE);
        SystemConfigServiceImpl spy = spy(systemConfigService);
        ArgumentCaptor<SystemConfigEntity> configCaptor = ArgumentCaptor.forClass(SystemConfigEntity.class);

        doReturn(configEntity).when(spy).getConfiguration();

        SysConfigResponse updatedResponse = new SysConfigResponse(UPDATED_PRICE, UPDATED_PERCENTAGE);
        when(mapper.toResponse(configEntity)).thenReturn(updatedResponse);

        // Act
        SysConfigResponse result = spy.update(request);

        // Assert
        assertAll(
                () -> verify(repository).save(configCaptor.capture()),
                () -> assertEquals(UPDATED_PRICE, configCaptor.getValue().getPrice()),
                () -> assertEquals(UPDATED_PERCENTAGE, configCaptor.getValue().getPercentage()),
                () -> assertEquals(UPDATED_PRICE, result.getPrice()),
                () -> assertEquals(UPDATED_PERCENTAGE, result.getPercentage())
        );
    }

    @Test
    void testUpdateConfigurationWhenPriceIsLowerThanCurrentPrice() throws Exception {
        // Arrange
        SysConfigUpdate request = new SysConfigUpdate(INVALID_PRICE, UPDATED_PERCENTAGE);
        SystemConfigServiceImpl spy = spy(systemConfigService);

        doReturn(configEntity).when(spy).getConfiguration();

        // Assert
        assertThrows(InvalidPriceException.class,
                () -> spy.update(request));
    }

    @Test
    void testGetConfiguration() throws Exception {
        // Arrange
        when(repository.findById(CONFIG_ID)).thenReturn(Optional.of(configEntity));

        // Act
        SystemConfigEntity result = systemConfigService.getConfiguration();

        // Assert
        assertAll(
                () -> assertEquals(CONFIG_ID, result.getId()),
                () -> assertEquals(CURRENT_PRICE, result.getPrice()),
                () -> assertEquals(CURRENT_PERCENTAGE, result.getPercentage())
        );
    }

    @Test
    void testGetConfigurationWhenNotFound() {
        // Arrange
        when(repository.findById(CONFIG_ID)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NotFoundException.class,
                () -> systemConfigService.getConfiguration());
    }

    @Test
    void testGetConfigResponse() throws Exception {
        // Arrange
        SystemConfigServiceImpl spy = spy(systemConfigService);

        doReturn(configEntity).when(spy).getConfiguration();
        when(mapper.toResponse(configEntity)).thenReturn(configResponse);

        // Act
        SysConfigResponse result = spy.getConfigResponse();

        // Assert
        assertAll(
                () -> assertEquals(CURRENT_PRICE, result.getPrice()),
                () -> assertEquals(CURRENT_PERCENTAGE, result.getPercentage())
        );
    }

    @Test
    void testCreateShouldThrowUnsupportedOperationException() {
        // Assert
        assertThrows(UnsupportedOperationException.class,
                () -> systemConfigService.create());
    }
}