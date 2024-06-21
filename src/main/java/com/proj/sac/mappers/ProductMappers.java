package com.proj.sac.mappers;

import com.proj.sac.entity.Product;
import com.proj.sac.enums.ProductAvailability;
import com.proj.sac.requestdto.ProductRequest;
import org.springframework.stereotype.Component;

@Component
public class ProductMappers {
    public Product mapToProduct(ProductRequest productRequest, int sellerId, ProductAvailability availability) {
        return Product.builder()
                .productName(productRequest.getProductName())
                .productDescription(productRequest.getProductDescription())
                .productPrice(productRequest.getProductPrice())
                .productQuantity(productRequest.getProductQuantity())
                .productAvailability(availability)
                .sellerId(sellerId)
                .build();
    }
}
