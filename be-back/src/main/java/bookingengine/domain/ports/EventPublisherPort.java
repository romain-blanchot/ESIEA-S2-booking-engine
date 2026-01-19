package bookingengine.domain.ports;

import bookingengine.domain.events.ChambreCreatedEvent;
import bookingengine.domain.events.PrixCalculatedEvent;
import bookingengine.domain.events.SaisonCreatedEvent;

public interface EventPublisherPort {

    void publish(ChambreCreatedEvent event);

    void publish(SaisonCreatedEvent event);

    void publish(PrixCalculatedEvent event);
}
