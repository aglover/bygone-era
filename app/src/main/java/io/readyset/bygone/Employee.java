package io.readyset.bygone;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Employee {
    private int id;
    private String firstName;
    private String lastName;
    private String gender;
    private Date hireDate;
    // database connection info
    private String url;
    private String user;
    private String password;

    public void setFirstName(String fname) {
        this.firstName = fname;
    }

    public void setLastName(String lName) {
        this.lastName = lName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public void save() throws EmployeeCRUDException {
        String sql = "insert into employees.employee (first_name, last_name, gender, hire_date, birth_date) " +
                "values (?, ?, ?::employees.employee_gender, ?, ?)";
        try (Connection conn = this.getDBConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, this.firstName);
            preparedStatement.setString(2, this.lastName);
            preparedStatement.setString(3, this.gender);
            preparedStatement.setTimestamp(4, new Timestamp(this.hireDate.getTime()));
            // this column can't be null but I am not interested in it for now
            preparedStatement.setTimestamp(5, new Timestamp(new Date().getTime()));
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            throw new EmployeeCRUDException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new EmployeeCRUDException(e.getMessage());
        }
    }

    /*
     * Preferable means to create this object - ensures Employee can do
     * something interesting rather than just be a container for properties.
     */
    public static Employee newInstance(String url, String user, String password) {
        return new Employee(url, user, password);
    }

    /*
     * Returns a list of fully initialized Employee objects who have the title
     * of Manager.
     */
    public List<Employee> findManagers() throws EmployeeCRUDException {
        String sql = "select e.id, e.first_name, e.last_name, e.gender, e.hire_date, " +
                "t.title, d.dept_name from employees.employee e " +
                "join employees.department_manager dm on e.id = dm.employee_id " +
                "join employees.department d on dm.department_id = d.id " +
                "join employees.title t on e.id = t.employee_id " +
                "where t.title = ? ";

        List<Employee> managers = new ArrayList<Employee>();
        try (Connection conn = this.getDBConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, "Manager");
            ResultSet resultSet = preparedStatement.executeQuery();
            buildList(managers, resultSet);
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            throw new EmployeeCRUDException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new EmployeeCRUDException(e.getMessage());
        }
        return managers;
    }

    private void buildList(List<Employee> list, ResultSet resultSet) throws SQLException {
        while (resultSet.next()) {
            list.add(
                    new Employee(resultSet.getInt("id"),
                            resultSet.getString("first_name"),
                            resultSet.getString("last_name"),
                            resultSet.getString("gender"),
                            resultSet.getTime("hire_date"),
                            this.url,
                            this.user,
                            this.password));
        }
    }

    /*
     * Returns a list of fully initialized Employee objects. You can
     * pass a limit -- eventually this could support pagination, for instance.
     */
    public List<Employee> findEmployees(int limit) throws EmployeeCRUDException {

        String sql = "select id, first_name, last_name, gender, hire_date " +
                "from employees.employee limit ?";

        List<Employee> employees = new ArrayList<Employee>();
        try (Connection conn = this.getDBConnection();
                PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, limit);
            ResultSet resultSet = preparedStatement.executeQuery();
            buildList(employees, resultSet);
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            throw new EmployeeCRUDException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new EmployeeCRUDException(e.getMessage());
        }
        return employees;
    }

    private Employee(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private Employee(int id, String fName, String lName, String gender, Date hirDate,
            String url, String username, String password) {
        this.id = id;
        this.firstName = fName;
        this.lastName = lName;
        this.gender = gender;
        this.hireDate = hirDate;

        this.url = url;
        this.user = username;
        this.password = password;
    }

    private Connection getDBConnection() throws SQLException {
        return DriverManager.getConnection(this.url, this.user, this.password);
    }

    public class EmployeeCRUDException extends Exception {
        public EmployeeCRUDException(String message) {
            super(message);
        }

        public EmployeeCRUDException(Throwable root) {
            super(root);
        }
    }
}
