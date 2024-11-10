package com.sas.hr.employee_management_api.controller;


import com.sas.hr.employee_management_api.dto.EmployeeDetailsDTO;
import com.sas.hr.employee_management_api.dto.EmployeeInputDTO;
import com.sas.hr.employee_management_api.service.EmployeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/upload/csvFromResourcesFolder")
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadCsvFileFromResources(){
        try {
            employeeService.saveEmployeesFromResources();
            return "CSV file uploaded and processed successfully.";
        } catch (IOException e) {
            log.error("Error occured in file upload:: {}", e.getMessage());
            return "Error uploading file.";
        }
    }

    @PostMapping("/upload/csvFromFileSystem")
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadCsvFromFileSystem(@RequestParam("file") MultipartFile file) {
        try {
            employeeService.processUploadedCsv(file);
            return "CSV file processed and data saved successfully.";
        } catch (IOException e) {
            log.error("Error occured in csv file processing :: {}", e.getMessage());
            return "Error processing CSV file: " + e.getMessage();
        }
    }

    // Get all employees, filter by birthday month (optional)
    @GetMapping
    public ResponseEntity<List<EmployeeDetailsDTO>> getAllEmployees(@RequestParam(value = "month", required = false) @Min(1) @Max(12) Integer month) {
        List<EmployeeDetailsDTO> employees;
        if (month != null) {
            employees = employeeService.getAllEmployeesByMonth(month);
        } else {
            employees = employeeService.getAllEmployees();
        }
        return ResponseEntity.ok(employees);
    }

    // Create a new employee
    @PostMapping
    public ResponseEntity<EmployeeDetailsDTO> createEmployee(@RequestBody @Valid EmployeeInputDTO employeeInputDTO) {
       EmployeeDetailsDTO employeeDetailsDTO = employeeService.createEmployee(employeeInputDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeDetailsDTO);
    }

    // Update an employee
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDetailsDTO> updateEmployee(@PathVariable Long id, @RequestBody @Valid EmployeeInputDTO employeeInputDTO) {
        EmployeeDetailsDTO employeeDetailsDTO =  employeeService.updateEmployee(id, employeeInputDTO);
        return employeeDetailsDTO != null ? ResponseEntity.ok(employeeDetailsDTO) : ResponseEntity.notFound().build();
    }

    // Retrieve an employee
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDetailsDTO> getEmployeeById(@PathVariable Long id) {
        EmployeeDetailsDTO employeeDetailsDTO = employeeService.getEmployeeById(id);
        return employeeDetailsDTO != null ? ResponseEntity.ok(employeeDetailsDTO) : ResponseEntity.notFound().build();
    }

    // Delete an employee
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
             employeeService.deleteEmployeeById(id);
             return ResponseEntity.noContent().build();
    }

}