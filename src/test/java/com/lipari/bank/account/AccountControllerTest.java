package com.lipari.bank.account;
import com.lipari.bank.auth.LipariBankUserDetailsService;
import com.lipari.bank.shared.config.SecurityConfig;
import com.lipari.bank.shared.exception.ResourceNotFoundException;
import com.lipari.bank.shared.security.JwtService;
import com.lipari.bank.shared.security.LipariBankAccessDeniedHandler;
import com.lipari.bank.shared.security.LipariBankAuthenticationEntryPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@Import({
    SecurityConfig.class,
    LipariBankAccessDeniedHandler.class,
    LipariBankAuthenticationEntryPoint.class
})
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private LipariBankUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAll_authenticated_returns200() throws Exception {
        when(accountService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isOk());
    }

    @Test
    void getAll_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_asAdmin_returns204() throws Exception {
        doNothing().when(accountService).delete(1L);

        mockMvc.perform(delete("/api/v1/accounts/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void delete_asCustomer_returns403() throws Exception {
        mockMvc.perform(delete("/api/v1/accounts/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getById_notFound_returns404() throws Exception {
        when(accountService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Account not found with id: 99"));

        mockMvc.perform(get("/api/v1/accounts/99"))
                .andExpect(status().isNotFound());
    }
}
