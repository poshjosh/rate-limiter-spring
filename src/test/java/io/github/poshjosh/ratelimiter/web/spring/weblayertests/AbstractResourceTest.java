package io.github.poshjosh.ratelimiter.web.spring.weblayertests;

import io.github.poshjosh.ratelimiter.bandwidths.BandwidthFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.servlet.http.Cookie;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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

    private Cookie [] cookies = new Cookie[0];

    void shouldFailWhenMaxLimitIsExceeded(String endpoint, int maxLimit) throws Exception {

        for(int i=0; i<maxLimit + 1; i++) {
            if(i == maxLimit) {
                shouldReturnStatusOfTooManyRequests(endpoint);
            }else{
                shouldReturnDefaultResult(endpoint);
            }
        }
    }

    MvcResult shouldReturnDefaultResult(String endpoint) throws Exception {
        return shouldReturnDefaultResult(HttpMethod.GET, endpoint);
    }

    MvcResult shouldReturnDefaultResult(HttpMethod method, String endpoint) throws Exception {
        final String result = endpoint;
        return this.perform(requestBuilder(method, endpoint)).andExpect(status().isOk())
                .andExpect(content().string(containsString(result))).andReturn();
    }

    MvcResult shouldReturnStatusOfTooManyRequests(String endpoint) throws Exception {
        return shouldReturnStatusOfTooManyRequests(HttpMethod.GET, endpoint);
    }

    MvcResult shouldReturnStatusOfTooManyRequests(HttpMethod method, String endpoint) throws Exception {
        return shouldReturnStatus(method, endpoint, HttpStatus.TOO_MANY_REQUESTS.value());
    }

    MvcResult shouldReturnStatus(HttpMethod method, String endpoint, int expectedStatus) throws Exception {
        return this.perform(requestBuilder(method, endpoint)).andExpect(status().is(expectedStatus)).andReturn();
    }

    protected ResultActions perform(RequestBuilder requestBuilder) throws Exception{
        cookies = new Cookie[0];
        return this.mockMvc.perform(requestBuilder).andDo(new ResultHandler() {
            @Override public void handle(MvcResult mvcResult) {
                cookies = mvcResult.getResponse().getCookies();
            }
        });
    }

    protected MockHttpServletRequestBuilder requestBuilder(HttpMethod method, String endpoint) {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.request(method, endpoint);
        builder.with(csrf());
        return builder;
    }

    public Cookie[] getCookiesFromLastRequest() {
        return cookies == null ? new Cookie[0] : cookies;
    }
}
