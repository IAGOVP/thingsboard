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
package org.thingsboard.server.common.data.ai.model.chat;

import dev.langchain4j.model.chat.ChatModel;

/**
 * langchain4j chat model configurer contract.
 */
public interface Langchain4jChatModelConfigurer {

    ChatModel configureChatModel(OpenAiChatModelConfig chatModelConfig);
/**
 * Configure chat model.
 *
 * @param chatModelConfig chat model config ({@link AzureOpenAiChatModelConfig})
 * @return {@link ChatModel}
 */

    ChatModel configureChatModel(AzureOpenAiChatModelConfig chatModelConfig);
/**
 * Configure chat model.
 *
 * @param chatModelConfig chat model config ({@link GoogleAiGeminiChatModelConfig})
 * @return {@link ChatModel}
 */

    ChatModel configureChatModel(GoogleAiGeminiChatModelConfig chatModelConfig);
/**
 * Configure chat model.
 *
 * @param chatModelConfig chat model config ({@link GoogleVertexAiGeminiChatModelConfig})
 * @return {@link ChatModel}
 */

    ChatModel configureChatModel(GoogleVertexAiGeminiChatModelConfig chatModelConfig);
/**
 * Configure chat model.
 *
 * @param chatModelConfig chat model config ({@link MistralAiChatModelConfig})
 * @return {@link ChatModel}
 */

    ChatModel configureChatModel(MistralAiChatModelConfig chatModelConfig);
/**
 * Configure chat model.
 *
 * @param chatModelConfig chat model config ({@link AnthropicChatModelConfig})
 * @return {@link ChatModel}
 */

    ChatModel configureChatModel(AnthropicChatModelConfig chatModelConfig);
/**
 * Configure chat model.
 *
 * @param chatModelConfig chat model config ({@link AmazonBedrockChatModelConfig})
 * @return {@link ChatModel}
 */

    ChatModel configureChatModel(AmazonBedrockChatModelConfig chatModelConfig);
/**
 * Configure chat model.
 *
 * @param chatModelConfig chat model config ({@link GitHubModelsChatModelConfig})
 * @return {@link ChatModel}
 */

    ChatModel configureChatModel(GitHubModelsChatModelConfig chatModelConfig);
/**
 * Configure chat model.
 *
 * @param chatModelConfig chat model config ({@link OllamaChatModelConfig})
 * @return {@link ChatModel}
 */

    ChatModel configureChatModel(OllamaChatModelConfig chatModelConfig);

}
