package com.api.mobigenz_be.controllers.admin;


import com.api.mobigenz_be.DTOs.*;
import com.api.mobigenz_be.constants.UrlConstant;
import com.api.mobigenz_be.entities.*;
import com.api.mobigenz_be.repositories.EmployeeRepository;
import com.api.mobigenz_be.services.AccountService;
import com.api.mobigenz_be.services.EmployeeService;
import com.api.mobigenz_be.services.EmployeeServiceImp;
import com.api.mobigenz_be.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@CrossOrigin(UrlConstant.baseUrlFE)
@RequestMapping("api/admin")
public class EmployeeController {

    @Autowired
    private EmployeeServiceImp employeeService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;



    @GetMapping("employee/getAll")
    public ResponseEntity<ResponseDTO> getPageEmployee(
            @RequestParam(value = "offset", defaultValue = "") int offset,
            @RequestParam(value = "limit", defaultValue = "") int limit
    ) {
        PageDTO<EmployeeDto> items = this.employeeService.getAll(offset, limit);
        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .status(OK)
                        .data(Map.of("employee", items))
                        .statusCode(OK.value())
                        .timeStamp(LocalDateTime.now())
                        .build()
        );
    }

    @PutMapping("employee/findByKey")
    public ResponseEntity<ResponseDTO> findByKey(
            @RequestParam(value = "offset", defaultValue = "")  int offset
            ,@RequestParam(value = "limit",defaultValue = "") int limit
            ,@RequestBody SearchDTO searchDTO) {
        try {
            offset = offset < 0 ? 0 : offset;
            Pageable pageable;

            List<Sort.Order> orders = new ArrayList<>();
            List<ListSortDTO> listSortDTO = searchDTO.getListSortDTO();
            pageable = PageRequest.of(offset, limit, Sort.by("id"));
            Page<Employee> pageEmployee = this.employeeService.findByKey(pageable, searchDTO.getValueSearch());
            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(OK)
                            .data(Map.of("employee", pageEmployee))
                            .statusCode(OK.value())
                            .timeStamp(LocalDateTime.now())
                            .build()
            );
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("employee")
    public ResponseEntity<ResponseObject> insertEmployee(@RequestBody Employee employee){
        try {
            Employee emp = this.employeeRepository.findEmployeeByEmailorPhone(employee.getEmail(), employee.getPhoneNumber());
            if (emp != null) {
                if (emp.getPhoneNumber().equalsIgnoreCase(employee.getPhoneNumber())) {
                    System.out.println("Da co sdt");
                    return ResponseEntity.status(BAD_REQUEST).body(
                            new ResponseObject("false", "Số điện thoại đã tồn tại!", emp.getPhoneNumber())
                    );
                }
                if (emp.getEmail().equalsIgnoreCase(employee.getEmail())) {
                    System.out.println("Da co email");
                    return ResponseEntity.status(BAD_REQUEST).body(
                            new ResponseObject("false", "Email đã tồn tại!", emp.getEmail())
                    );
                }
                return ResponseEntity.status(BAD_REQUEST).body(
                        new ResponseObject("false", "Thêm tài khoản thất bại!", "")
                );

            } else {
                String password = passwordEncoder.encode("Employee@123");
                Set<Role> roles = new HashSet<>();
                Role role = this.roleService.getRoleById(3);
                roles.add(role);
                Account account = new Account();
                account.setEmail(employee.getEmail());
                account.setPhoneNumber(employee.getPhoneNumber());
                account.setPassword(password);
                account.setRoles(roles);
                account.setCtime(LocalDateTime.now());
                account.setStatus(1);
                AccountDTO accountDTO = this.accountService.add(account);
                employee.setAccount(account);
                EmployeeDto employeeDto =  this.employeeService.create(employee);
                return ResponseEntity.status(OK).body(
                        new ResponseObject("true", "Thêm nhân viên thành công!", employeeDto)
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("employee/{id}")
    public ResponseEntity<ResponseDTO> getEmployeeById(@PathVariable("id") Integer id) {
        try {
            EmployeeDto employeeDto = this.employeeService.getById(id);
            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(OK)
                            .data(Map.of("employee", employeeDto))
                            .statusCode(OK.value())
                            .timeStamp(LocalDateTime.now())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.ok(
                    ResponseDTO.builder()
                            .status(NO_CONTENT)
                            .data(Map.of("message", "Employee not exist"))
                            .statusCode(NO_CONTENT.value())
                            .timeStamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    @PutMapping("employee")
    public ResponseEntity<ResponseObject> updateEmployee(@RequestBody Employee employee) {
        try {
            Optional<Employee> cus = this.employeeRepository.findById(employee.getId());
            if (cus.isPresent()) {
                Account account = cus.get().getAccount();
                if(account != null){
                    account.setCtime(LocalDateTime.now());
                    account.setPhoneNumber(employee.getPhoneNumber());
                    this.accountService.add(account);
                    employee.setBirthday(employee.getBirthday());
                    employee.setAccount(account);
                }
                this.employeeService.update(employee);
                List<EmployeeDto> employeeDtoList = this.employeeService.getAllEmployee();
                return ResponseEntity.status(OK).body(
                        new ResponseObject("true", "Cập nhật thông tin nhân viên thành công!", employeeDtoList)
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(BAD_REQUEST).body(
                new ResponseObject("false", "Cập nhật thất bại!", "")
        );
    }

    @DeleteMapping("employee/{id}")
    public ResponseEntity<ResponseObject> deleteEmployee(@PathVariable(value = "id") Integer id) {
        Employee employee = this.employeeRepository.findEmployeeById(id);
        if (employee != null) {
            this.employeeService.delete(employee);
            List<EmployeeDto> employeeDtoList = this.employeeService.getAllEmployee();
            return ResponseEntity.status(OK).body(
                    new ResponseObject("true", "Xóa nhân viên thành công!", employeeDtoList)
            );

        }
        return ResponseEntity.status(OK).body(
                new ResponseObject("true", "Xóa thất bại!", "")
        );
    }

    @GetMapping("searchEmployee")
    public ResponseEntity<ResponseDTO> searchByAll(@RequestParam("search") String search) {
        List<EmployeeDto> employeeDtoList = this.employeeService.searchByAll(search);
        return ResponseEntity.ok(
                ResponseDTO.builder()
                        .status(OK)
                        .data(Map.of("employee", employeeDtoList))
                        .statusCode(OK.value())
                        .timeStamp(LocalDateTime.now())
                        .build()
        );
    }

//
//    @PutMapping("employee")
//    public ResponseEntity<ResponseDTO> updateEmployee(@RequestBody EmployeeDto employeeDto){
//        EmployeeDto employeeDto1 = this.employeeService.update(employeeDto);
//        return ResponseEntity.ok(
//                ResponseDTO
//                        .builder()
//                        .data(Map.of("employee", employeeDto1))
//                        .status(OK)
//                        .statusCode(OK.value())
//                        .timeStamp(LocalDateTime.now())
//                        .build()
//        );
//    }
//
//    @DeleteMapping("employee/{id}")
//    public ResponseEntity<ResponseDTO> deleteEmployee(@PathVariable("id") EmployeeDto employeeDto){
//        this.employeeService.delete(employeeDto);
//        return ResponseEntity.ok(
//                ResponseDTO.builder()
//                        .message("Delete success")
//                        .status(OK)
//                        .data(Map.of("employee", this.employeeService.getList()))
//                        .statusCode(OK.value())
//                        .timeStamp(LocalDateTime.now())
//                        .build()
//        );
//    }

}
