/*
 * Copyright 2018 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package tech.pegasys.pantheon.ethereum.jsonrpc.websocket.subscription.logs;

import tech.pegasys.pantheon.ethereum.jsonrpc.internal.filter.LogsQuery;
import tech.pegasys.pantheon.ethereum.jsonrpc.internal.parameters.FilterParameter;
import tech.pegasys.pantheon.ethereum.jsonrpc.websocket.subscription.Subscription;
import tech.pegasys.pantheon.ethereum.jsonrpc.websocket.subscription.request.SubscriptionType;

public class LogsSubscription extends Subscription {

  private final FilterParameter filterParameter;

  public LogsSubscription(
      final Long subscriptionId, final String connectionId, final FilterParameter filterParameter) {
    super(subscriptionId, connectionId, SubscriptionType.LOGS, Boolean.FALSE);
    this.filterParameter = filterParameter;
  }

  public LogsQuery getLogsQuery() {
    return new LogsQuery.Builder()
        .addresses(filterParameter.getAddresses())
        .topics(filterParameter.getTopics())
        .build();
  }
}
