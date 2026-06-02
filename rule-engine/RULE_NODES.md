# Rule engine nodes catalog
| Type | UI name | Java class | Description | Docs |
|------|---------|------------|-------------|------|
| ACTION | assign to customer | `TbAssignToCustomerNode` | Assign message originator entity to customer |  |
| ACTION | calculated fields and alarm rules | `TbCalculatedFieldsNode` | Pushes incoming messages to calculated fields and alarm rules services |  |
| ACTION | clear alarm | `TbClearAlarmNode` | Clear Alarm |  |
| ACTION | copy to view | `TbCopyAttributesToEntityViewNode` | Copy attributes from asset/device to entity view and changes message originator  |  |
| ACTION | create alarm | `TbCreateAlarmNode` | Create or Update Alarm |  |
| ACTION | create relation | `TbCreateRelationNode` | Finds target entity specified in the configuration and creates a relation with t |  |
| ACTION | delay (deprecated) | `TbMsgDelayNode` | Delays incoming message (deprecated) |  |
| ACTION | delete attributes | `TbMsgDeleteAttributesNode` | Delete attributes for Message Originator. |  |
| ACTION | delete relation | `TbDeleteRelationNode` | Deletes relation with the incoming message originator based on the configured di |  |
| ACTION | device profile (deprecated) | `TbDeviceProfileNode` | Process device messages based on device profile settings (deprecated) |  |
| ACTION | device state | `TbDeviceStateNode` | Triggers device connectivity events |  |
| ACTION | generator | `TbMsgGeneratorNode` | Periodically generates messages |  |
| ACTION | gps geofencing events | `TbGpsGeofencingActionNode` | Produces incoming messages using GPS based geofencing |  |
| ACTION | log | `TbLogNode` | Log incoming messages using JS script for transformation Message into String |  |
| ACTION | math function | `TbMathNode` | Apply math function and save the result into the message and/or database |  |
| ACTION | message count | `TbMsgCountNode` | Count incoming messages |  |
| ACTION | push to cloud | `TbMsgPushToCloudNode` | Pushes messages from edge to cloud |  |
| ACTION | push to edge | `TbMsgPushToEdgeNode` | Push messages from cloud to edge |  |
| ACTION | rest call reply | `TbSendRestApiCallReplyNode` | Sends reply to REST API call to rule engine |  |
| ACTION | rpc call reply | `TbSendRPCReplyNode` | Sends reply to RPC call from device |  |
| ACTION | rpc call request | `TbSendRPCRequestNode` | Sends RPC call to device |  |
| ACTION | save to custom table | `TbSaveToCustomCassandraTableNode` | Node stores data from incoming Message payload to the Cassandra database into th |  |
| ACTION | synchronization end | `TbSynchronizationEndNode` | This Node is now deprecated. Use \ |  |
| ACTION | synchronization start | `TbSynchronizationBeginNode` | This Node is now deprecated. Use \ |  |
| ACTION | unassign from customer | `TbUnassignFromCustomerNode` | Unassign message originator entity from customer |  |
| ENRICHMENT | customer attributes | `TbGetCustomerAttributeNode` | Adds message originator customer attributes or latest telemetry into message or  |  |
| ENRICHMENT | customer details | `TbGetCustomerDetailsNode` | Adds message originator customer details into message or message metadata |  |
| ENRICHMENT | fetch device credentials | `TbFetchDeviceCredentialsNode` | Adds device credentials to the message or message metadata |  |
| ENRICHMENT | originator attributes | `TbGetAttributesNode` | Adds attributes and/or latest timeseries data for the message originator to the  |  |
| ENRICHMENT | originator fields | `TbGetOriginatorFieldsNode` | Adds message originator fields values into message or message metadata |  |
| ENRICHMENT | originator telemetry | `TbGetTelemetryNode` | Adds message originator telemetry for selected time range into message metadata |  |
| ENRICHMENT | related device attributes | `TbGetDeviceAttrNode` | Add originators related device attributes and/or latest telemetry values into me |  |
| ENRICHMENT | related entity data | `TbGetRelatedAttributeNode` | Adds originators related entity attributes or latest telemetry or fields into me |  |
| ENRICHMENT | tenant attributes | `TbGetTenantAttributeNode` | Adds message originator tenant attributes or latest telemetry into message or me |  |
| ENRICHMENT | tenant details | `TbGetTenantDetailsNode` | Adds message originator tenant details into message or message metadata |  |
| EXTERNAL | AI request | `TbAiNode` | Sends a request to an AI model using system and user prompts. Supports JSON mode |  |
| EXTERNAL | aws lambda | `TbAwsLambdaNode` | Publish message to the AWS Lambda |  |
| EXTERNAL | aws sns | `TbSnsNode` | Publish message to the AWS SNS |  |
| EXTERNAL | aws sqs | `TbSqsNode` | Publish messages to the AWS SQS |  |
| EXTERNAL | azure iot hub | `TbAzureIotHubNode` | Publish messages to the Azure IoT Hub |  |
| EXTERNAL | gcp pubsub | `TbPubSubNode` | Publish message to the Google Cloud PubSub |  |
| EXTERNAL | kafka | `TbKafkaNode` | Publish messages to Kafka server |  |
| EXTERNAL | mqtt | `TbMqttNode` | Publish messages to the MQTT broker |  |
| EXTERNAL | rabbitmq | `TbRabbitMqNode` | Publish messages to the RabbitMQ |  |
| EXTERNAL | rest api call | `TbRestApiCallNode` | Invoke REST API calls to external REST server |  |
| EXTERNAL | send email | `TbSendEmailNode` | Sends email message via SMTP server. |  |
| EXTERNAL | send notification | `TbNotificationNode` | Sends notification to targets using the template |  |
| EXTERNAL | send sms | `TbSendSmsNode` | Sends SMS message via SMS provider. |  |
| EXTERNAL | send to slack | `TbSlackNode` | Send message via Slack |  |
| FILTER | alarm status filter | `TbCheckAlarmStatusNode` | Checks alarm status. |  |
| FILTER | asset profile switch | `TbAssetTypeSwitchNode` | Route incoming messages based on the name of the asset profile |  |
| FILTER | check fields presence | `TbCheckMessageNode` | Checks the presence of the specified fields in the message and/or metadata. |  |
| FILTER | check relation presence | `TbCheckRelationNode` | Checks the presence of the relation between the originator of the message and ot |  |
| FILTER | device profile switch | `TbDeviceTypeSwitchNode` | Route incoming messages based on the name of the device profile |  |
| FILTER | entity type filter | `TbOriginatorTypeFilterNode` | Filter incoming messages by the type of message originator entity |  |
| FILTER | entity type switch | `TbOriginatorTypeSwitchNode` | Route incoming messages by Message Originator Type |  |
| FILTER | gps geofencing filter | `TbGpsGeofencingFilterNode` | Filter incoming messages by GPS based geofencing |  |
| FILTER | message type filter | `TbMsgTypeFilterNode` | Filter incoming messages by Message Type |  |
| FILTER | message type switch | `TbMsgTypeSwitchNode` | Route incoming messages by Message Type |  |
| FILTER | script | `TbJsFilterNode` | Filter incoming messages using TBEL or JS script |  |
| FILTER | switch | `TbJsSwitchNode` | Routes incoming message to one OR multiple output connections. |  |
| FLOW | acknowledge | `TbAckNode` | Acknowledges the incoming message |  |
| FLOW | checkpoint | `TbCheckpointNode` | transfers the message to another queue |  |
| FLOW | output | `TbRuleChainOutputNode` | transfers the message to the caller rule chain |  |
| FLOW | rule chain | `TbRuleChainInputNode` | Transfers the message to another rule chain |  |
| TRANSFORMATION | change originator | `TbChangeOriginatorNode` | Change message originator to Tenant/Customer/Related Entity/Alarm Originator/Ent |  |
| TRANSFORMATION | copy key-value pairs | `TbCopyKeysNode` | Copies key-value pairs from message to message metadata or vice-versa. |  |
| TRANSFORMATION | deduplication | `TbMsgDeduplicationNode` | Deduplicate messages within the same originator entity for a configurable period |  |
| TRANSFORMATION | delete key-value pairs | `TbDeleteKeysNode` | Deletes key-value pairs from message or message metadata. |  |
| TRANSFORMATION | json path | `TbJsonPathNode` | Transforms incoming message body using JSONPath expression. |  |
| TRANSFORMATION | rename keys | `TbRenameKeysNode` | Renames message or message metadata keys. |  |
| TRANSFORMATION | script | `TbTransformMsgNode` | Change Message payload, Metadata or Message type using JavaScript |  |
| TRANSFORMATION | split array msg | `TbSplitArrayMsgNode` | Split array message into several messages |  |
| TRANSFORMATION | to email | `TbMsgToEmailNode` | Transforms message to email message |  |
