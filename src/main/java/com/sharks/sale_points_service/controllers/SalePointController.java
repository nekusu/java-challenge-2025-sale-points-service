package com.sharks.sale_points_service.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sharks.sale_points_service.models.dtos.NewSalePoint;
import com.sharks.sale_points_service.models.dtos.SalePointDTO;
import com.sharks.sale_points_service.services.SalePointService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
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

@Tag(name = "Sale Points", description = "Operations related to sale points management")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/sale-points")
public class SalePointController {

    private final SalePointService salePointService;

    public SalePointController(SalePointService salePointService) {
        this.salePointService = salePointService;
    }

    @Operation(summary = "Get all sale points", description = "Returns a list of all sale points available in the system.", responses = {
            @ApiResponse(responseCode = "200", description = "List of sale points retrieved successfully", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SalePointDTO.class))))
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SalePointDTO> getAllSalePoints() {
        return salePointService.getAllSalePointDTOs();
    }

    @Operation(summary = "Get sale point by ID", description = "Returns a sale point identified by its unique ID.", parameters = {
            @Parameter(name = "id", description = "ID of the sale point", required = true, example = "1")
    }, responses = {
            @ApiResponse(responseCode = "200", description = "Sale point retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SalePointDTO.class))),
            @ApiResponse(responseCode = "404", description = "Sale point not found")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SalePointDTO getSalePoint(@PathVariable Long id) {
        return salePointService.getSalePointDTOById(id);
    }

    @Operation(summary = "Create a new sale point", description = "Creates a new sale point with the provided details.", requestBody = @RequestBody(description = "Sale point data to create", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = NewSalePoint.class))), responses = {
            @ApiResponse(responseCode = "201", description = "Sale point created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SalePointDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Conflict")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SalePointDTO createSalePoint(
            @org.springframework.web.bind.annotation.RequestBody @Valid NewSalePoint newSalePoint) {
        return salePointService.createSalePoint(newSalePoint);
    }

    @Operation(summary = "Update an existing sale point", description = "Updates the details of an existing sale point identified by its ID.", parameters = {
            @Parameter(name = "id", description = "ID of the sale point to update", required = true, example = "1")
    }, requestBody = @RequestBody(description = "Updated sale point data", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = NewSalePoint.class))), responses = {
            @ApiResponse(responseCode = "200", description = "Sale point updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SalePointDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Sale point not found"),
            @ApiResponse(responseCode = "409", description = "Conflict")
    })
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SalePointDTO updateSalePoint(@PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestBody @Valid NewSalePoint newSalePoint) {
        return salePointService.updateSalePoint(id, newSalePoint);
    }

    @Operation(summary = "Delete a sale point", description = "Deletes a sale point identified by its ID.", parameters = {
            @Parameter(name = "id", description = "ID of the sale point to delete", required = true, example = "1")
    }, responses = {
            @ApiResponse(responseCode = "204", description = "Sale point deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Sale point not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSalePoint(@PathVariable Long id) {
        salePointService.deleteSalePoint(id);
    }
}
