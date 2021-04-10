package io.vlingo.xoom.petclinic.infrastructure.persistence;

import io.vlingo.xoom.petclinic.infrastructure.Events;
import io.vlingo.xoom.petclinic.infrastructure.*;
import io.vlingo.xoom.petclinic.model.pet.*;

import io.vlingo.lattice.model.projection.Projectable;
import io.vlingo.lattice.model.projection.StateStoreProjectionActor;
import io.vlingo.symbio.Source;
import io.vlingo.symbio.store.state.StateStore;

/**
 * See
 * <a href="https://docs.vlingo.io/vlingo-lattice/projections#implementing-with-the-statestoreprojectionactor">
 *   StateStoreProjectionActor
 * </a>
 */
public class PetProjectionActor extends StateStoreProjectionActor<PetData> {

  private static final PetData Empty = PetData.empty();

  public PetProjectionActor() {
    this(QueryModelStateStoreProvider.instance().store);
  }

  public PetProjectionActor(final StateStore stateStore) {
    super(stateStore);
  }

  @Override
  protected PetData currentDataFor(final Projectable projectable) {
    return Empty;
  }

  @Override
  protected PetData merge(final PetData previousData, final int previousVersion, final PetData currentData, final int currentVersion) {

    if (previousVersion == currentVersion) return currentData;

    PetData merged = previousData;

    for (final Source<?> event : sources()) {
      switch (Events.valueOf(event.typeName())) {
        case PetRegistered: {
          final PetRegistered typedEvent = typed(event);
          final NameData name = NameData.from(typedEvent.name.value);
          final DateData birth = DateData.from(typedEvent.birth.value);
          final KindData kind = KindData.from(typedEvent.kind.animalTypeId);
          final OwnerData owner = OwnerData.from(typedEvent.owner.clientId);
          merged = PetData.from(typedEvent.id, name, birth, null, kind, owner);
          break;
        }

        case PetNameChanged: {
          final PetNameChanged typedEvent = typed(event);
          final NameData name = NameData.from(typedEvent.name.value);
          merged = PetData.from(typedEvent.id, name, previousData.birth, previousData.death, previousData.kind, previousData.owner);
          break;
        }

        case PetBirthRecorded: {
          final PetBirthRecorded typedEvent = typed(event);
          final DateData birth = DateData.from(typedEvent.birth.value);
          merged = PetData.from(typedEvent.id, previousData.name, birth, previousData.death, previousData.kind, previousData.owner);
          break;
        }

        case PetDeathRecorded: {
          final PetDeathRecorded typedEvent = typed(event);
          final DateData death = DateData.from(typedEvent.death.value);
          merged = PetData.from(typedEvent.id, previousData.name, previousData.birth, death, previousData.kind, previousData.owner);
          break;
        }

        case PetKindCorrected: {
          final PetKindCorrected typedEvent = typed(event);
          final KindData kind = KindData.from(typedEvent.kind.animalTypeId);
          merged = PetData.from(typedEvent.id, previousData.name, previousData.birth, previousData.death, kind, previousData.owner);
          break;
        }

        case PetOwnerChanged: {
          final PetOwnerChanged typedEvent = typed(event);
          final OwnerData owner = OwnerData.from(typedEvent.owner.clientId);
          merged = PetData.from(typedEvent.id, previousData.name, previousData.birth, previousData.death, previousData.kind, owner);
          break;
        }

        default:
          logger().warn("Event of type " + event.typeName() + " was not matched.");
          break;
      }
    }

    return merged;
  }
}