package com.altech.store.service;

import com.altech.store.dto.Receipt;
import com.altech.store.entity.Basket;
import com.altech.store.entity.BasketItem;
import com.altech.store.entity.Customer;
import com.altech.store.entity.Product;
import com.altech.store.exception.InsufficientStockException;
import com.altech.store.repository.BasketRepository;
import com.altech.store.repository.CustomerRepository;
import com.altech.store.repository.DealRepository;
import com.altech.store.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BasketServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BasketRepository basketRepository;

    @Mock
    private DealService dealService;

    @InjectMocks
    private BasketServiceImpl basketService;

    private Customer customer;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customer = new Customer();
        customer.setId(1L);

        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("1000.00"));
        product.setStock(10);
    }

    @Test
    void addItem_shouldAddNewItemToNewBasket() throws InsufficientStockException {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);
        when(basketRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        basketService.addItem(1L, 1L, 2);

        assertNotNull(customer.getBasket());
        assertEquals(1, customer.getBasket().getItems().size());
        assertEquals(8, product.getStock());
        verify(productRepository).save(product);
        verify(basketRepository).save(customer.getBasket());
    }

    @Test
    void addItem_shouldThrowIfInsufficientStock() {
        product.setStock(1);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class, () -> basketService.addItem(1L, 1L, 5));
    }

    @Test
    void removeItem_shouldRemoveItemAndRestoreStock() {
        Basket basket = new Basket();
        BasketItem item = new BasketItem();
        item.setProduct(product);
        item.setQuantity(2);
        item.setBasket(basket);
        basket.setItems(new ArrayList<>(List.of(item)));
        customer.setBasket(basket);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        basketService.removeItem(1L, 1L, 2);

        assertTrue(basket.getItems().isEmpty());
        assertEquals(12, product.getStock());
        verify(productRepository).save(product);
        verify(basketRepository).save(basket);
    }

    @Test
    void getReceipt_shouldReturnCorrectReceipt() {
        Basket basket = new Basket();
        BasketItem item = new BasketItem();
        item.setProduct(product);
        item.setQuantity(2);
        basket.setItems(List.of(item));
        customer.setBasket(basket);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(dealService.getAllActiveDeals()).thenReturn(Collections.emptyList());


        Receipt receipt = basketService.getReceipt(1L);

        assertNotNull(receipt);
        assertEquals(1, receipt.getItems().size());
        assertEquals(new BigDecimal("2000.00"), receipt.getTotal());
    }
}
