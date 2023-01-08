package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.BandwidthFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class AbstractResourceTest {

    static {
        final String bandwidthFactoryClass = BandwidthFactory.AllOrNothing.class.getName();
        System.out.printf("%s [%s] INFO  AbstractResourceTest - Using BandwidthFactory: %s\n",
                java.time.LocalTime.now(), Thread.currentThread().getName(), bandwidthFactoryClass);
        System.setProperty("bandwidth-factory-class", bandwidthFactoryClass);
    }

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
        this.mockMvc.perform(doGet(endpoint)).andExpect(status().isOk())
                .andExpect(content().string(containsString(result)));
    }

    void shouldReturnStatusOfTooManyRequests(String endpoint) throws Exception {
        shouldReturnStatus(endpoint, HttpStatus.TOO_MANY_REQUESTS.value());
    }

    void shouldReturnStatus(String endpoint, int expectedStatus) throws Exception {
        this.mockMvc.perform(doGet(endpoint)).andExpect(status().is(expectedStatus));
    }

    protected MockHttpServletRequestBuilder doGet(String endpoint) {
        MockHttpServletRequestBuilder builder = get(endpoint);
        return builder;
    }
}
