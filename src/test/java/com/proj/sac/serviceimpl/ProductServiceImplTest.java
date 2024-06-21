package com.proj.sac.serviceimpl;

import com.proj.sac.entity.Product;
import com.proj.sac.entity.Seller;
import com.proj.sac.enums.ProductAvailability;
import com.proj.sac.exception.ProductNotFoundException;
import com.proj.sac.exception.SellerNotFoundException;
import com.proj.sac.mappers.ProductMappers;
import com.proj.sac.repo.ProductRepo;
import com.proj.sac.repo.SellerRepo;
import com.proj.sac.requestdto.ProductRequest;
import com.proj.sac.requestdto.ProductRequestDto;
import com.proj.sac.util.ResponseStructure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class ProductServiceImplTest {

    @MockBean
    private ProductRepo productRepo;

    @MockBean
    private SellerRepo sellerRepo;

    @MockBean
    private ProductMappers productMappers;

//    @MockBean
    private MongoTemplate mongoTemplate;

    @MockBean
    private ResponseStructure<Product> productStructure;

    @MockBean
    private ResponseStructure<List<Product>> productListStructure;

    @Autowired
    private ProductServiceImpl productService;

    @Test
    void testCreateProduct_Success() {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductQuantity(120);

        Seller seller = new Seller();
        seller.setUserId(1);

        Product product = new Product();
        product.setProductId("dgaskjdljaskd");

        when(sellerRepo.findById(1)).thenReturn(Optional.of(seller));
        when(productMappers.mapToProduct(any(ProductRequest.class), anyInt(), any(ProductAvailability.class))).thenReturn(product);
        when(productRepo.save(any(Product.class))).thenReturn(product);

        ResponseEntity<ResponseStructure<Product>> response = productService.createProduct(productRequest, 1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productStructure).setData(product);
        verify(productStructure).setMessage("Product Added !!!");
        verify(productStructure).setStatusCode(HttpStatus.CREATED.value());
        verify(productRepo, times(1)).save(product);
    }

    @Test
    void testCreateProduct_SellerNotFound() {
        ProductRequest productRequest = new ProductRequest();

        when(sellerRepo.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(SellerNotFoundException.class, () -> {
            productService.createProduct(productRequest, 1);
        });

        assertEquals("Seller not present !!!", exception.getMessage());
    }

    @Test
    void testGetAllProducts_Success() {
        Seller seller = new Seller();
        seller.setUserId(1);

        List<Product> productList = List.of(new Product(), new Product());

        when(sellerRepo.findById(1)).thenReturn(Optional.of(seller));
        when(productRepo.findBySellerId(1)).thenReturn(productList);

        ResponseEntity<ResponseStructure<List<Product>>> response = productService.getAllProducts(1);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(productListStructure).setData(productList);
        verify(productListStructure).setMessage("List of products fetched");
        verify(productListStructure).setStatusCode(HttpStatus.FOUND.value());
    }

    @Test
    void testGetAllProducts_SellerNotFound() {
        when(sellerRepo.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(SellerNotFoundException.class, () -> {
            productService.getAllProducts(1);
        });

        assertEquals("Seller not found", exception.getMessage());
    }

    @Test
    void testGetAllProducts_ProductsNotFound() {
        Seller seller = new Seller();
        seller.setUserId(1);

        when(sellerRepo.findById(1)).thenReturn(Optional.of(seller));
        when(productRepo.findBySellerId(1)).thenReturn(null);

        Exception exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.getAllProducts(1);
        });

        assertEquals("Products not found", exception.getMessage());
    }

//    @Test
//    void testFindAllProducts_Success() {
//        ProductRequestDto productRequestDto = new ProductRequestDto();
//        productRequestDto.setProductName("Test");
//
//        List<Product> productList = List.of(new Product(), new Product());
//
//        Criteria criteria = new Criteria();
//        criteria.and("productName").regex("Test", "i");
//
//        when(mongoTemplate.find(any(Query.class), eq(Product.class))).thenReturn(productList);
//
//        ResponseEntity<ResponseStructure<List<Product>>> response = productService.findAllProducts(productRequestDto);
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        verify(productListStructure).setData(productList);
//        verify(productListStructure).setMessage("All Products Fetched");
//        verify(productListStructure).setStatusCode(HttpStatus.FOUND.value());
//    }
}