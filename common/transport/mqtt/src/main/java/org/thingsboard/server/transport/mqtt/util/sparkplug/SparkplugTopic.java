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
package org.thingsboard.server.transport.mqtt.util.sparkplug;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;

import static org.thingsboard.server.transport.mqtt.util.sparkplug.SparkplugMessageType.parseMessageType;
import static org.thingsboard.server.transport.mqtt.util.sparkplug.SparkplugTopicService.TOPIC_ROOT_SPB_V_1_0;
import static org.thingsboard.server.transport.mqtt.util.sparkplug.SparkplugTopicService.TOPIC_SPLIT_REGEXP;

/**
 * Created by nickAS21 on 12.12.22
 * A Sparkplug MQTT Topic
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SparkplugTopic {

    /**
     * The Sparkplug namespace version.
     */
    private final String namespace;

    /**
     * The SparkplugDesciptor for this Edge Node or Device
     */
    @JsonIgnore
    private final SparkplugDescriptor sparkplugDescriptor;

    /**
     * The {@link EdgeNodeDescriptor} for this Edge Node or Device
     */
    private final EdgeNodeDescriptor edgeNodeDescriptor;

    /**
     * The ID of the logical grouping of Edge of Network (EoN) Nodes and devices.
     */
    private final String groupId;

    /**
     * The ID of the Edge of Network (EoN) Node.
     */
    private final String edgeNodeId;

    /**
     * The ID of the device.
     */
    private String deviceId;

    /**
     * The ID if this is a Sparkplug Host Application topic
     */
    private final String hostApplicationId;

    /**
     * The message type.
     */
    private final SparkplugMessageType type;

    public SparkplugTopic() {
        this.namespace = null;
        this.sparkplugDescriptor = null;
        this.edgeNodeDescriptor = null;
        this.groupId = null;
        this.edgeNodeId = null;
        this.deviceId = null;
        this.hostApplicationId = null;
        this.type = null;
    }

    public SparkplugTopic(SparkplugTopic sparkplugTopic, SparkplugMessageType type) {
        super();
        this.namespace = sparkplugTopic.namespace;
        this.groupId = sparkplugTopic.groupId;
        this.edgeNodeId = sparkplugTopic.edgeNodeId;
        this.sparkplugDescriptor = new EdgeNodeDescriptor(groupId, edgeNodeId);
        this.edgeNodeDescriptor = new EdgeNodeDescriptor(groupId, edgeNodeId);
        this.deviceId = null;
        this.type = type;
        this.hostApplicationId = null;
    }

    public SparkplugTopic(SparkplugTopic sparkplugTopic, SparkplugMessageType type, String deviceId) {
        super();
        this.namespace = sparkplugTopic.namespace;
        this.groupId = sparkplugTopic.groupId;
        this.edgeNodeId = sparkplugTopic.edgeNodeId;
        this.deviceId = deviceId;
        this.sparkplugDescriptor = deviceId == null
                ? new EdgeNodeDescriptor(groupId, edgeNodeId)
                : new DeviceDescriptor(groupId, edgeNodeId, deviceId);
        this.edgeNodeDescriptor = new EdgeNodeDescriptor(groupId, edgeNodeId);
        this.type = type;
        this.hostApplicationId = null;
    }

    /**
     * A Constructor for Device Topics
     *
     * @param namespace the namespace
     * @param groupId the Group ID
     * @param edgeNodeId the Edge Node ID
     * @param deviceId the Device ID
     * @param type the message type
     */
    public SparkplugTopic(String namespace, String groupId, String edgeNodeId, String deviceId, SparkplugMessageType type) {
        super();
        this.namespace = namespace;
        this.sparkplugDescriptor = deviceId == null
                ? new EdgeNodeDescriptor(groupId, edgeNodeId)
                : new DeviceDescriptor(groupId, edgeNodeId, deviceId);
        this.edgeNodeDescriptor = new EdgeNodeDescriptor(groupId, edgeNodeId);
        this.groupId = groupId;
        this.edgeNodeId = edgeNodeId;
        this.deviceId = deviceId;
        this.hostApplicationId = null;
        this.type = type;
    }

    /**
     * A Constructor for Edge Node Topics
     * @param namespace the namespace
     * @param groupId the group ID
     * @param edgeNodeId the edge node ID
     * @param type the message type
     */
    public SparkplugTopic(String namespace, String groupId, String edgeNodeId, SparkplugMessageType type) {
        super();
        this.namespace = namespace;
        this.groupId = groupId;
        this.edgeNodeId = edgeNodeId;
        this.deviceId = null;
        this.sparkplugDescriptor = new EdgeNodeDescriptor(groupId, edgeNodeId);
        this.edgeNodeDescriptor = new EdgeNodeDescriptor(groupId, edgeNodeId);
        this.hostApplicationId = null;
        this.type = type;
    }

    /**
     * A Constructor for Device Topics
     *
     * @param namespace the namespace
     * @param deviceDescriptor the {@link EdgeNodeDescriptor}
     * @param type the message type
     */
    public SparkplugTopic(String namespace, DeviceDescriptor deviceDescriptor, SparkplugMessageType type) {
        this(namespace, deviceDescriptor.getGroupId(), deviceDescriptor.getEdgeNodeId(), deviceDescriptor.getDeviceId(),
                type);
    }

    /**
     * A Constructor for Edge Node Topics
     *
     * @param namespace the namespace
     * @param edgeNodeDescriptor the {@link EdgeNodeDescriptor}
     * @param type the message type
     */
    public SparkplugTopic(String namespace, EdgeNodeDescriptor edgeNodeDescriptor, SparkplugMessageType type) {
        this(namespace, edgeNodeDescriptor.getGroupId(), edgeNodeDescriptor.getEdgeNodeId(), type);
    }

    /**
     * A Constructor for Host Application Topics
     *
     * @param namespace the namespace
     * @param hostApplicationId the Host Application ID
     */
    public SparkplugTopic(String namespace, String hostApplicationId, SparkplugMessageType type) {
        super();
        this.namespace = namespace;
        this.hostApplicationId = hostApplicationId;
        this.type = type;
        this.sparkplugDescriptor = null;
        this.edgeNodeDescriptor = null;
        this.groupId = null;
        this.edgeNodeId = null;
        this.deviceId = null;
    }
    /**
     * Parse topic.
     *
     * @param topicString topic string ({@link String})
     * @return {@link SparkplugTopic}
     * @throws ThingsboardException if the operation fails validation, authorization, or business rules
     */
    public static SparkplugTopic parseTopic(String topicString) throws ThingsboardException {
        try {
            if (isValidIdElementToUTF8(topicString)) {
                SparkplugMessageType messageType;
                String[] splitTopic = topicString.split(TOPIC_SPLIT_REGEXP);
                if (TOPIC_ROOT_SPB_V_1_0.equals(splitTopic[0])) {
                    if (splitTopic.length == 3) {
                        messageType = parseMessageType(splitTopic[1]);
                        if (messageType.isState())
                            return new SparkplugTopic(TOPIC_ROOT_SPB_V_1_0, splitTopic[2], messageType);
                    } else if (splitTopic.length == 4) {
                        messageType = parseMessageType(splitTopic[2]);
                        if (messageType.isNode())
                            return new SparkplugTopic(TOPIC_ROOT_SPB_V_1_0, splitTopic[1], splitTopic[3], messageType);
                    } else if (splitTopic.length == 5) {
                        messageType = parseMessageType(splitTopic[2]);
                        if (messageType.isDevice())
                            return new SparkplugTopic(TOPIC_ROOT_SPB_V_1_0, splitTopic[1], splitTopic[3], splitTopic[4], messageType);

                    }
                }
            }
            throw new ThingsboardException("Invalid Sparkplug topic from String: " + topicString, ThingsboardErrorCode.INVALID_ARGUMENTS);
        } catch (
                Exception e) {
            throw new ThingsboardException(e, ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        }

    }

    
    /**
     * Returns namespace.
     *
     * @return {@link String}
     * @throws Exception on processing failure
     */
    public String getNamespace() {
        return namespace;
    }

    
    /**
     * Returns sparkplug descriptor.
     *
     * @return {@link SparkplugDescriptor}
     * @throws Exception on processing failure
     */
    public SparkplugDescriptor getSparkplugDescriptor() {
        return sparkplugDescriptor;
    }

    
    /**
     * Returns edge node descriptor.
     *
     * @return {@link EdgeNodeDescriptor}
     * @throws Exception on processing failure
     */
    public EdgeNodeDescriptor getEdgeNodeDescriptor() {
        return edgeNodeDescriptor;
    }

    
    /**
     * Returns group id.
     *
     * @return {@link String}
     * @throws Exception on processing failure
     */
    public String getGroupId() {
        return groupId;
    }

    
    /**
     * Returns edge node id.
     *
     * @return {@link String}
     * @throws Exception on processing failure
     */
    public String getEdgeNodeId() {
        return edgeNodeId;
    }

    
    /**
     * Returns device id.
     *
     * @return {@link String}
     * @throws Exception on processing failure
     */
    public String getDeviceId() {
        return deviceId;
    }
    /**
     * Updates device id plus.
     *
     * @param deviceIdNew device id new ({@link String})
     * @return nothing
     * @throws Exception on processing failure
     */
    public void updateDeviceIdPlus(String deviceIdNew) {
        this.deviceId = this.deviceId.equals("+") ? deviceIdNew : this.deviceId;
    }

    
    /**
     * Returns host application id.
     *
     * @return {@link String}
     * @throws Exception on processing failure
     */
    public String getHostApplicationId() {
        return hostApplicationId;
    }

    
    /**
     * Returns type.
     *
     * @return {@link SparkplugMessageType}
     * @throws Exception on processing failure
     */
    public SparkplugMessageType getType() {
        return type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (hostApplicationId == null) {
            sb.append(getNamespace()).append("/").append(getGroupId()).append("/").append(getType()).append("/")
                    .append(getEdgeNodeId());
            if (getDeviceId() != null) {
                sb.append("/").append(getDeviceId());
            }
        } else {
            sb.append(getNamespace()).append("/").append(getType()).append("/").append(hostApplicationId);
        }
        return sb.toString();
    }

    
    /**
     * Is type.
     *
     * @param type type ({@link SparkplugMessageType})
     * @return the boolean result
     * @throws Exception on processing failure
     */
    public boolean isType(SparkplugMessageType type) {
        return this.type != null && this.type.equals(type);
    }
    /**
     * Is node.
     *
     * @return the boolean result
     * @throws Exception on processing failure
     */
    public boolean isNode() {
        return this.deviceId == null;
    }
    /**
     * Returns node device name.
     *
     * @return {@link String}
     * @throws Exception on processing failure
     */
    public String getNodeDeviceName() {
        return isNode() ? edgeNodeId : deviceId;
    }
    /**
     * Is valid id element to utf8.
     *
     * @param deviceIdElement device id element ({@link String})
     * @return the boolean result
     * @throws Exception on processing failure
     */
    public static boolean isValidIdElementToUTF8(String deviceIdElement) {
        if (deviceIdElement == null) {
            return false;
        }
        String regex = "^(?!.*//)[^+#]*$";
        return deviceIdElement.matches(regex);
    }
}

