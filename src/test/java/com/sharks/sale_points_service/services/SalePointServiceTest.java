package com.sharks.sale_points_service.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.sharks.sale_points_service.exceptions.NameAlreadyExistsException;
import com.sharks.sale_points_service.exceptions.SalePointNotFoundException;
import com.sharks.sale_points_service.models.SalePoint;
import com.sharks.sale_points_service.models.dtos.NewSalePoint;
import com.sharks.sale_points_service.models.dtos.SalePointDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.sharks.sale_points_service.repositories.SalePointRepository;
import com.sharks.sale_points_service.services.impl.SalePointServiceImpl;

import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class SalePointServiceTest {

    @Mock
    private SalePointRepository salePointRepository;

    @InjectMocks
    private SalePointServiceImpl salePointService;

    private SalePoint salePoint;
    private NewSalePoint newSalePoint;

    @BeforeEach
    public void setUp() {
        salePoint = new SalePoint();
        salePoint.setName("Test Point");
        ReflectionTestUtils.setField(salePoint, "id", 1L);
        newSalePoint = new NewSalePoint("New Point");
    }

    @Test
    void testGetAllSalePointDTOs_ReturnsList() {
        when(salePointRepository.findAll()).thenReturn(List.of(salePoint));

        List<SalePointDTO> result = salePointService.getAllSalePointDTOs();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Point", result.get(0).getName());
    }

    @Test
    void testGetAllSalePointDTOs_ReturnsEmptyList() {
        when(salePointRepository.findAll()).thenReturn(Collections.emptyList());

        List<SalePointDTO> result = salePointService.getAllSalePointDTOs();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetSalePointById_Found() {
        when(salePointRepository.findById(1L)).thenReturn(Optional.of(salePoint));

        SalePoint result = salePointService.getSalePointById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetSalePointById_NotFound_ThrowsException() {
        when(salePointRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(SalePointNotFoundException.class, () -> salePointService.getSalePointById(2L));
    }

    @Test
    void testCreateSalePoint_Success() {
        when(salePointRepository.save(any(SalePoint.class))).thenReturn(salePoint);

        SalePointDTO result = salePointService.createSalePoint(newSalePoint);
        assertNotNull(result);
        assertEquals(SalePointDTO.class, result.getClass());
        assertEquals("Test Point", result.getName());
    }

    @Test
    void testCreateSalePoint_NameAlreadyExists_ThrowsException() {
        NewSalePoint duplicate = new NewSalePoint("New Point");

        when(salePointRepository.existsByName("New Point")).thenReturn(true);

        assertThrows(NameAlreadyExistsException.class, () -> salePointService.createSalePoint(duplicate));
    }

    @Test
    void testUpdateSalePoint_Success() {
        when(salePointRepository.findById(1L)).thenReturn(Optional.of(salePoint));
        when(salePointRepository.save(any(SalePoint.class))).thenReturn(salePoint);

        SalePointDTO result = salePointService.updateSalePoint(1L, newSalePoint);
        assertNotNull(result);
        assertEquals(SalePointDTO.class, result.getClass());
        assertEquals("New Point", result.getName());
    }

    @Test
    void testUpdateSalePoint_NotFound_ThrowsException() {
        when(salePointRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(SalePointNotFoundException.class, () -> salePointService.updateSalePoint(2L, newSalePoint));
    }

    @Test
    void testDeleteSalePoint_Success() {
        when(salePointRepository.findById(1L)).thenReturn(Optional.of(salePoint));
        doNothing().when(salePointRepository).delete(any(SalePoint.class));

        assertDoesNotThrow(() -> salePointService.deleteSalePoint(1L));
        verify(salePointRepository, times(1)).delete(any(SalePoint.class));
    }

    @Test
    void testDeleteSalePoint_NotFound_ThrowsException() {
        when(salePointRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(SalePointNotFoundException.class, () -> salePointService.deleteSalePoint(2L));
    }
}
