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
package org.thingsboard.server.service.component;

import org.thingsboard.server.common.data.plugin.ComponentDescriptor;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.data.rule.RuleChainType;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Andrew Shvayka
 */
public interface ComponentDiscoveryService {

    void discoverComponents();

    /**
     * Returns rule node info.
     *
     * @param clazz clazz ({@link String})
     * @return optional {@link RuleNodeClassInfo}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */

    Optional<RuleNodeClassInfo> getRuleNodeInfo(String clazz);

    /**
     * Returns versioned nodes.
     *
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<RuleNodeClassInfo> getVersionedNodes();

    /**
     * Returns components.
     *
     * @param type type ({@link ComponentType})
     * @param ruleChainType rule chain type ({@link RuleChainType})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<ComponentDescriptor> getComponents(ComponentType type, RuleChainType ruleChainType);

    /**
     * Returns components.
     *
     * @param types types ({@link Set})
     * @param ruleChainType rule chain type ({@link RuleChainType})
     * @return {@link List}
     * @throws Exception if an unexpected error occurs during processing
     */

    List<ComponentDescriptor> getComponents(Set<ComponentType> types, RuleChainType ruleChainType);

    /**
     * Returns component.
     *
     * @param clazz clazz ({@link String})
     * @return optional {@link ComponentDescriptor}, empty if not found
     * @throws Exception if an unexpected error occurs during processing
     */

    Optional<ComponentDescriptor> getComponent(String clazz);
}
