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
package org.thingsboard.server.dao.cassandra.guava;

import com.datastax.oss.driver.api.core.cql.AsyncResultSet;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.cql.Statement;
import com.datastax.oss.driver.api.core.cql.SyncCqlSession;
import com.datastax.oss.driver.api.core.session.Session;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import com.datastax.oss.driver.internal.core.cql.DefaultPrepareRequest;
import com.datastax.oss.driver.internal.core.cql.SinglePageResultSet;
import com.google.common.util.concurrent.ListenableFuture;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.concurrent.ExecutionException;

/**
 * guava session contract for the DAO layer.
 */

public interface GuavaSession extends Session, SyncCqlSession {

    GenericType<ListenableFuture<AsyncResultSet>> ASYNC =
            new GenericType<ListenableFuture<AsyncResultSet>>() {};

    GenericType<ListenableFuture<PreparedStatement>> ASYNC_PREPARED =
            new GenericType<ListenableFuture<PreparedStatement>>() {};

    @NonNull
    /**
     * Executes the requested data.
     *
     * @param statement statement ({@link Statement})
     * @return {@link ResultSet}
     */
    default ResultSet execute(@NonNull Statement<?> statement) {
        AsyncResultSet firstPage = getSafe(this.executeAsync(statement));
        if (firstPage.hasMorePages()) {
            /**
             * Guava multi page result set.
             *
             * @return the return new value
             */
            return new GuavaMultiPageResultSet(this, statement, firstPage);
        } else {
            /**
             * Single page result set.
             *
             * @return the return new value
             */
            return new SinglePageResultSet(firstPage);
        }
    }

    /**
     * Executes async.
     *
     * @param statement statement ({@link Statement})
     * @return future completing with {@link AsyncResultSet}
     */
    default ListenableFuture<AsyncResultSet> executeAsync(Statement<?> statement) {
        return this.execute(statement, ASYNC);
    }

    /**
     * Executes async.
     *
     * @param statement statement ({@link String})
     * @return future completing with {@link AsyncResultSet}
     */
    default ListenableFuture<AsyncResultSet> executeAsync(String statement) {
        return this.executeAsync(SimpleStatement.newInstance(statement));
    }

    /**
     * Prepare async.
     *
     * @param statement statement ({@link SimpleStatement})
     * @return future completing with {@link PreparedStatement}
     */
    default ListenableFuture<PreparedStatement> prepareAsync(SimpleStatement statement) {
        return this.execute(new DefaultPrepareRequest(statement), ASYNC_PREPARED);
    }

    /**
     * Prepare async.
     *
     * @param statement statement ({@link String})
     * @return future completing with {@link PreparedStatement}
     */
    default ListenableFuture<PreparedStatement> prepareAsync(String statement) {
        return this.prepareAsync(SimpleStatement.newInstance(statement));
    }

    /**
     * Returns safe.
     *
     * @param future future ({@link ListenableFuture})
     * @return {@link AsyncResultSet}
     */
    static AsyncResultSet getSafe(ListenableFuture<AsyncResultSet> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            /**
             * Illegal state exception.
             *
             * @return the throw new value
             */
            throw new IllegalStateException(e);
        }
    }
}
