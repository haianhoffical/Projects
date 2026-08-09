package com.dhvestudent.service;

import com.dhvestudent.dto.*;
import com.dhvestudent.entity.*;
import com.dhvestudent.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Service
public class ProductService {

    @Autowired private ProductRepository productRepository;
    @Autowired private ProductImageRepository imageRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public PageResponse<Product> getProducts(Long categoryId, Product.ConditionType condition,
                                              String search, String sort, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        if ("price-asc".equals(sort)) pageable = PageRequest.of(page, size, Sort.by("price").ascending());
        if ("price-desc".equals(sort)) pageable = PageRequest.of(page, size, Sort.by("price").descending());

        Page<Product> result = productRepository.findActiveProducts(categoryId, condition, search, pageable);
        return PageResponse.<Product>builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        Product p = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        p.setViewCount(p.getViewCount() + 1);
        productRepository.save(p);
        return p;
    }

    @Transactional
    public Product createProduct(Long sellerId, ProductRequest req, List<MultipartFile> images) {
        User seller = userRepository.findById(sellerId).orElseThrow();
        Category cat = categoryRepository.findById(req.getCategoryId()).orElseThrow();

        Product product = Product.builder()
                .seller(seller)
                .category(cat)
                .title(req.getTitle())
                .description(req.getDescription())
                .price(req.getPrice())
                .originalPrice(req.getOriginalPrice())
                .conditionType(req.getConditionType())
                .location(req.getLocation())
                .status(Product.ProductStatus.ACTIVE)
                .build();
        productRepository.save(product);

        if (images != null) {
            for (int i = 0; i < images.size(); i++) {
                String url = fileStorageService.storeFile(images.get(i), "products");
                if (url != null) {
                    ProductImage img = ProductImage.builder()
                            .product(product)
                            .imageUrl(url)
                            .isPrimary(i == 0)
                            .displayOrder(i)
                            .build();
                    imageRepository.save(img);
                    product.getImages().add(img);
                }
            }
        }
        return product;
    }

    @Transactional
    public void deleteProduct(Long id, Long sellerId) {
        Product p = productRepository.findById(id).orElseThrow();
        if (!p.getSeller().getId().equals(sellerId)) throw new RuntimeException("Không có quyền xóa");
        p.setStatus(Product.ProductStatus.HIDDEN);
        productRepository.save(p);
    }

    @Transactional(readOnly = true)
    public List<Product> getSecondhandProducts() {
        return productRepository.findSecondhandProducts(PageRequest.of(0, 5));
    }
}
