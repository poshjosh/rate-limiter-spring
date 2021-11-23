package com.looseboxes.ratelimiter.web.spring.weblayer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AbstractResourceTest {

    @Autowired
    private MockMvc mockMvc;

    void shouldFailWhenMaxLimitIsExceeded(String endpoint, int maxLimit) throws Exception {

        for(int i=0; i<maxLimit + 1; i++) {
            if(i == maxLimit) {
                assertThrows(Exception.class, () -> shouldReturnDefaultResult(endpoint));
            }else{
                shouldReturnDefaultResult(endpoint);
            }
        }
    }

    void shouldReturnDefaultResult(String endpoint) throws Exception {
        final String result = endpoint;
        this.mockMvc.perform(get(endpoint)).andExpect(status().isOk())
                .andExpect(content().string(containsString(result)));
    }
}
