/**
 * Copyright © 2016-2026 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.http.converter.xml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.Assert;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * Disabled shadow of Spring's XML Jackson HTTP message converter.
 *
 * <p>Spring's {@code RestTemplate} prefers {@code MappingJackson2XmlHttpMessageConverter} over
 * {@code MappingJackson2HttpMessageConverter} for {@code application/json} in some setups,
 * which causes {@code UnsupportedMediaType} errors. This class is registered in the same
 * package as Spring's original to take precedence on the classpath while reporting
 * {@code canRead=false} and {@code canWrite=false} so JSON handling falls through to the
 * correct JSON converter.
 */
public class MappingJackson2XmlHttpMessageConverter extends AbstractJackson2HttpMessageConverter {
    private static final List<MediaType> problemDetailMediaTypes;

    public MappingJackson2XmlHttpMessageConverter() {
        this(Jackson2ObjectMapperBuilder.xml().build());
    }

    public MappingJackson2XmlHttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper, new MediaType[]{new MediaType("application", "xml", StandardCharsets.UTF_8), new MediaType("text", "xml", StandardCharsets.UTF_8), new MediaType("application", "*+xml", StandardCharsets.UTF_8)});
        Assert.isInstanceOf(XmlMapper.class, objectMapper, "XmlMapper required");
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        Assert.isInstanceOf(XmlMapper.class, objectMapper, "XmlMapper required");
        super.setObjectMapper(objectMapper);
    }

    protected List<MediaType> getMediaTypesForProblemDetail() {
        return problemDetailMediaTypes;
    }

    static {
        problemDetailMediaTypes = Collections.singletonList(MediaType.APPLICATION_PROBLEM_XML);
    }

    /**
     * Always returns false so RestTemplate does not use this converter for XML/JSON reads.
     *
     * @param type         target type (ignored)
     * @param contextClass controller method context (ignored)
     * @param mediaType    Content-Type (ignored)
     * @return {@code false} — delegate to other message converters
     */
    @Override
    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
        return false;
    }

    /**
     * Always returns false so RestTemplate does not use this converter for XML/JSON writes.
     *
     * @param clazz     object type (ignored)
     * @param mediaType Content-Type (ignored)
     * @return {@code false} — delegate to other message converters
     */
    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;
    }
}
