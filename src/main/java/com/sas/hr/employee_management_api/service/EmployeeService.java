package com.sas.hr.employee_management_api.service;


import com.sas.hr.employee_management_api.dto.EmployeeDetailsDTO;
import com.sas.hr.employee_management_api.dto.EmployeeInputDTO;
import com.sas.hr.employee_management_api.mapper.EmployeeMapper;
import com.sas.hr.employee_management_api.model.Employee;
import com.sas.hr.employee_management_api.repository.EmployeeJpaRepository;
import com.sas.hr.employee_management_api.repository.EmployeeRepository;
import com.sas.hr.employee_management_api.util.CSVProcessor;
import com.sas.hr.employee_management_api.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final CSVProcessor csvProcessor;
    private final EmployeeJpaRepository employeeJpaRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, CSVProcessor csvProcessor, EmployeeJpaRepository employeeJpaRepository) {
        this.employeeRepository = employeeRepository;
        this.csvProcessor = csvProcessor;
        this.employeeJpaRepository = employeeJpaRepository;
    }

    // Process the CSV file passed with a filePath (either from resources or filesystem)
    public void processCsvFile(Resource resource) throws IOException {
        // Load employees from CSV using CsvLoader (from resources or filesystem)
        List<EmployeeInputDTO> employeeDTOList = csvProcessor.loadEmployeesFromCsv(resource);
        List<Employee> employeeList = EmployeeMapper.toEmployeeEntityList(employeeDTOList);
        // Persist employees to the database
        employeeRepository.batchInsertEmployeesUsingJdbc(employeeList);
    }

    // A method to handle file upload from a client (for multipart file)
    public void processUploadedCsv(MultipartFile file) throws IOException {

        // Generate a unique file name to avoid name collisions
        String tempFileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        // Define a temporary directory (use default temp directory)
        Path tempDir = Files.createTempDirectory("uploadedFiles");

        // Create the path for the temporary file
        Path tempFilePath = tempDir.resolve(tempFileName);

        // Save the MultipartFile to the temporary file
        Files.copy(file.getInputStream(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);

        Resource resource = new FileSystemResource(tempFilePath.toFile());

        // Process the file using CsvLoader
        processCsvFile(resource);
    }

    // A method to handle processing of CSV from the resources folder (data folder)
    public void saveEmployeesFromResources() throws IOException {
        // Path for resource files in classpath
        Resource resource = new ClassPathResource("static/data/ProgrammingChallengeData.csv"); // adjust based on your structure

        processCsvFile(resource);
    }



    //Get All employees
    public List<EmployeeDetailsDTO> getAllEmployees(){
        List<Employee> employeeList = employeeJpaRepository.findAll();
        return EmployeeMapper.toEmployeeDTOList(employeeList);
    }

    // Get all employees, optionally filtered by birthday month
    public List<EmployeeDetailsDTO> getAllEmployeesByMonth(int month) {
        List<Employee> employeeList =  employeeRepository.findEmployeesByBirthdayMonthUsingNamedParamJdbcTemplate(month);
        return EmployeeMapper.toEmployeeDTOList(employeeList);
    }

    // Delete an employee
    public void deleteEmployeeById(Long id) {
        Employee employee = employeeJpaRepository.findById(id).orElseThrow();
        employeeJpaRepository.delete(employee);
    }


    // Update an employee
    public EmployeeDetailsDTO updateEmployee(Long id, EmployeeInputDTO employeeInputDTO) {

        Employee employee = employeeJpaRepository.findById(id).orElseThrow();
        if(null!=employee){
            employee.setLocation(employeeInputDTO.location());
            employee.setBirthDay(DateUtil.convertDateStringToFormattedLocalDate(employeeInputDTO.birthDate()));
            employee.setFirstName(employeeInputDTO.firstName());
            employee.setLastName(employeeInputDTO.lastName());
            Employee resultEmployee = employeeJpaRepository.save(employee);
            return EmployeeMapper.toEmployeeDTO(resultEmployee);
        }
        return null;

    }

    // Create an employee
    public EmployeeDetailsDTO createEmployee(EmployeeInputDTO employeeInputDTO) {
        Employee employee = EmployeeMapper.toEmployeeEntity(employeeInputDTO);
        Employee resultEmployee = employeeJpaRepository.save(employee);
        if(null!=resultEmployee)
            return EmployeeMapper.toEmployeeDTO(resultEmployee);
        else
            return null;
    }

    public EmployeeDetailsDTO getEmployeeById(Long id) {
        Optional<Employee> employee = employeeJpaRepository.findById(id);
        return employee.map(EmployeeMapper::toEmployeeDTO).orElse(null);
    }
}
