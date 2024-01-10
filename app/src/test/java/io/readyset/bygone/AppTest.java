
package io.readyset.bygone;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;
import java.util.List;

class AppTest {

    private Employee getEmployee() {
        return Employee.newInstance("jdbc:postgresql://127.0.0.1:5433/hoodoo", "postgres", "hoodoo");
    }

    @Test
    void verifyEmployeeCreate() {
        Employee employee = getEmployee();
        assertNotNull(employee, "employee instance is null");

        employee.setFirstName("Andy");
        employee.setLastName("Glover");
        employee.setGender("M");
        employee.setHireDate(new Date());

        assertDoesNotThrow(() -> {
            employee.save();
        });
    }

    @Test
    void findOnlyManagers() {
        Employee employee = getEmployee();
        assertNotNull(employee, "employee instance is null");

        assertDoesNotThrow(() -> {
            List<Employee> managers = employee.findManagers();
            assertNotNull(managers, "managers is null");
            assertEquals(24, managers.size(), "managers size was not 24");
        });
    }

    @Test
    void employeeListIs10Large() {
        Employee employee = getEmployee();
        assertNotNull(employee);

        assertDoesNotThrow(() -> {
            List<Employee> employees = employee.findEmployees(10);
            assertNotNull(employees, "Employees object was null");
            assertEquals(10, employees.size(), "the list of employees was not equal to 10");
        });
    }

    @Test
    void employeeListIsOnly1Large() {
        Employee employee = getEmployee();
        assertNotNull(employee);

        assertDoesNotThrow(() -> {
            List<Employee> employees = employee.findEmployees(1);
            assertNotNull(employees, "Employees object was null");
            assertEquals(1, employees.size(), "the list of employees was not equal to 1");
        });
    }

    @Test
    void employeeListIsOnly100Large() {
        Employee employee = getEmployee();
        assertNotNull(employee);

        assertDoesNotThrow(() -> {
            List<Employee> employees = employee.findEmployees(100);
            assertNotNull(employees, "Employees object was null");
            assertEquals(100, employees.size(), "the list of employees was not equal to 100");
        });
    }

}
