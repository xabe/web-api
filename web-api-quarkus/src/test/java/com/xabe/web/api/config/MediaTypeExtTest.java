package com.xabe.web.api.config;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

class MediaTypeExtTest {

    @Test
    public void shouldHaveProtectedConstructor() throws Exception {
        MatcherAssert.assertThat(new MediaTypeExt(), Matchers.is(Matchers.notNullValue()));
    }

}