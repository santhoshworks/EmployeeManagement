package com.sas.hr.employee_management_api.service;

import com.sas.hr.employee_management_api.dto.EmployeeDetailsDTO;
import com.sas.hr.employee_management_api.dto.EmployeeInputDTO;
import com.sas.hr.employee_management_api.model.Employee;
import com.sas.hr.employee_management_api.repository.EmployeeJpaRepository;
import com.sas.hr.employee_management_api.repository.EmployeeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeJpaRepository employeeJpaRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void testGetEmployeeByIdSuccess() {

        //Arrange
        Employee emp1 = new Employee(1L, "John", "Peter", "New York","NY","New York, NY", LocalDate.of(1985, 5, 25));
        Employee emp2 = new Employee(2L, "Pal", "Smith","Los Angeles", "CA","Los Angeles, CA", LocalDate.of(1991, 5, 12));
        when(employeeJpaRepository.findById(1L)).thenReturn(Optional.of(emp1));

        //Act
        EmployeeDetailsDTO employeeDetailsDTO = employeeService.getEmployeeById(1L);

        //Assert
        Assertions.assertNotNull(employeeDetailsDTO);
        Assertions.assertEquals("John",employeeDetailsDTO.firstName());
        Assertions.assertEquals("Peter",employeeDetailsDTO.lastName());
    }

    @Test
    void testGetEmployeeByIdNotFound() {

        //Arrange
        Employee emp1 = new Employee(1L, "John", "Peter", "New York","NY","New York, NY", LocalDate.of(1985, 5, 25));
        Employee emp2 = new Employee(2L, "Pal", "Smith","Los Angeles", "CA","Los Angeles, CA", LocalDate.of(1991, 5, 12));
        when(employeeJpaRepository.findById(1L)).thenReturn(Optional.empty());

        //Act
        EmployeeDetailsDTO employeeDetailsDTO = employeeService.getEmployeeById(1L);

        //Assert
        Assertions.assertNull(employeeDetailsDTO);
    }



    @Test
    void testGetAllEmployeesByMonthUsingNamedJDBC(){
        //Arrange
        Employee emp2 = new Employee(2L, "Pal", "Smith","Los Angeles", "CA","Los Angeles, CA", LocalDate.of(1991, 5, 12));        when(employeeRepository.findEmployeesByBirthdayMonthUsingNamedParamJdbcTemplate(1)).thenReturn(List.of(emp2));

        //Act
        List<EmployeeDetailsDTO> result = employeeService.getAllEmployeesByMonth(1);

        //Assert
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result).extracting(EmployeeDetailsDTO::firstName).contains("Pal");
    }

    @Test
    void testGetAllEmployees(){
        //Arrange
        Employee emp1 = new Employee(1L, "John", "Peter", "New York","NY","New York, NY", LocalDate.of(1985, 5, 25));
        Employee emp2 = new Employee(2L, "Pal", "Smith","Los Angeles", "CA","Los Angeles, CA", LocalDate.of(1991, 5, 12));
        when(employeeJpaRepository.findAll()).thenReturn(List.of(emp1,emp2));

        //Act
        List<EmployeeDetailsDTO> result = employeeService.getAllEmployees();

        //Assert
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
        assertThat(result).extracting(EmployeeDetailsDTO::firstName).contains("John");
    }

    @Test
    void testDeleteEmployeeById() {
        //Arrange
        Employee emp1 = new Employee(1L, "John", "Peter", "New York","NY","New York, NY", LocalDate.of(1985, 5, 25));
       when(employeeJpaRepository.findById(1L)).thenReturn(Optional.of(emp1));
        doNothing().when(employeeJpaRepository).delete(emp1);

       //Act
       employeeService.deleteEmployeeById(1L);

       //Assert
        verify(employeeJpaRepository, times(1)).delete(emp1);
    }

    @Test
    void testDeleteEmployeeByIdNotFound() {
        //Arrange
        Employee emp1 = new Employee(1L, "John", "Peter", "New York","NY","New York, NY", LocalDate.of(1985, 5, 25));
                when(employeeJpaRepository.findById(1L)).thenReturn(Optional.empty());

        //Act
        assertThatThrownBy(() -> employeeService.deleteEmployeeById(1L))
                .isInstanceOf(NoSuchElementException.class);
//                .hasMessage("Employee with ID " + 1L + " not found");

        // Verify that the deleteById method was never called
        verify(employeeJpaRepository, never()).delete(emp1);

        //Assert
        // Assert that calling get() on an empty Optional throws NoSuchElementException
        assertThatThrownBy(() -> Optional.empty().get())
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("No value present");
    }

    @Test
    void testCreateEmployee() {
        //ARRANGE
        Employee emp1 = new Employee(1L, "John", "Peter", "New York","NY","New York, NY", LocalDate.of(1985, 5, 25));
        when(employeeJpaRepository.save(Mockito.any(Employee.class))).thenReturn(emp1);


        EmployeeInputDTO employeeInputDTO = new EmployeeInputDTO("John", "Peter", "New York","NY,","New York, NY","10/5/2020");
        //act
        EmployeeDetailsDTO createdEmployee = employeeService.createEmployee(employeeInputDTO);

        //ASSERT
        assertThat(createdEmployee.firstName()).isEqualTo(employeeInputDTO.firstName());
    }

    @Test
    void testUpdateEmployee() {
       //Arrange
        Employee emp1 = new Employee(1L, "John", "Peter", "New York","NY","New York, NY", LocalDate.of(1985, 5, 25));
        Mockito.when(employeeJpaRepository.findById(1L)).thenReturn(Optional.of(emp1));
        Mockito.when(employeeJpaRepository.save(Mockito.any(Employee.class))).thenReturn(emp1);

        //Act
        EmployeeInputDTO updatedEmployee = new EmployeeInputDTO("Pal", "Smith", "Los Angeles","CA","Los Angeles,CA","10/5/2020");
        EmployeeDetailsDTO result = employeeService.updateEmployee(1L, updatedEmployee);

        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("Pal");
    }
}
