package ch.wisv.events.core.service;

import ch.wisv.events.ServiceTest;
import ch.wisv.events.api.request.ProductDto;
import ch.wisv.events.core.exception.normal.ProductInvalidException;
import ch.wisv.events.core.exception.normal.ProductNotFoundException;
import ch.wisv.events.core.exception.runtime.ProductAlreadyLinkedException;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.ProductRepository;
import ch.wisv.events.core.service.product.ProductService;
import ch.wisv.events.core.service.product.ProductServiceImpl;
import ch.wisv.events.core.util.VatRate;
import com.google.common.collect.ImmutableList;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProductServiceImplTest extends ServiceTest {

    /**
     * Mock of ProductRepository
     */
    @Mock
    private ProductRepository productRepository;

    /**
     * ProductService
     */
    private ProductService productService;

    /**
     * Default Product
     */
    private Product product;

    /**
     * Method setUp
     */
    @Before
    public void setUp() {
        productService = new ProductServiceImpl(productRepository);
        product = new Product(
                "Product",
                "Description",
                1.d,
                VatRate.VAT_HIGH,
                100,
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1)
        );
        product.setMaxSoldPerCustomer(1);
    }

    /**
     * Method tearDown
     */
    @After
    public void tearDown() {
        product = null;
    }

    /**
     * Test get all product method
     */
    @Test
    public void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(Collections.singletonList(product));

        assertEquals(Collections.singletonList(product), productService.getAllProducts());
    }

    /**
     * Test get all product method
     */
    @Test
    public void testGetAllProductsEmpty() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        assertEquals(Collections.emptyList(), productService.getAllProducts());
    }

    /**
     * Method testGetAvailableProducts ...
     */
    @Test
    public void testGetAvailableProducts() {
        product.setSellStart(LocalDateTime.now().minusHours(10));
        when(productRepository.findAllBySellStartBefore(any(LocalDateTime.class))).thenReturn(Collections.singletonList(product));

        assertEquals(Collections.singletonList(product), productService.getAvailableProducts());
    }

    /**
     * Method testGetAvailableProducts ...
     */
    @Test
    public void testGetAvailableProductsEmpty() {
        product.setSold(100);
        product.setMaxSold(100);
        when(productRepository.findAllBySellStartBefore(any(LocalDateTime.class))).thenReturn(Collections.singletonList(product));

        assertEquals(Collections.emptyList(), productService.getAvailableProducts());
    }

    /**
     * Test get product by key..
     */
    @Test
    public void testGetByKey() throws Exception {
        when(productRepository.findByKey(product.getKey())).thenReturn(Optional.of(product));

        assertEquals(product, productService.getByKey(product.getKey()));
    }

    /**
     * Test get product by key empty..
     */
    @Test
    public void testGetByKeyEmpty() throws Exception {
        thrown.expect(ProductNotFoundException.class);
        thrown.expectMessage("Product with key " + product.getKey() + " not found!");

        when(productRepository.findByKey(product.getKey())).thenReturn(Optional.empty());
        productService.getByKey(product.getKey());
    }

    /**
     * Test update..
     */
    @Test
    public void testUpdate() throws Exception {
        when(productRepository.findByKey(product.getKey())).thenReturn(Optional.of(product));

        productService.update(product);
        verify(productRepository, times(1)).save(product);
    }

    /**
     * Test update empty
     */
    @Test
    public void testUpdateEmpty() throws Exception {
        thrown.expect(ProductNotFoundException.class);
        thrown.expectMessage("Product with key " + product.getKey() + " not found!");

        when(productRepository.findByKey(product.getKey())).thenReturn(Optional.empty());

        productService.update(product);
    }

    /**
     * Test create from a ProductDto.
     */
    @Test
    public void testCreateDto() throws Exception {
        ProductDto productDto = new ProductDto();
        productDto.setTitle("Tile");
        productDto.setCost(1.d);
        productDto.setVatRate(VatRate.VAT_HIGH);
        productDto.setDescription("Test");
        productDto.setMaxSold(1);
        productDto.setMaxSoldPerCustomer(1);

        when(productRepository.saveAndFlush(any(Product.class))).thenReturn(new Product(productDto));

        Product product = productService.create(productDto);
        verify(productRepository, times(1)).saveAndFlush(any(Product.class));
        assertEquals(productDto.getTitle(), product.getTitle());
        assertEquals(productDto.getCost(), product.getCost());
        assertEquals(productDto.getVatRate(), product.getVatRate());
        assertEquals(productDto.getDescription(), product.getDescription());
        assertEquals(productDto.getMaxSold(), product.getMaxSold());
        assertEquals(productDto.getMaxSoldPerCustomer(), product.getMaxSoldPerCustomer());
    }

    /**
     * Test create.
     */
    @Test
    public void testCreate() throws Exception {
        productService.create(product);
        verify(productRepository, times(1)).saveAndFlush(product);
    }

    /**
     * Test create when sell start not set.
     */
    @Test
    public void testCreateSellStartNotSet() throws Exception {
        Product product1 = mock(Product.class);

        when(product1.getTitle()).thenReturn("Title");
        when(product1.getSellEnd()).thenReturn(null);
        when(product1.getCost()).thenReturn(0.d);
        when(product1.getMaxSoldPerCustomer()).thenReturn(1);
        when(product1.getSellStart()).thenReturn(null).thenReturn(LocalDateTime.now());

        productService.create(product1);

        verify(product1, times(1)).setSellStart(any(LocalDateTime.class));
    }

    /**
     * Test create when title is invalid.
     */
    @Test
    public void testCreateInvalidTitle() throws Exception {
        product.setTitle(null);

        thrown.expect(ProductInvalidException.class);
        thrown.expectMessage("Title is required, and therefore should be filled in!");

        productService.create(product);

        verify(productRepository, times(0)).saveAndFlush(product);
    }

    /**
     * Test create when sell end is before sell start.
     */
    @Test
    public void testCreateInvalidSellEndBeforeSellStart() throws Exception {
        product.setSellEnd(LocalDateTime.now().minusDays(1));
        product.setSellStart(LocalDateTime.now());

        thrown.expect(ProductInvalidException.class);
        thrown.expectMessage("Starting date for selling should be before the ending time");

        productService.create(product);

        verify(productRepository, times(0)).saveAndFlush(product);
    }

    /**
     * Test create when cost is invalid.
     */
    @Test
    public void testCreateInvalidCost() throws Exception {
        product.setCost(null);

        thrown.expect(ProductInvalidException.class);
        thrown.expectMessage("Price is required, and therefore should be filled in!");

        productService.create(product);

        verify(productRepository, times(0)).saveAndFlush(product);
    }

    /**
     * Test create when same sub product has been added twice.
     */
    @Test
    public void testCreateInvalidSameProduct() throws Exception {
        Product otherProduct = new Product();
        product.setProducts(ImmutableList.of(otherProduct, otherProduct));

        thrown.expect(ProductInvalidException.class);
        thrown.expectMessage("It is not possible to add the same product twice or more!");

        productService.create(product);

        verify(productRepository, times(0)).saveAndFlush(product);
    }

    /**
     * Test create when max sold per Customer is invalid.
     */
    @Test
    public void testCreateInvalidMaxSoldPerCustomer() throws Exception {
        product.setMaxSoldPerCustomer(26);

        thrown.expect(ProductInvalidException.class);
        thrown.expectMessage("Max sold per customer should be between 1 and 25!");

        productService.create(product);

        verify(productRepository, times(0)).saveAndFlush(product);
    }

    /**
     * Test create.
     */
    @Test
    public void testCreateLinkProducts() throws Exception {
        Product mock = mock(Product.class);
        product.setProducts(ImmutableList.of(mock));
        productService.create(product);

        verify(mock, times(1)).setLinked(true);
        verify(productRepository, times(1)).save(mock);
        verify(productRepository, times(1)).saveAndFlush(product);
    }

    /**
     * Test delete
     */
    @Test
    public void testDelete() {
        productService.delete(product);
        verify(productRepository, times(1)).delete(product);
    }

    /**
     * Test delete when the product is added to an event
     */
    @Test
    public void testDeleteProductAddedToEvent() {
        thrown.expect(ProductAlreadyLinkedException.class);
        thrown.expectMessage("Product is already added to an Event");

        product.setLinked(true);

        productService.delete(product);
    }

}