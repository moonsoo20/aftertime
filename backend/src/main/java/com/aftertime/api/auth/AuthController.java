package com.aftertime.api.auth;
import com.aftertime.api.user.*;import jakarta.servlet.http.*;import jakarta.validation.Valid;import jakarta.validation.constraints.*;import org.springframework.http.*;import org.springframework.security.authentication.*;import org.springframework.security.core.Authentication;import org.springframework.security.core.context.SecurityContextHolder;import org.springframework.security.crypto.password.PasswordEncoder;import org.springframework.web.bind.annotation.*;import org.springframework.web.server.ResponseStatusException;
@RestController @RequestMapping("/api/auth") public class AuthController {
 private final AppUserRepository users;private final PasswordEncoder encoder;private final AuthenticationManager manager;
 public AuthController(AppUserRepository u,PasswordEncoder e,AuthenticationManager m){users=u;encoder=e;manager=m;}
 public record SignupRequest(@NotBlank @Size(max=100) String name,@Email @NotBlank String email,@Size(min=8,max=100) String password){}
 public record LoginRequest(@Email @NotBlank String email,@NotBlank String password){}
 public record UserResponse(String name,String email){static UserResponse from(AppUser u){return new UserResponse(u.getName(),u.getEmail());}}
 @PostMapping("/signup") @ResponseStatus(HttpStatus.CREATED) public UserResponse signup(@Valid @RequestBody SignupRequest r,HttpServletRequest h){String e=r.email().trim().toLowerCase();if(users.existsByEmailIgnoreCase(e))throw new ResponseStatusException(HttpStatus.CONFLICT,"이미 가입된 이메일입니다.");users.save(new AppUser(r.name().trim(),e,encoder.encode(r.password())));return login(new LoginRequest(e,r.password()),h);}
 @PostMapping("/login") public UserResponse login(@Valid @RequestBody LoginRequest r,HttpServletRequest h){Authentication a=manager.authenticate(new UsernamePasswordAuthenticationToken(r.email().trim().toLowerCase(),r.password()));SecurityContextHolder.getContext().setAuthentication(a);h.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT",SecurityContextHolder.getContext());return users.findByEmailIgnoreCase(a.getName()).map(UserResponse::from).orElseThrow();}
 @GetMapping("/me") public UserResponse me(Authentication a){return users.findByEmailIgnoreCase(a.getName()).map(UserResponse::from).orElseThrow();}
 @PostMapping("/logout") @ResponseStatus(HttpStatus.NO_CONTENT) public void logout(HttpServletRequest r){HttpSession s=r.getSession(false);if(s!=null)s.invalidate();SecurityContextHolder.clearContext();}
}
