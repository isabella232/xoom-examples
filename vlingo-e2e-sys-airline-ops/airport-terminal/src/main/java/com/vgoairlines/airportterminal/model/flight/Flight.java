// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package com.vgoairlines.airportterminal.model.flight;

import io.vlingo.actors.Definition;
import io.vlingo.actors.Address;
import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;

import java.time.LocalDateTime;

public interface Flight {

  Completes<FlightState> openGate(final String number,
                                  final GateAssignment gateAssignment,
                                  final Equipment equipment,
                                  final Schedule schedule);

  static Completes<FlightState> openGate(final Stage stage,
                                         final String number,
                                         final GateAssignment gateAssignment,
                                         final Equipment equipment,
                                         final Schedule schedule) {
    final Address _address = stage.addressFactory().uniquePrefixedWith("g-");
    final Flight _flight = stage.actorFor(Flight.class, Definition.has(FlightEntity.class, Definition.parameters(_address.idString())), _address);
    return _flight.openGate(number, gateAssignment, equipment, schedule);
  }

  Completes<FlightState> arrive(final LocalDateTime arrivedOn);

  Completes<FlightState> startBoarding();

  Completes<FlightState> endBoarding();

  Completes<FlightState> depart(final LocalDateTime departedOn);

  Completes<FlightState> closeGate();

}