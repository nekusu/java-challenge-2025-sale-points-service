package com.sharks.sale_points_service.services;

import java.util.List;

import com.sharks.sale_points_service.models.Path;
import com.sharks.sale_points_service.models.PathCost;
import com.sharks.sale_points_service.models.dtos.PathDTO;
import com.sharks.sale_points_service.models.dtos.NewPath;
import com.sharks.sale_points_service.models.dtos.NewPathWithoutIds;

public interface PathService {

    List<PathDTO> getAllPathDTOs();

    Path getPathByIds(Long idA, Long idB);

    List<Path> getPathsById(Long id);

    PathDTO getPathDTOByIds(Long idA, Long idB);

    List<PathDTO> getPathDTOsById(Long id);

    PathDTO createPath(NewPath newPath);

    PathDTO updatePath(Long idA, Long idB, NewPathWithoutIds newPath);

    void deletePath(Long idA, Long idB);

    PathCost findCheapestPath(Long startId, Long endId);
}
