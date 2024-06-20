//package com.nhnacademy.auth.filter;
//
//import com.nhnacademy.auth.domain.CustomUserDetails;
//import com.nhnacademy.auth.domain.Role;
//import com.nhnacademy.auth.dto.ClientLoginRequestDto;
//import com.nhnacademy.auth.dto.ClientLoginResponseDto;
//import com.nhnacademy.auth.utils.JWTUtils;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.io.IOException;
//import java.util.Collections;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//public class LoginFilterTest {
//
//    @InjectMocks
//    private LoginFilter loginFilter;
//
//    @Mock
//    private JWTUtils jwtUtils;
//
//    @Mock
//    private AuthenticationManager authenticationManager;
//
//    @Mock
//    private FilterChain filterChain;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        loginFilter = new LoginFilter(authenticationManager, jwtUtils);
//        SecurityContextHolder.clearContext();
//    }
//
//    @Test
//    public void testAttemptAuthentication() throws IOException {
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.setContent("{\"clientEmail\":\"test@example.com\", \"clientPassword\":\"password\"}".getBytes());
//        MockHttpServletResponse response = new MockHttpServletResponse();
//
//        ClientLoginRequestDto loginRequest = new ClientLoginRequestDto("test@example.com", "password");
//
//        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequest.getClientEmail(), loginRequest.getClientPassword(), null);
//
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authToken);
//
//        Authentication result = loginFilter.attemptAuthentication(request, response);
//
//        assertThat(result).isNotNull();
//        assertThat(result.getName()).isEqualTo("test@example.com");
//    }
//
//    @Test
//    public void testSuccessfulAuthentication() throws IOException, ServletException {
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        MockHttpServletResponse response = new MockHttpServletResponse();
//        Authentication authResult = mock(Authentication.class);
//
//        ClientLoginResponseDto loginResponse = ClientLoginResponseDto.builder()
//                .clientEmail("test@example.com")
//                .clientPassword("password")
//                .clientName("Test User")
//                .role(Role.ROLE_USER)
//                .build();
//
//        CustomUserDetails customUserDetails = new CustomUserDetails(loginResponse);
//        when(authResult.getPrincipal()).thenReturn(customUserDetails);
//        when(authResult.getAuthorities()).thenAnswer(invocation -> Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
//
//        when(jwtUtils.createJwt(anyString(), anyString(), anyString())).thenReturn("testToken");
//
//        loginFilter.successfulAuthentication(request, response, filterChain, authResult);
//
//        assertThat(response.getHeader("Authorization")).isEqualTo("Bearer testToken");
//    }
//
//    @Test
//    public void testUnsuccessfulAuthentication() throws IOException, ServletException {
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        MockHttpServletResponse response = new MockHttpServletResponse();
//        AuthenticationException failed = mock(AuthenticationException.class);
//
//        loginFilter.unsuccessfulAuthentication(request, response, failed);
//
//        assertThat(response.getStatus()).isEqualTo(401);
//    }
//}
