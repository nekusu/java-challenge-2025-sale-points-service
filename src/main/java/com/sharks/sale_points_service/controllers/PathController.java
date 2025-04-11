package com.sharks.sale_points_service.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sharks.sale_points_service.models.dtos.PathDTO;
import com.sharks.sale_points_service.models.dtos.NewPath;
import com.sharks.sale_points_service.models.dtos.NewPathWithoutIds;
import com.sharks.sale_points_service.services.PathService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/paths")
public class PathController {

    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PathDTO> getAllPaths() {
        return pathService.getAllPathDTOs();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<PathDTO> getPathsById(@PathVariable Long id) {
        return pathService.getPathDTOsById(id);
    }

    @GetMapping("/{idA}/{idB}")
    @ResponseStatus(HttpStatus.OK)
    public PathDTO getPathByIds(@PathVariable Long idA, @PathVariable Long idB) {
        return pathService.getPathDTOByIds(idA, idB);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PathDTO createPath(@RequestBody @Valid NewPath newPath) {
        return pathService.createPath(newPath);
    }

    @PutMapping("/{idA}/{idB}")
    @ResponseStatus(HttpStatus.OK)
    public PathDTO updatePath(@PathVariable Long idA, @PathVariable Long idB,
            @RequestBody @Valid NewPathWithoutIds newPath) {
        return pathService.updatePath(idA, idB, newPath);
    }

    @DeleteMapping("/{idA}/{idB}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePath(@PathVariable Long idA, @PathVariable Long idB) {
        pathService.deletePath(idA, idB);
    }
}
