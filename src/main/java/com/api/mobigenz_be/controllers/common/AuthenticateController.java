package com.api.mobigenz_be.controllers.common;

import com.api.mobigenz_be.DTOs.AccountDTO;
import com.api.mobigenz_be.configs.securities.jwt.JWTFilter;
import com.api.mobigenz_be.configs.securities.jwt.TokenProvider;
import com.api.mobigenz_be.constants.Constant;
import com.api.mobigenz_be.controllers.admin.vm.LoginVM;
import com.api.mobigenz_be.entities.*;
import com.api.mobigenz_be.repositories.AccountRepository;
import com.api.mobigenz_be.services.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(value = Constant.Api.Path.PREFIX)
@CrossOrigin(origins = "http://localhost:4200")
public class AuthenticateController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final TokenProvider tokenProvider;

    public AuthenticateController(AuthenticationManagerBuilder authenticationManagerBuilder, TokenProvider tokenProvider) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.tokenProvider = tokenProvider;
    }

    @Autowired
    public JavaMailSender emailSender;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RoleServiceImpl roleServiceImpl;

    @Autowired
    EmailSenderService emailSenderService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OtpService otpService;


    @PostMapping("login")
    public ResponseEntity<?> authenticateAdmin(@Valid @RequestBody LoginVM loginVM) {
//		T???o chu???i authentication t??? email v?? password (object LoginRequest
        try {
            Account account = this.accountRepository.findAccountByEmail(loginVM.getEmail());
            if (account != null) {
                UsernamePasswordAuthenticationToken authenticationString = new UsernamePasswordAuthenticationToken(
                        loginVM.getEmail(),
                        loginVM.getPassword()
                );
                Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationString);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = tokenProvider.createToken(authentication, loginVM.getRememberMe());
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, String.format("Bearer %s", jwt));
                System.out.println(jwt);
                return new ResponseEntity<>(Collections.singletonMap("token", jwt), httpHeaders, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return ResponseEntity.internalServerError().build();
//        User userLogin = userService.findUserByUserName(adminLoginVM.getUserName());
//        return ResponseEntity.ok().body(new JWTTokenResponse(jwt, userLogin.getUserName())); //Tr??? v??? chu???i jwt(authentication string)

    }

    @PostMapping("register")
    public ResponseEntity<ResponseObject> insert(@RequestBody Account account) {
        try {
            Account acc = this.accountRepository.findAccountByEmailorPhone(account.getEmail(), account.getPhoneNumber());
            if (acc != null) {
                if (acc.getPhoneNumber().equalsIgnoreCase(account.getPhoneNumber())) {
                    return ResponseEntity.status(BAD_REQUEST).body(
                            new ResponseObject("false", "S??? ??i???n tho???i ???? t???n t???i!", acc.getPhoneNumber())
                    );
                }
                if (acc.getEmail().equalsIgnoreCase(account.getEmail())) {
                    return ResponseEntity.status(BAD_REQUEST).body(
                            new ResponseObject("false", "Email ???? t???n t???i!", acc.getEmail())
                    );
                }
                return ResponseEntity.status(BAD_REQUEST).body(
                        new ResponseObject("false", "????ng k?? th???t b???i!", "")
                );

            } else {
                String password = passwordEncoder.encode(account.getPassword());
                String email = account.getEmail();
                String name = account.getEmail().substring(0, email.indexOf("@"));
                Set<Role> roles = new HashSet<>();
                Role role = this.roleServiceImpl.getRoleById(3);
                roles.add(role);
                account.setRoles(roles);
                account.setPassword(password);
                account.setStatus(0);

                Customer customer = new Customer();
                customer.setCustomerName(name);
                customer.setAccount(account);
                customer.setPhoneNumber(account.getPhoneNumber());
                customer.setBirthday(LocalDate.of(1970, 1, 1));
                customer.setEmail(account.getEmail());
                customer.setCustomerType(0);
                customer.setStatus(1);
                customer.setCtime(LocalDate.now());

                account.setCustomer(customer);
                AccountDTO accountDTO = this.accountService.add(account);
                return ResponseEntity.status(OK).body(
                        new ResponseObject("true", "????ng k?? t??i kho???n th??nh c??ng!", "")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("forgot")
    public ResponseEntity<ResponseObject> forgot(@RequestParam(value = "email") String email) {
        Account account = this.accountRepository.findAccountByEmail(email);
        try {
            if (account == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseObject("false", "Email kh??ng c?? trong h??? th???ng!", "")
                );
            } else {
                ExecutorService executor = Executors.newFixedThreadPool(10);
                Random random = new Random();
                int otp = random.nextInt(900000) + 100000;
                SimpleMailMessage message = new SimpleMailMessage();

                message.setTo(email);
                message.setSubject("M?? OTP x??c th???c t??i kho???n:");
                message.setText("M?? OTP c???a b???n l??: " + otp);
                Otp isOtp = new Otp();
                isOtp.setOtpCode(otp);
                isOtp.setEmailAccount(account.getEmail());
                isOtp.setStatus(1);
                isOtp.setIssue_At(System.currentTimeMillis() + 300000);
                Optional<Otp> otp1 = this.otpService.findByEmail(email);
                if (otp1.isPresent()) {
                    isOtp.setId(otp1.get().getId());
                }
                this.otpService.save(isOtp);

                CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> emailSender.send(message), executor);
                executor.shutdown();

                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("true", "OTP ???? ???????c g???i ?????n email c???a b???n!", email)
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(BAD_REQUEST).body(
                    new ResponseObject("false", "L???y m?? OTP th???t b???i!", "")
            );
        }
    }

//    @GetMapping("getOTP")
//    public ResponseEntity<ResponseObject> getOTP(@RequestParam(value = "email") String email) {
//        Object isOtp = session.getAttribute(email);
//        return ResponseEntity.status(OK).body(
//                new ResponseObject("true", "OTP la: " + isOtp, isOtp)
//        );
//    }


    @GetMapping("changepass")
    public ResponseEntity<ResponseObject> changePass(@RequestParam(value = "email") String email,
                                                     @RequestParam(value = "isOtp") String otp,
                                                     @RequestParam(value = "password") String password,
                                                     @RequestParam(value = "repassword") String repassword
    ) {
        try {
            Optional<Otp> isOtp = this.otpService.findByEmail(email);
            if (isOtp.isPresent() && (otp.equals(isOtp.get().getOtpCode().toString())) && password.equals(repassword)) {
                Account account = accountService.findByEmail(email);
                account.setPassword(passwordEncoder.encode(password));
                accountService.update(account);
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("true", "Thay ?????i m???t kh???u th??nh c??ng", "")
                );
            } else {
                return ResponseEntity.status(BAD_REQUEST).body(
                        new ResponseObject("false", "OTP kh??ng ch??nh x??c! Ki???m tra l???i m?? OTP trong gmail c???a b???n!", "")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("false", "Thay ?????i m???t kh???u th???t b???i", ""));
        }
    }
}
