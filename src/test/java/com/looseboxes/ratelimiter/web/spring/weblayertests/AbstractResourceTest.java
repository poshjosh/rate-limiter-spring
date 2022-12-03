package com.looseboxes.ratelimiter.web.spring.weblayertests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class AbstractResourceTest {

    @Autowired
    private MockMvc mockMvc;

    void shouldFailWhenMaxLimitIsExceeded(String endpoint, int maxLimit) throws Exception {

        for(int i=0; i<maxLimit + 1; i++) {
            if(i == maxLimit) {
                shouldReturnStatusOfTooManyRequests(endpoint);
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

    void shouldReturnStatusOfTooManyRequests(String endpoint) throws Exception {
        shouldReturnStatus(endpoint, HttpStatus.TOO_MANY_REQUESTS.value());
    }

    void shouldReturnStatus(String endpoint, int expectedStatus) throws Exception {
        this.mockMvc.perform(get(endpoint)).andExpect(status().is(expectedStatus));
    }
}
