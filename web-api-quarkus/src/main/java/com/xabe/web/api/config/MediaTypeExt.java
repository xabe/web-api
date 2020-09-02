package com.xabe.web.api.config;

import javax.ws.rs.core.MediaType;

public final class MediaTypeExt extends MediaType {

    public static final String APPLICATION_PROBLEM_JSON = "application/problem+json";
    public static final MediaType APPLICATION_PROBLEM_JSON_TYPE = new MediaType("application", "problem+json");
    MediaTypeExt(){}

}