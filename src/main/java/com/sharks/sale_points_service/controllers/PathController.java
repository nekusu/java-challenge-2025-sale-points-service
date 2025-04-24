package com.sharks.sale_points_service.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sharks.sale_points_service.models.dtos.PathDTO;
import com.sharks.sale_points_service.models.PathCost;
import com.sharks.sale_points_service.models.dtos.NewPath;
import com.sharks.sale_points_service.models.dtos.NewPathWithoutIds;
import com.sharks.sale_points_service.services.PathService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Paths", description = "Operations related to path management between sale points")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/paths")
public class PathController {

    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @Operation(summary = "Get all paths", description = "Returns a list of all paths available in the system.", responses = {
            @ApiResponse(responseCode = "200", description = "List of paths retrieved successfully", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PathDTO.class))))
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PathDTO> getAllPaths() {
        return pathService.getAllPathDTOs();
    }

    @Operation(summary = "Get paths by sale point ID", description = "Returns a list of paths associated with the given sale point ID.", parameters = {
            @Parameter(name = "id", description = "ID of the sale point", required = true, example = "1")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "Paths retrieved successfully", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = PathDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Sale point not found")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<PathDTO> getPathsById(@PathVariable Long id) {
        return pathService.getPathDTOsById(id);
    }

    @Operation(summary = "Get path by two sale point IDs", description = "Returns the path between two sale points identified by their IDs.", parameters = {
            @Parameter(name = "idA", description = "ID of the first sale point", required = true, example = "1"),
            @Parameter(name = "idB", description = "ID of the second sale point", required = true, example = "2")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "Path retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PathDTO.class))),
            @ApiResponse(responseCode = "404", description = "Path not found")
    })
    @GetMapping("/{idA}/{idB}")
    @ResponseStatus(HttpStatus.OK)
    public PathDTO getPathByIds(@PathVariable Long idA, @PathVariable Long idB) {
        return pathService.getPathDTOByIds(idA, idB);
    }

    @Operation(summary = "Get cost of the cheapest path between two sale points", description = "Returns the cost of the cheapest path between two sale points identified by their IDs.", parameters = {
            @Parameter(name = "idA", description = "ID of the first sale point", required = true, example = "1"),
            @Parameter(name = "idB", description = "ID of the second sale point", required = true, example = "2")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "Path cost retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PathCost.class))),
            @ApiResponse(responseCode = "404", description = "Path not found")
    })
    @GetMapping("/{idA}/{idB}/cost")
    @ResponseStatus(HttpStatus.OK)
    public PathCost getPathCost(@PathVariable Long idA, @PathVariable Long idB) {
        return pathService.findCheapestPath(idA, idB);
    }

    @Operation(summary = "Create a new path", description = "Creates a new path between sale points with the provided details.", requestBody = @RequestBody(description = "Path data to create", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = NewPath.class))), responses = {
            @ApiResponse(responseCode = "201", description = "Path created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PathDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Conflict")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PathDTO createPath(@org.springframework.web.bind.annotation.RequestBody @Valid NewPath newPath) {
        return pathService.createPath(newPath);
    }

    @Operation(summary = "Update an existing path", description = "Updates the details of an existing path between two sale points identified by their IDs.", parameters = {
            @Parameter(name = "idA", description = "ID of the first sale point", required = true, example = "1"),
            @Parameter(name = "idB", description = "ID of the second sale point", required = true, example = "2")
    }, requestBody = @RequestBody(description = "Updated path data", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = NewPathWithoutIds.class))), responses = {
            @ApiResponse(responseCode = "200", description = "Path updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PathDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Path not found")
    })
    @PutMapping("/{idA}/{idB}")
    @ResponseStatus(HttpStatus.OK)
    public PathDTO updatePath(@PathVariable Long idA, @PathVariable Long idB,
            @org.springframework.web.bind.annotation.RequestBody @Valid NewPathWithoutIds newPath) {
        return pathService.updatePath(idA, idB, newPath);
    }

    @Operation(summary = "Delete a path", description = "Deletes a path between two sale points identified by their IDs.", parameters = {
            @Parameter(name = "idA", description = "ID of the first sale point", required = true, example = "1"),
            @Parameter(name = "idB", description = "ID of the second sale point", required = true, example = "2")
    }, responses = {
            @ApiResponse(responseCode = "204", description = "Path deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Path not found")
    })
    @DeleteMapping("/{idA}/{idB}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePath(@PathVariable Long idA, @PathVariable Long idB) {
        pathService.deletePath(idA, idB);
    }
}
