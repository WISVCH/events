package ch.wisv.events.webshop.service;

import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.product.Product;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * WebshopServiceImpl class.
 */
@Service
public class WebshopServiceImpl implements WebshopService {

    /**
     * Filter the products in a Event which are not sold now.
     *
     * @param event of type Event
     *
     * @return Event
     */
    @Override
    public Event filterEventProductNotSalable(Event event) {
        List<Product> salableProducts = event.getProducts().stream()
                .filter(this.isAfterSellStart())
                .filter(this.isBeforeSellEnd())
                .collect(Collectors.toList());
        event.setProducts(salableProducts);

        return event;
    }

    /**
     * Filter the products by events if they can be sold or not.
     *
     * @param events of type List
     *
     * @return List
     */
    @Override
    public List<Event> filterEventProductNotSalable(List<Event> events) {
        return events.stream().map(this::filterEventProductNotSalable)
                .filter(event -> event.getProducts().size() > 0 || event.hasExternalProductUrl())
                .collect(Collectors.toList());
    }

    /**
     * Check if Product is after sell start.
     *
     * @return Predicate
     */
    private Predicate<Product> isAfterSellStart() {
        return product -> {
            if (product.getSellStart() == null) {
                return true;
            } else {
                return LocalDateTime.now().isAfter(product.getSellStart());
            }
        };
    }

    /**
     * Check if Product is in sell interval.
     *
     * @return Predicate
     */
    private Predicate<Product> isBeforeSellEnd() {
        return product -> {
            if (product.getSellEnd() == null) {
                return true;
            } else {
                return LocalDateTime.now().isBefore(product.getSellEnd());
            }
        };
    }
}
