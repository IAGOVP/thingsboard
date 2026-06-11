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
package org.thingsboard.server.service.subscription;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.kv.Aggregation;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.query.AbstractDataQuery;
import org.thingsboard.server.common.data.query.EntityData;
import org.thingsboard.server.common.data.query.EntityDataPageLink;
import org.thingsboard.server.common.data.query.EntityDataQuery;
import org.thingsboard.server.common.data.query.EntityKey;
import org.thingsboard.server.common.data.query.EntityKeyType;
import org.thingsboard.server.common.data.query.TsValue;
import org.thingsboard.server.dao.attributes.AttributesService;
import org.thingsboard.server.dao.entity.EntityService;
import org.thingsboard.server.service.ws.WebSocketService;
import org.thingsboard.server.service.ws.WebSocketSessionRef;
import org.thingsboard.server.service.ws.telemetry.sub.TelemetrySubscriptionUpdate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * Subscription context for tb abstract data WebSocket commands.
 * <p>Maintains query state, caches, and pending updates for one command id.
 */

@Slf4j
public abstract class TbAbstractDataSubCtx<T extends AbstractDataQuery<? extends EntityDataPageLink>> extends TbAbstractEntityQuerySubCtx<T> {

    protected final Map<Integer, EntityId> subToEntityIdMap;
    @Getter
    protected PageData<EntityData> data;

    /**
     * Constructs {@link TbAbstractDataSubCtx} with the supplied dependencies and configuration.
     * @param serviceId service id
     * @param wsService ws service
     * @param entityService entity service
     * @param localSubscriptionService local subscription service
     * @param attributesService attributes service
     * @param stats stats
     * @param sessionRef reference to the WebSocket session
     * @param cmdId client command id
     */

    public TbAbstractDataSubCtx(String serviceId, WebSocketService wsService,
                                EntityService entityService, TbLocalSubscriptionService localSubscriptionService,
                                AttributesService attributesService, SubscriptionServiceStatistics stats,
                                WebSocketSessionRef sessionRef, int cmdId) {
        super(serviceId, wsService, entityService, localSubscriptionService, attributesService, stats, sessionRef, cmdId);
        this.subToEntityIdMap = new ConcurrentHashMap<>();
    }

    /**
     * Fetches data.
     * @return @Override
    public void
     */

    @Override
    public void fetchData() {
        this.data = findEntityData();
    }

    /**
     * Finds entity data.
     *
     * <p>Default implementation inherited from the supertype.
     * @return {@link PageData}
     */

    protected PageData<EntityData> findEntityData() {
        PageData<EntityData> result = entityService.findEntityDataByQuery(getTenantId(), getCustomerId(), buildEntityDataQuery());
        if (log.isTraceEnabled()) {
            result.getData().forEach(ed -> {
                log.trace("[{}][{}] EntityData: {}", getSessionId(), getCmdId(), ed);
            });
        }
        return result;
    }

    /**
     * Is dynamic.
     * @return {@code true} when the condition holds
     */

    @Override
    public boolean isDynamic() {
        return query != null && query.getPageLink().isDynamic();
    }

    /**
     * Updates update.
     *
     * <p>Default implementation inherited from the supertype.
     * @return @Override
    protected synchronized void
     */

    @Override
    protected synchronized void update() {
        PageData<EntityData> newData = findEntityData();
        Map<EntityId, EntityData> oldDataMap;
        if (data != null && !data.getData().isEmpty()) {
            oldDataMap = data.getData().stream().collect(Collectors.toMap(EntityData::getEntityId, Function.identity(), (a, b) -> a));
        } else {
            oldDataMap = Collections.emptyMap();
        }
        Map<EntityId, EntityData> newDataMap = newData.getData().stream().collect(Collectors.toMap(EntityData::getEntityId, Function.identity(), (a, b) -> a));
        if (oldDataMap.size() == newDataMap.size() && oldDataMap.keySet().equals(newDataMap.keySet())) {
            log.trace("[{}][{}] No updates to entity data found", sessionRef.getSessionId(), cmdId);
        } else {
            this.data = newData;
            doUpdate(newDataMap);
        }
    }

    /**
     * Do update.
     * @param newDataMap new data map
     */

    protected abstract void doUpdate(Map<EntityId, EntityData> newDataMap);

    /**
     * Builds entity data query.
     * @return {@link EntityDataQuery}
     */

    protected abstract EntityDataQuery buildEntityDataQuery();

    /**
     * Returns entities data.
     * @return {@link List}
     */

    public List<EntityData> getEntitiesData() {
        return data.getData();
    }

    /**
     * Clears subscriptions.
     * @return @Override
    public void
     */

    @Override
    public void clearSubscriptions() {
        clearEntitySubscriptions();
        super.clearSubscriptions();
    }

    /**
     * Clears entity subscriptions.
     *
     * <p>Default implementation inherited from the supertype.
     */

    public void clearEntitySubscriptions() {
        if (subToEntityIdMap != null) {
            for (Integer subId : subToEntityIdMap.keySet()) {
                localSubscriptionService.cancelSubscription(getTenantId(), getSessionId(), subId);
            }
            subToEntityIdMap.clear();
        }
    }

    /**
     * Creates latest values subscriptions.
     * @param keys keys
     */

    public void createLatestValuesSubscriptions(List<EntityKey> keys) {
        createSubscriptions(keys, true, 0, 0);
    }

    /**
     * Creates time series subscriptions.
     * @param entityKeyStates entity key states
     * @param startTs start ts
     * @param endTs end ts
     */

    public void createTimeSeriesSubscriptions(Map<EntityData, Map<String, Long>> entityKeyStates, long startTs, long endTs) {
        createTimeSeriesSubscriptions(entityKeyStates, startTs, endTs, false);
    }

    /**
     * Creates time series subscriptions.
     * @param entityKeyStates entity key states
     * @param startTs start ts
     * @param endTs end ts
     * @param resultToLatestValues result to latest values
     */

    public void createTimeSeriesSubscriptions(Map<EntityData, Map<String, Long>> entityKeyStates, long startTs, long endTs, boolean resultToLatestValues) {
        entityKeyStates.forEach((entityData, keyStates) -> {
            int subIdx = sessionRef.getSessionSubIdSeq().incrementAndGet();
            subToEntityIdMap.put(subIdx, entityData.getEntityId());
            localSubscriptionService.addSubscription(
                    createTsSub(entityData, subIdx, false, startTs, endTs, keyStates, resultToLatestValues), sessionRef);
        });
    }

    private void createSubscriptions(List<EntityKey> keys, boolean latestValues, long startTs, long endTs) {
        Map<EntityKeyType, List<EntityKey>> keysByType = getEntityKeyByTypeMap(keys);
        for (EntityData entityData : data.getData()) {
            List<TbSubscription> entitySubscriptions = addSubscriptions(entityData, keysByType, latestValues, startTs, endTs);
            entitySubscriptions.forEach(subscription -> localSubscriptionService.addSubscription(subscription, sessionRef));
        }
    }

    /**
     * Returns entity key by type map.
     * @param keys keys
     * @return {@link Map}
     */

    protected Map<EntityKeyType, List<EntityKey>> getEntityKeyByTypeMap(List<EntityKey> keys) {
        Map<EntityKeyType, List<EntityKey>> keysByType = new HashMap<>();
        keys.forEach(key -> keysByType.computeIfAbsent(key.getType(), k -> new ArrayList<>()).add(key));
        return keysByType;
    }

    /**
     * Registers subscriptions.
     * @param entityData entity data
     * @param keysByType keys by type
     * @param latestValues latest values
     * @param startTs start ts
     * @param endTs end ts
     * @return {@link List}
     */

    protected List<TbSubscription> addSubscriptions(EntityData entityData, Map<EntityKeyType, List<EntityKey>> keysByType, boolean latestValues, long startTs, long endTs) {
        List<TbSubscription> subscriptionList = new ArrayList<>();
        keysByType.forEach((keysType, keysList) -> {
            int subIdx = sessionRef.getSessionSubIdSeq().incrementAndGet();
            subToEntityIdMap.put(subIdx, entityData.getEntityId());
            switch (keysType) {
                case TIME_SERIES:
                    subscriptionList.add(createTsSub(entityData, subIdx, keysList, latestValues, startTs, endTs));
                    break;
                case CLIENT_ATTRIBUTE:
                    subscriptionList.add(createAttrSub(entityData, subIdx, keysType, TbAttributeSubscriptionScope.CLIENT_SCOPE, keysList));
                    break;
                case SHARED_ATTRIBUTE:
                    subscriptionList.add(createAttrSub(entityData, subIdx, keysType, TbAttributeSubscriptionScope.SHARED_SCOPE, keysList));
                    break;
                case SERVER_ATTRIBUTE:
                    subscriptionList.add(createAttrSub(entityData, subIdx, keysType, TbAttributeSubscriptionScope.SERVER_SCOPE, keysList));
                    break;
                case ATTRIBUTE:
                    subscriptionList.add(createAttrSub(entityData, subIdx, keysType, TbAttributeSubscriptionScope.ANY_SCOPE, keysList));
                    break;
            }
        });
        return subscriptionList;
    }

    private TbSubscription createAttrSub(EntityData entityData, int subIdx, EntityKeyType keysType, TbAttributeSubscriptionScope scope, List<EntityKey> subKeys) {
        Map<String, Long> keyStates = buildKeyStats(entityData, keysType, subKeys, true);
        log.trace("[{}][{}][{}] Creating attributes subscription for [{}] with keys: {}", serviceId, cmdId, subIdx, entityData.getEntityId(), keyStates);
        return TbAttributeSubscription.builder()
                .serviceId(serviceId)
                .sessionId(sessionRef.getSessionId())
                .subscriptionId(subIdx)
                .tenantId(sessionRef.getSecurityCtx().getTenantId())
                .entityId(entityData.getEntityId())
                .updateProcessor((sub, subscriptionUpdate) -> sendWsMsg(sub.getSessionId(), subscriptionUpdate, keysType))
                .queryTs(createdTime)
                .allKeys(false)
                .keyStates(keyStates)
                .scope(scope)
                .build();
    }

    private TbSubscription createTsSub(EntityData entityData, int subIdx, List<EntityKey> subKeys, boolean latestValues, long startTs, long endTs) {
        Map<String, Long> keyStates = buildKeyStats(entityData, EntityKeyType.TIME_SERIES, subKeys, latestValues);
        if (!latestValues && entityData.getTimeseries() != null) {
            entityData.getTimeseries().forEach((k, v) -> {
                long ts = Arrays.stream(v).map(TsValue::getTs).max(Long::compareTo).orElse(0L);
                log.trace("[{}][{}] Updating key: {} with ts: {}", serviceId, cmdId, k, ts);
                if (!Aggregation.NONE.equals(getCurrentAggregation()) && ts < endTs) {
                    ts = endTs;
                }
                keyStates.put(k, ts);
            });
        }
        return createTsSub(entityData, subIdx, latestValues, startTs, endTs, keyStates);
    }

    private TbTimeSeriesSubscription createTsSub(EntityData entityData, int subIdx, boolean latestValues, long startTs, long endTs, Map<String, Long> keyStates) {
        return createTsSub(entityData, subIdx, latestValues, startTs, endTs, keyStates, latestValues);
    }

    private TbTimeSeriesSubscription createTsSub(EntityData entityData, int subIdx, boolean latestValues, long startTs, long endTs, Map<String, Long> keyStates, boolean resultToLatestValues) {
        log.trace("[{}][{}][{}] Creating time-series subscription for [{}] with keys: {}", serviceId, cmdId, subIdx, entityData.getEntityId(), keyStates);
        return TbTimeSeriesSubscription.builder()
                .serviceId(serviceId)
                .sessionId(sessionRef.getSessionId())
                .subscriptionId(subIdx)
                .tenantId(sessionRef.getSecurityCtx().getTenantId())
                .entityId(entityData.getEntityId())
                .updateProcessor((sub, subscriptionUpdate) -> sendWsMsg(sub.getSessionId(), subscriptionUpdate, EntityKeyType.TIME_SERIES, resultToLatestValues))
                .queryTs(createdTime)
                .allKeys(false)
                .keyStates(keyStates)
                .latestValues(latestValues)
                .startTime(startTs)
                .endTime(endTs)
                .build();
    }

    private void sendWsMsg(String sessionId, TelemetrySubscriptionUpdate subscriptionUpdate, EntityKeyType keyType) {
        sendWsMsg(sessionId, subscriptionUpdate, keyType, true);
    }

    private Map<String, Long> buildKeyStats(EntityData entityData, EntityKeyType keysType, List<EntityKey> subKeys, boolean latestValues) {
        Map<String, Long> keyStates = new HashMap<>();
        subKeys.forEach(key -> keyStates.put(key.getKey(), 0L));
        if (latestValues && entityData.getLatest() != null) {
            Map<String, TsValue> currentValues = entityData.getLatest().get(keysType);
            if (currentValues != null) {
                currentValues.forEach((k, v) -> {
                    if (subKeys.contains(new EntityKey(keysType, k))) {
                        log.trace("[{}][{}] Updating key: {} with ts: {}", serviceId, cmdId, k, v.getTs());
                        keyStates.put(k, v.getTs());
                    }
                });
            }
        }
        return keyStates;
    }

    abstract void sendWsMsg(String sessionId, TelemetrySubscriptionUpdate subscriptionUpdate, EntityKeyType keyType, boolean resultToLatestValues);

    /**
     * Returns current aggregation.
     * @return {@link Aggregation}
     */

    protected abstract Aggregation getCurrentAggregation();

}
