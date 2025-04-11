package com.sharks.sale_points_service.services.impl;

import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.sharks.sale_points_service.exceptions.PathAlreadyExistsException;
import com.sharks.sale_points_service.exceptions.PathNotFoundException;
import com.sharks.sale_points_service.exceptions.SameSalePointException;
import com.sharks.sale_points_service.models.Path;
import com.sharks.sale_points_service.models.SalePoint;
import com.sharks.sale_points_service.models.dtos.PathDTO;
import com.sharks.sale_points_service.models.dtos.NewPath;
import com.sharks.sale_points_service.models.dtos.NewPathWithoutIds;
import com.sharks.sale_points_service.repositories.PathRepository;
import com.sharks.sale_points_service.services.PathService;
import com.sharks.sale_points_service.services.SalePointService;

@Service
public class PathServiceImpl implements PathService {

    private final PathRepository pathRepository;
    private final SalePointService salePointService;

    public PathServiceImpl(PathRepository pathRepository, SalePointService salePointService) {
        this.pathRepository = pathRepository;
        this.salePointService = salePointService;
    }

    @Override
    public List<PathDTO> getAllPathDTOs() {
        return pathRepository.findAll().stream().map(PathDTO::new).toList();
    }

    @Override
    public Path getPathByIds(Long idA, Long idB) {
        return pathRepository.findBySalePointA_IdAndSalePointB_Id(idA, idB)
                .or(() -> pathRepository.findBySalePointA_IdAndSalePointB_Id(idB, idA))
                .orElseThrow(() -> new PathNotFoundException(idA, idB));
    }

    @Override
    public List<Path> getPathsById(Long id) {
        HashSet<Path> paths = new HashSet<>();
        paths.addAll(pathRepository.findBySalePointA_Id(id));
        paths.addAll(pathRepository.findBySalePointB_Id(id));
        return paths.stream().toList();
    }

    @Override
    public PathDTO getPathDTOByIds(Long idA, Long idB) {
        return new PathDTO(getPathByIds(idA, idB));
    }

    @Override
    public List<PathDTO> getPathDTOsById(Long id) {
        return getPathsById(id).stream().map(PathDTO::new).toList();
    }

    @Override
    public PathDTO createPath(NewPath newPath) {
        validatePath(newPath);
        SalePoint salePointA = salePointService.getSalePointById(newPath.idA());
        SalePoint salePointB = salePointService.getSalePointById(newPath.idB());
        Path path = new Path(salePointA, salePointB, newPath.cost());
        Path savedPath = pathRepository.save(path);
        return new PathDTO(savedPath);
    }

    @Override
    public PathDTO updatePath(Long idA, Long idB, NewPathWithoutIds newPath) {
        Path existingPath = getPathByIds(idA, idB);
        existingPath.setCost(newPath.cost());
        Path updatedPath = pathRepository.save(existingPath);
        return new PathDTO(updatedPath);
    }

    @Override
    public void deletePath(Long idA, Long idB) {
        Path existingPath = getPathByIds(idA, idB);
        pathRepository.delete(existingPath);
    }

    private void validatePath(NewPath newPath) {
        if (newPath.idA().equals(newPath.idB()))
            throw new SameSalePointException(newPath.idA());
        if (pathRepository.existsBySalePointA_IdAndSalePointB_Id(newPath.idA(), newPath.idB())
                || pathRepository.existsBySalePointA_IdAndSalePointB_Id(newPath.idB(), newPath.idA()))
            throw new PathAlreadyExistsException(newPath.idA(), newPath.idB());
    }
}
