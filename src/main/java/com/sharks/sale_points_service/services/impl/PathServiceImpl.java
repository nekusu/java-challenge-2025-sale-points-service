package com.sharks.sale_points_service.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.sharks.sale_points_service.exceptions.PathAlreadyExistsException;
import com.sharks.sale_points_service.exceptions.PathNotFoundException;
import com.sharks.sale_points_service.exceptions.SameSalePointException;
import com.sharks.sale_points_service.models.Path;
import com.sharks.sale_points_service.models.PathCost;
import com.sharks.sale_points_service.models.SalePoint;
import com.sharks.sale_points_service.models.SalePointCost;
import com.sharks.sale_points_service.models.dtos.PathDTO;
import com.sharks.sale_points_service.models.dtos.NewPath;
import com.sharks.sale_points_service.models.dtos.NewPathWithoutIds;
import com.sharks.sale_points_service.repositories.PathRepository;
import com.sharks.sale_points_service.services.PathService;
import com.sharks.sale_points_service.services.SalePointService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
    @Cacheable(value = "paths", key = "#idA + '-' + #idB")
    public PathDTO getPathDTOByIds(Long idA, Long idB) {
        return new PathDTO(getPathByIds(idA, idB));
    }

    @Override
    @Cacheable(value = "paths", key = "#id")
    public List<PathDTO> getPathDTOsById(Long id) {
        return getPathsById(id).stream().map(PathDTO::new).toList();
    }

    @Override
    @CachePut(value = "paths", key = "#newPath.idA + '-' + #newPath.idB")
    @CacheEvict(value = "pathCost", allEntries = true)
    public PathDTO createPath(NewPath newPath) {
        validatePath(newPath);
        SalePoint salePointA = salePointService.getSalePointById(newPath.idA());
        SalePoint salePointB = salePointService.getSalePointById(newPath.idB());
        Path path = new Path(salePointA, salePointB, newPath.cost());
        Path savedPath = pathRepository.save(path);
        return new PathDTO(savedPath);
    }

    @Override
    @CachePut(value = "paths", key = "#idA + '-' + #idB")
    @CacheEvict(value = "pathCost", allEntries = true)
    public PathDTO updatePath(Long idA, Long idB, NewPathWithoutIds newPath) {
        Path existingPath = getPathByIds(idA, idB);
        existingPath.setCost(newPath.cost());
        Path updatedPath = pathRepository.save(existingPath);
        return new PathDTO(updatedPath);
    }

    @Override
    @CachePut(value = "paths", key = "#idA + '-' + #idB")
    @CacheEvict(value = "pathCost", allEntries = true)
    public void deletePath(Long idA, Long idB) {
        Path existingPath = getPathByIds(idA, idB);
        pathRepository.delete(existingPath);
    }

    @Override
    @Cacheable(value = "pathCost", key = "#startId + '-' + #endId")
    public PathCost findCheapestPath(Long startId, Long endId) {
        PriorityQueue<SalePointCost> queue = new PriorityQueue<>();
        Map<Long, List<SalePointCost>> graph = new HashMap<>();
        Map<Long, String> salePointNames = new HashMap<>();
        Map<Long, Long> previous = new HashMap<>();
        Map<Long, Boolean> visited = new HashMap<>();
        Map<Long, Double> costs = new HashMap<>();
        SalePoint startSalePoint = salePointService.getSalePointById(startId);
        SalePoint endSalePoint = salePointService.getSalePointById(endId);
        salePointNames.put(startId, startSalePoint.getName());
        salePointNames.put(endId, endSalePoint.getName());
        queue.add(new SalePointCost(startId, 0.0));
        previous.put(startId, null);
        visited.put(startId, false);
        costs.put(startId, 0.0);

        while (!queue.isEmpty()) {
            SalePointCost current = queue.poll();
            Long currentId = current.getId();
            visited.put(currentId, true);
            log.info("Visiting sale point ID {}", currentId);

            if (currentId.equals(endId))
                return buildPathCost(currentId, previous, costs, salePointNames, endId);

            if (graph.get(currentId) == null)
                buildGraphForCurrent(graph, salePointNames, previous, visited, costs, currentId);

            processNeighbors(graph, visited, costs, previous, queue, current);
        }

        throw new PathNotFoundException(startId, endId);
    }

    private PathCost buildPathCost(Long currentId, Map<Long, Long> previous, Map<Long, Double> costs,
            Map<Long, String> salePointNames, Long endId) {
        List<SalePointCost> finalPath = new ArrayList<>();
        Long iterId = currentId;
        while (iterId != null) {
            Double salePointCost = costs.get(iterId) - costs.getOrDefault(previous.get(iterId), 0.0);
            finalPath.add(new SalePointCost(iterId, salePointNames.get(iterId), salePointCost));
            iterId = previous.get(iterId);
        }
        log.info("Final path found: {}", finalPath.reversed().stream().map(SalePointCost::getId).toList());
        log.info("Total cost: {}", costs.get(endId));
        return new PathCost(finalPath.reversed(), costs.get(endId));
    }

    private void buildGraphForCurrent(Map<Long, List<SalePointCost>> graph, Map<Long, String> salePointNames,
            Map<Long, Long> previous, Map<Long, Boolean> visited, Map<Long, Double> costs, Long currentId) {
        List<SalePointCost> salePointCosts = new ArrayList<>();
        List<Path> paths = getPathsById(currentId);
        for (Path path : paths) {
            SalePoint connectedSalePoint = path.getSalePointB();
            if (connectedSalePoint.getId().equals(currentId))
                connectedSalePoint = path.getSalePointA();
            Long salePointId = connectedSalePoint.getId();
            salePointNames.putIfAbsent(salePointId, connectedSalePoint.getName());
            previous.putIfAbsent(salePointId, null);
            visited.putIfAbsent(salePointId, false);
            costs.putIfAbsent(salePointId, Double.MAX_VALUE);
            salePointCosts.add(new SalePointCost(salePointId, path.getCost()));
        }
        graph.put(currentId, salePointCosts);
        log.info("Sale points connected to ID {}: {}", currentId,
                salePointCosts.stream().map(SalePointCost::getId).toList());
    }

    private void processNeighbors(Map<Long, List<SalePointCost>> graph, Map<Long, Boolean> visited,
            Map<Long, Double> costs, Map<Long, Long> previous, PriorityQueue<SalePointCost> queue,
            SalePointCost current) {
        Long currentId = current.getId();
        for (SalePointCost salePointCost : graph.get(currentId)) {
            if (Boolean.TRUE.equals(visited.get(salePointCost.getId())))
                continue;
            Double newCost = current.getCost() + salePointCost.getCost();
            if (newCost < costs.get(salePointCost.getId())) {
                previous.put(salePointCost.getId(), currentId);
                costs.put(salePointCost.getId(), newCost);
                queue.add(new SalePointCost(salePointCost.getId(), newCost));
                log.info("New cost for sale point ID {}: {}", salePointCost.getId(), newCost);
            }
        }
    }

    private void validatePath(NewPath newPath) {
        if (newPath.idA().equals(newPath.idB()))
            throw new SameSalePointException(newPath.idA());
        if (pathRepository.existsBySalePointA_IdAndSalePointB_Id(newPath.idA(), newPath.idB())
                || pathRepository.existsBySalePointA_IdAndSalePointB_Id(newPath.idB(), newPath.idA()))
            throw new PathAlreadyExistsException(newPath.idA(), newPath.idB());
    }
}
