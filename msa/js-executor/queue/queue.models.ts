///
/// Copyright © 2016-2026 The Thingsboard Authors
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///     http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.
///
/// Abstraction over Kafka (or future queue backends) for js-executor.
///

export interface IQueue {
    /** Backend name for logging (e.g. {@code kafka}). */
    name: string;
    /** Connect consumers/producers and subscribe to request topic. */
    init(): Promise<void>;
    /** Publish JS response to {@code responseTopic} with Kafka headers. */
    send(responseTopic: string, msgKey: string, rawResponse: Buffer, headers: any): Promise<any>;
    /** Disconnect and release clients. */
    destroy(): Promise<void>;
}
