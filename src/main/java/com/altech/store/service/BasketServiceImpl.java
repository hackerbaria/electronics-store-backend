package com.altech.store.service;

import com.altech.store.dto.DealType;
import com.altech.store.dto.Receipt;
import com.altech.store.entity.*;
import com.altech.store.exception.InsufficientStockException;
import com.altech.store.exception.ResourceNotFoundException;
import com.altech.store.repository.BasketRepository;
import com.altech.store.repository.CustomerRepository;
import com.altech.store.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BasketServiceImpl implements BasketService {
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final BasketRepository basketRepository;
    private final DealService dealService;

    @Override
    public void addItem(Long customerId, Long productId, int quantity) throws InsufficientStockException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + customerId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        if (product.getStock() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product ID " + productId);
        }

        product.setStock(product.getStock() - quantity);
        Basket basket = customer.getBasket();
        if (basket == null) {
            basket = new Basket();
            basket.setCustomer(customer);
            basket.setItems(new ArrayList<>());
            customer.setBasket(basket);
        }

        Basket finalBasket = basket;
        BasketItem item = finalBasket.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseGet(() -> {
                    BasketItem newItem = new BasketItem();
                    newItem.setProduct(product);
                    newItem.setQuantity(0);
                    newItem.setBasket(finalBasket);
                    finalBasket.getItems().add(newItem);
                    return newItem;
                });

        item.setQuantity(item.getQuantity() + quantity);

        productRepository.save(product);
        basketRepository.save(basket); // this also saves BasketItems via cascade
    }

    @Override
    public void removeItem(Long customerId, Long productId, int quantity) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        Basket basket = customer.getBasket();
        BasketItem item = basket.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found in basket"));

        int newQty = item.getQuantity() - quantity;

        if (newQty <= 0) {
            basket.getItems().remove(item);
        } else {
            item.setQuantity(newQty);
        }

        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
        basketRepository.save(basket);
    }

    @Override
    public Receipt getReceipt(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Basket basket = customer.getBasket();
        List<Receipt.Line> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        List<Deal> deals = dealService.getAllActiveDeals();

        for (BasketItem item : basket.getItems()) {
            Product product = item.getProduct();
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
            BigDecimal price = product.getPrice();
            BigDecimal subtotal = price.multiply(quantity);

            // Placeholder for discount calculation (currently zero)
            BigDecimal discount = BigDecimal.ZERO;

            // Get the applicable deal for the product
            Deal deal = deals.stream()
                    .filter(d -> d.getProductIds().contains(product.getId()))
                    .findFirst()
                    .orElse(null);

            if (deal != null) {
                switch (deal.getDealType()) {
                    case BUY_1_GET_2ND_50_PERCENT -> {
                        int eligibleDiscountedItems = quantity.intValue() / 2;
                        BigDecimal discountPerItem = price.multiply(
                                BigDecimal.valueOf(deal.getDiscountValue())
                                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                        );
                        discount = discountPerItem.multiply(BigDecimal.valueOf(eligibleDiscountedItems));
                    }

                    case PERCENTAGE_DISCOUNT -> {
                        BigDecimal percentOff = BigDecimal.valueOf(deal.getDiscountValue())
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                        discount = subtotal.multiply(percentOff);
                    }

                    case FIXED_AMOUNT_DISCOUNT -> {
                        BigDecimal fixedDiscountPerItem = BigDecimal.valueOf(deal.getDiscountValue());
                        discount = fixedDiscountPerItem.multiply(quantity);
                    }


                    // Add more deal types here as needed
                }
            }


            items.add(new Receipt.Line(
                    product.getName(),
                    item.getQuantity(),
                    price,
                    subtotal,
                    discount
            ));

            total = total.add(subtotal.subtract(discount));
        }

        return new Receipt(items, total);
    }
}
