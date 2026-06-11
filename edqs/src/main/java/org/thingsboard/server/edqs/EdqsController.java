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
package org.thingsboard.server.edqs;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thingsboard.server.edqs.state.EdqsStateService;

/**
 * HTTP readiness probe for the EDQS microservice.
 *
 * <p>Business entity queries are served over Kafka ({@link org.thingsboard.server.edqs.processor.EdqsProcessor}), not REST. This controller exposes {@code GET /api/edqs/ready} for Kubernetes/Docker health checks.
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/edqs")
public class EdqsController {

    private final EdqsStateService edqsStateService;

    /**
     * Returns true when all assigned Kafka partitions have been restored and the index is queryable.
     *
     * <p>HTTP: GET {@code /api/edqs/ready}
     *
     * @return {@link ResponseEntity} — 200 when ready, 400 while state is still replaying
     * @throws Exception if an unexpected error occurs during processing
     */
    @GetMapping("/ready")
    public ResponseEntity<Void> isReady() {
        if (edqsStateService.isReady()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}
