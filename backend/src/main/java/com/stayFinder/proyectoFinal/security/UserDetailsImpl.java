package com.stayFinder.proyectoFinal.security;

import com.stayFinder.proyectoFinal.entity.Usuario;
import com.stayFinder.proyectoFinal.entity.enums.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UserDetailsImpl implements UserDetails {

    private final long id;
    private final String email;
    private final String password;
    private final Role role; // ✅ Guardamos el rol

    public UserDetailsImpl(Usuario user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getContrasena();
        this.role = user.getRole(); // ✅ Asignamos el rol desde la entidad Usuario
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring Security requiere "ROLE_" como prefijo
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override public String getUsername() { return email; }
    @Override public String getPassword() { return password; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() {return true;}
}
