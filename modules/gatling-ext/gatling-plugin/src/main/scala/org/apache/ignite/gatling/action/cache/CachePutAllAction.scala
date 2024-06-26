/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.gatling.action.cache

import scala.collection.SortedMap

import io.gatling.core.action.Action
import io.gatling.core.session.Expression
import io.gatling.core.session.Session
import io.gatling.core.structure.ScenarioContext
import org.apache.ignite.gatling.action.CacheAction

/**
 * Action for the putAll Ignite operation.
 *
 * @tparam K Type of the cache key.
 * @tparam V Type of the cache value.
 * @param requestName Name of the request.
 * @param cacheName Name of cache.
 * @param map Collection of cache entry keys and values.
 * @param keepBinary True if it should operate with binary objects.
 * @param async True if async API should be used.
 * @param next Next action from chain to invoke upon this one completion.
 * @param ctx Scenario context.
 */
class CachePutAllAction[K, V](
    requestName: String,
    cacheName: String,
    map: Expression[SortedMap[K, V]],
    keepBinary: Boolean,
    async: Boolean,
    next: Action,
    ctx: ScenarioContext
) extends CacheAction[K, V]("putAll", requestName, ctx, next, cacheName, keepBinary, async) {

    override protected def execute(session: Session): Unit = withSessionCheck(session) {
        for {
            resolvedMap <- map(session)

            CacheActionParameters(cacheApi, _) <- resolveCacheParameters(session)
        } yield {
            logger.debug(s"session user id: #${session.userId}, before $request")

            val func = if (async) cacheApi.putAllAsync _ else cacheApi.putAll _

            call(func(resolvedMap), session)
        }
    }
}
