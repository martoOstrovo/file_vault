package ukim.finki.file_vault.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ukim.finki.file_vault.model.Role;
import ukim.finki.file_vault.model.User;
import ukim.finki.file_vault.repository.RoleRepository;
import ukim.finki.file_vault.service.SecurityUtils;
import ukim.finki.file_vault.service.TwoFactorTokenService;
import ukim.finki.file_vault.service.UserService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final TwoFactorTokenService twoFactorTokenService;

    public  CustomAuthenticationSuccessHandler(RoleRepository roleRepository,
                                               UserService userService,
                                               TwoFactorTokenService twoFactorTokenService) {

        this.roleRepository = roleRepository;
        this.userService = userService;
        this.twoFactorTokenService = twoFactorTokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        User user = SecurityUtils.getCurrentUser();
        Role roleUnconfirmed = roleRepository.findByRoleName("ROLE_UNCONFIRMED").orElseThrow(() ->
                new RuntimeException("Error in CustomAuthenticationSuccessHandler ROLE search."));

        assert user != null;
        if(user.getRoles().stream().noneMatch(role -> role.equals(roleUnconfirmed))) {
            user.getRoles().add(roleUnconfirmed);
            userService.saveUser(user);
        }

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(authentication.getAuthorities());

        if(grantedAuthorities.stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(roleUnconfirmed.getRoleName()))) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_UNCONFIRMED"));
        }

        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(customUserDetails, user.getPassword(), grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(token);
        twoFactorTokenService.sendTwoFactorTokenEmail(user);
        response.sendRedirect("/2FA");
    }
}
