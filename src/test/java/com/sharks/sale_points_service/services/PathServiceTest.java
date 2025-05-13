package com.sharks.sale_points_service.services;

import com.sharks.sale_points_service.exceptions.PathAlreadyExistsException;
import com.sharks.sale_points_service.exceptions.PathNotFoundException;
import com.sharks.sale_points_service.exceptions.SalePointNotFoundException;
import com.sharks.sale_points_service.exceptions.SameSalePointException;
import com.sharks.sale_points_service.models.Path;
import com.sharks.sale_points_service.models.PathCost;
import com.sharks.sale_points_service.models.SalePoint;
import com.sharks.sale_points_service.models.SalePointCost;
import com.sharks.sale_points_service.models.dtos.NewPath;
import com.sharks.sale_points_service.models.dtos.NewPathWithoutIds;
import com.sharks.sale_points_service.models.dtos.PathDTO;
import com.sharks.sale_points_service.repositories.PathRepository;
import com.sharks.sale_points_service.services.impl.PathServiceImpl;
import com.sharks.sale_points_service.services.impl.SalePointServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PathServiceTest {

    @Mock
    private PathRepository pathRepository;

    @Mock
    private SalePointServiceImpl salePointService;

    @InjectMocks
    private PathServiceImpl pathService;

    private SalePoint a;
    private SalePoint b;
    private Path path;

    @BeforeEach
    void setUp() {
        a = new SalePoint("A");
        ReflectionTestUtils.setField(a, "id", 1L);
        b = new SalePoint("B");
        ReflectionTestUtils.setField(b, "id", 2L);
        path = new Path(a, b, 10.0);
    }

    @Test
    void testGetAllPathDTOs_ReturnsList() {
        when(pathRepository.findAll()).thenReturn(List.of(path));

        List<PathDTO> result = pathService.getAllPathDTOs();
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getSalePointA().getId());
        assertEquals(2L, result.get(0).getSalePointB().getId());
    }

    @Test
    void testGetAllPathDTOs_ReturnsEmptyList() {
        when(pathRepository.findAll()).thenReturn(Collections.emptyList());

        List<PathDTO> result = pathService.getAllPathDTOs();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPathByIds_Found() {
        when(pathRepository.findBySalePointA_IdAndSalePointB_Id(1L, 2L)).thenReturn(Optional.of(path));

        Path result = pathService.getPathByIds(1L, 2L);
        assertNotNull(result);
        assertEquals(1L, result.getSalePointA().getId());
        assertEquals(2L, result.getSalePointB().getId());
    }

    @Test
    void testGetPathByIds_FoundAnyDirection() {
        Path reversePath = new Path(b, a, 10.0);

        when(pathRepository.findBySalePointA_IdAndSalePointB_Id(1L, 2L)).thenReturn(Optional.empty());
        when(pathRepository.findBySalePointA_IdAndSalePointB_Id(2L, 1L)).thenReturn(Optional.of(reversePath));

        Path result = pathService.getPathByIds(1L, 2L);
        assertNotNull(result);
        assertEquals(2L, result.getSalePointA().getId());
        assertEquals(1L, result.getSalePointB().getId());
    }

    @Test
    void testGetPathByIds_NotFound_ThrowsException() {
        when(pathRepository.findBySalePointA_IdAndSalePointB_Id(1L, 2L)).thenReturn(Optional.empty());
        when(pathRepository.findBySalePointA_IdAndSalePointB_Id(2L, 1L)).thenReturn(Optional.empty());

        assertThrows(PathNotFoundException.class, () -> pathService.getPathByIds(1L, 2L));
    }

    @Test
    void testGetPathsById_ReturnsBothDirectionsList() {
        Path ab = new Path(a, b, 10.0);
        Path ba = new Path(b, a, 15.0);

        when(pathRepository.findBySalePointA_Id(1L)).thenReturn(List.of(ab));
        when(pathRepository.findBySalePointB_Id(1L)).thenReturn(List.of(ba));

        List<Path> result = pathService.getPathsById(1L);
        assertEquals(2, result.size());
    }

    @Test
    void testGetPathsById_ReturnsEmptyList() {
        when(pathRepository.findBySalePointA_Id(1L)).thenReturn(Collections.emptyList());
        when(pathRepository.findBySalePointB_Id(1L)).thenReturn(Collections.emptyList());

        List<Path> result = pathService.getPathsById(1L);
        assertEquals(0, result.size());
    }

    @Test
    void testCreatePath_Success() {
        NewPath newPath = new NewPath(1L, 2L, 10.0);

        when(pathRepository.existsBySalePointA_IdAndSalePointB_Id(1L, 2L)).thenReturn(false);
        when(pathRepository.existsBySalePointA_IdAndSalePointB_Id(2L, 1L)).thenReturn(false);
        when(salePointService.getSalePointById(1L)).thenReturn(a);
        when(salePointService.getSalePointById(2L)).thenReturn(b);
        when(pathRepository.save(any(Path.class))).thenReturn(path);

        PathDTO result = pathService.createPath(newPath);
        assertNotNull(result);
        assertEquals(PathDTO.class, result.getClass());
        assertEquals(1L, result.getSalePointA().getId());
        assertEquals(2L, result.getSalePointB().getId());
        assertEquals(10.0, result.getCost());
    }

    @Test
    void testCreatePath_SameSalePoint_ThrowsException() {
        NewPath newPath = new NewPath(1L, 1L, 10.0);
        assertThrows(SameSalePointException.class, () -> pathService.createPath(newPath));
    }

    @Test
    void testCreatePath_PathAlreadyExists_ThrowsException() {
        NewPath newPath = new NewPath(1L, 2L, 10.0);
        when(pathRepository.existsBySalePointA_IdAndSalePointB_Id(1L, 2L)).thenReturn(true);
        assertThrows(PathAlreadyExistsException.class, () -> pathService.createPath(newPath));
    }

    @Test
    void testUpdatePath_Success() {
        NewPathWithoutIds newPath = new NewPathWithoutIds(20.0);

        when(pathRepository.findBySalePointA_IdAndSalePointB_Id(1L, 2L)).thenReturn(Optional.of(path));
        when(pathRepository.save(any(Path.class))).thenReturn(new Path(a, b, 20.0));

        PathDTO result = pathService.updatePath(1L, 2L, newPath);
        assertEquals(PathDTO.class, result.getClass());
        assertEquals(20.0, result.getCost());
    }

    @Test
    void testUpdatePath_NotFound_ThrowsException() {
        NewPathWithoutIds newPath = new NewPathWithoutIds(20.0);

        when(pathRepository.findBySalePointA_IdAndSalePointB_Id(1L, 2L)).thenReturn(Optional.empty());
        when(pathRepository.findBySalePointA_IdAndSalePointB_Id(2L, 1L)).thenReturn(Optional.empty());

        assertThrows(PathNotFoundException.class, () -> pathService.updatePath(1L, 2L, newPath));
    }

    @Test
    void testDeletePath_Success() {
        when(pathRepository.findBySalePointA_IdAndSalePointB_Id(1L, 2L)).thenReturn(Optional.of(path));
        doNothing().when(pathRepository).delete(path);

        assertDoesNotThrow(() -> pathService.deletePath(1L, 2L));
        verify(pathRepository, times(1)).delete(path);
    }

    @Test
    void testDeletePath_NotFound_ThrowsException() {
        when(pathRepository.findBySalePointA_IdAndSalePointB_Id(1L, 2L)).thenReturn(Optional.empty());
        when(pathRepository.findBySalePointA_IdAndSalePointB_Id(2L, 1L)).thenReturn(Optional.empty());

        assertThrows(PathNotFoundException.class, () -> pathService.deletePath(1L, 2L));
    }

    @Test
    void testFindCheapestPath_SinglePath() {
        Path testPath = new Path(a, b, 5.0);

        when(salePointService.getSalePointById(1L)).thenReturn(a);
        when(salePointService.getSalePointById(2L)).thenReturn(b);
        when(pathRepository.findBySalePointA_Id(1L)).thenReturn(List.of(testPath));
        when(pathRepository.findBySalePointB_Id(1L)).thenReturn(Collections.emptyList());

        PathCost result = pathService.findCheapestPath(1L, 2L);
        assertEquals(5.0, result.getTotalCost());
        assertEquals(2, result.getPath().size());
        assertEquals(1L, result.getPath().get(0).getId());
        assertEquals(2L, result.getPath().get(1).getId());
    }

    @Test
    void testFindCheapestPath_MultiplePaths() {
        SalePoint c = new SalePoint("C");
        ReflectionTestUtils.setField(c, "id", 3L);
        Path ab = new Path(a, b, 2.0);
        Path bc = new Path(b, c, 2.0);
        Path ac = new Path(a, c, 10.0);

        when(salePointService.getSalePointById(1L)).thenReturn(a);
        when(salePointService.getSalePointById(3L)).thenReturn(c);
        when(pathRepository.findBySalePointA_Id(1L)).thenReturn(List.of(ab, ac));
        when(pathRepository.findBySalePointB_Id(1L)).thenReturn(Collections.emptyList());
        when(pathRepository.findBySalePointA_Id(2L)).thenReturn(List.of(bc));
        when(pathRepository.findBySalePointB_Id(2L)).thenReturn(List.of(ab));

        PathCost result = pathService.findCheapestPath(1L, 3L);
        assertEquals(4.0, result.getTotalCost());
        List<Long> ids = result.getPath().stream().map(SalePointCost::getId).toList();
        assertEquals(List.of(1L, 2L, 3L), ids);
    }

    @Test
    void testFindCheapestPath_SalePointNotFound_ThrowsException() {
        when(salePointService.getSalePointById(1L)).thenReturn(a);
        when(salePointService.getSalePointById(2L)).thenThrow(SalePointNotFoundException.class);

        assertThrows(SalePointNotFoundException.class, () -> pathService.findCheapestPath(1L, 2L));
    }

    @Test
    void testFindCheapestPath_PathNotFound_ThrowsException() {
        when(salePointService.getSalePointById(1L)).thenReturn(a);
        when(salePointService.getSalePointById(2L)).thenReturn(b);
        when(pathRepository.findBySalePointA_Id(1L)).thenReturn(Collections.emptyList());
        when(pathRepository.findBySalePointB_Id(1L)).thenReturn(Collections.emptyList());

        assertThrows(PathNotFoundException.class, () -> pathService.findCheapestPath(1L, 2L));
    }
}
