package ukim.finki.file_vault.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ukim.finki.file_vault.repository.RoleRepository;
import java.io.IOException;

@Component
public class TwoFactorAuthFilter extends OncePerRequestFilter {
    public static final String TWO_FACTOR_AUTHENTICATION_URL = "/2FA";
    public final RoleRepository roleRepository;

    public TwoFactorAuthFilter(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(request.getRequestURI().startsWith("/static/") || request.getRequestURI().startsWith("/logout")){
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            boolean hasUnconfirmedRole = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_UNCONFIRMED"));
            boolean isAccessingTwoFactorURI = request.getRequestURI().equals(TWO_FACTOR_AUTHENTICATION_URL);

            if(hasUnconfirmedRole && !isAccessingTwoFactorURI) {
                response.sendRedirect(TWO_FACTOR_AUTHENTICATION_URL);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
