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
package org.thingsboard.server.common.data.ai.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.langchain4j.data.message.Content;
import dev.langchain4j.data.message.TextContent;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import static org.thingsboard.server.common.data.ai.dto.TbContent.TbTextContent;

@Schema(
        discriminatorProperty = "contentType",
        discriminatorMapping = {
                @DiscriminatorMapping(value = "TEXT", schema = TbTextContent.class)
        }
)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "contentType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TbTextContent.class, name = "TEXT")
})
/**
 * tb content contract.
 */
public sealed interface TbContent permits TbTextContent {

    TbContentType contentType();
/**
 * To lang chain content.
 *
 * @return {@link Content}
 */

    Content toLangChainContent();

    enum TbContentType {

        TEXT

    }

    @Schema(
            description = "Text-based content part of a user's prompt",
            allOf = TbContent.class
    )
    /**
     * Tb text content.
     *
     * @param Schema.RequiredMode.REQUIRED schema.required mode.required
     * @param content" content"
     * @param text text ({@link String})
     * @return the record value
     */
    record TbTextContent(
            @NotBlank
            @Schema(
                    requiredMode = Schema.RequiredMode.REQUIRED,
                    description = "The text content",
                    example = "What is the weather like in Kyiv today?"
            )
            String text
    ) implements TbContent {
        /**
         * Content type.
         *
         * @return {@link TbContentType}
         */

        @Override
        public TbContentType contentType() {
            return TbContentType.TEXT;
        }
        /**
         * To lang chain content.
         *
         * @return {@link Content}
         */

        @Override
        public Content toLangChainContent() {
            return TextContent.from(text);
        }

    }

}
