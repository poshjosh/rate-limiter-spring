package com.looseboxes.ratelimiter.spring.web;

import org.springframework.core.convert.converter.Converter;

import javax.servlet.http.HttpServletRequest;

public interface RequestToIdConverter extends Converter<HttpServletRequest, Object> {

}
