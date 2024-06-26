package com.nhnacademy.auth.domain;

import com.nhnacademy.auth.exception.UnknownRoleException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RoleTest {

    @Test
    public void testJsonCreatorWithValidRole() {
        assertThat(Role.jsonCreator("ROLE_ADMIN")).isEqualTo(Role.ROLE_ADMIN);
        assertThat(Role.jsonCreator("ROLE_USER")).isEqualTo(Role.ROLE_USER);
        assertThat(Role.jsonCreator("ROLE_NON_USER")).isEqualTo(Role.ROLE_NON_USER);
    }

    @Test
    public void testJsonCreatorWithValidRoleWithoutPrefix() {
        assertThat(Role.jsonCreator("ADMIN")).isEqualTo(Role.ROLE_ADMIN);
        assertThat(Role.jsonCreator("USER")).isEqualTo(Role.ROLE_USER);
        assertThat(Role.jsonCreator("NON_USER")).isEqualTo(Role.ROLE_NON_USER);
    }

    @Test
    public void testJsonCreatorWithInvalidRole() {
        assertThatThrownBy(() -> Role.jsonCreator("INVALID"))
                .isInstanceOf(UnknownRoleException.class)
                .hasMessageContaining("Invalid role: ROLE_INVALID");
    }

    @Test
    public void testGetAuthority() {
        assertThat(Role.ROLE_ADMIN.getAuthority()).isEqualTo("ROLE_ADMIN");
        assertThat(Role.ROLE_USER.getAuthority()).isEqualTo("ROLE_USER");
        assertThat(Role.ROLE_NON_USER.getAuthority()).isEqualTo("ROLE_NON_USER");
    }
}
