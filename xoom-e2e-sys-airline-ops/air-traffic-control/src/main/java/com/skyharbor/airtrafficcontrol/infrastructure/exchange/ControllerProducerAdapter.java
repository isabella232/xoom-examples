// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package com.skyharbor.airtrafficcontrol.infrastructure.exchange;

import io.vlingo.xoom.common.version.SemanticVersion;
import io.vlingo.xoom.lattice.exchange.ExchangeAdapter;
import io.vlingo.xoom.lattice.exchange.rabbitmq.Message;
import io.vlingo.xoom.lattice.model.IdentifiedDomainEvent;

public class ControllerProducerAdapter implements ExchangeAdapter<IdentifiedDomainEvent, IdentifiedDomainEvent, Message> {

  private static final String SCHEMA_PREFIX = "SkyHarborPHX:groundops:com.skyharbor.airtrafficcontrol";

  private final DomainEventMapper mapper = new DomainEventMapper();

  @Override
  public IdentifiedDomainEvent fromExchange(final Message exchangeMessage) {
    return mapper.externalToLocal(exchangeMessage);
  }

  @Override
  public Message toExchange(final IdentifiedDomainEvent event) {
    final Message message = mapper.localToExternal(event);
    message.messageParameters.typeName(resolveFullSchemaReference(event));
    return message;
  }

  @Override
  public boolean supports(final Object exchangeMessage) {
    if(!exchangeMessage.getClass().equals(Message.class)) {
      return false;
    }
    final String schemaName = ((Message) exchangeMessage).messageParameters.typeName();
    return schemaName.startsWith(SCHEMA_PREFIX);
  }

  private String resolveFullSchemaReference(final IdentifiedDomainEvent event) {
    final String semanticVersion = SemanticVersion.toString(event.sourceTypeVersion);
    return String.format("%s:%s:%s", SCHEMA_PREFIX, event.getClass().getSimpleName(), semanticVersion);
  }

}