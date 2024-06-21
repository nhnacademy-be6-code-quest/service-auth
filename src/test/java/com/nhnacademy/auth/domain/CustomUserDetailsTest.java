//package com.nhnacademy.auth.domain;
//
//import com.nhnacademy.auth.dto.ClientLoginResponseDto;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.security.core.GrantedAuthority;
//
//import java.util.Collection;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//public class CustomUserDetailsTest {
//
//    private CustomUserDetails customUserDetails;
//    private ClientLoginResponseDto clientLoginResponseDto;
//
//    @BeforeEach
//    public void setUp() {
//        clientLoginResponseDto = ClientLoginResponseDto.builder()
//                .clientEmail("test@example.com")
//                .clientPassword("password")
//                .clientName("Test User")
//                .role(Role.ROLE_USER)
//                .build();
//
//        customUserDetails = new CustomUserDetails(clientLoginResponseDto);
//    }
//
//    @Test
//    public void testGetAuthorities() {
//        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();
//        assertThat(authorities).hasSize(1);
//        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_USER");
//    }
//
//    @Test
//    public void testGetPassword() {
//        assertThat(customUserDetails.getPassword()).isEqualTo("password");
//    }
//
//    @Test
//    public void testGetUsername() {
//        assertThat(customUserDetails.getUsername()).isEqualTo("test@example.com");
//    }
//
//    @Test
//    public void testIsAccountNonExpired() {
//        assertThat(customUserDetails.isAccountNonExpired()).isTrue();
//    }
//
//    @Test
//    public void testIsAccountNonLocked() {
//        assertThat(customUserDetails.isAccountNonLocked()).isTrue();
//    }
//
//    @Test
//    public void testIsCredentialsNonExpired() {
//        assertThat(customUserDetails.isCredentialsNonExpired()).isTrue();
//    }
//
//    @Test
//    public void testIsEnabled() {
//        assertThat(customUserDetails.isEnabled()).isTrue();
//    }
//}
