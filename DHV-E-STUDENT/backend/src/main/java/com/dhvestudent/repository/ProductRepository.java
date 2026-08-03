package com.dhvestudent.repository;

import com.dhvestudent.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:condition IS NULL OR p.conditionType = :condition) " +
           "AND (:search IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "     OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY p.createdAt DESC")
    Page<Product> findActiveProducts(@Param("categoryId") Long categoryId,
                                     @Param("condition") Product.ConditionType condition,
                                     @Param("search") String search,
                                     Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' AND p.seller.id = :sellerId ORDER BY p.createdAt DESC")
    List<Product> findBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.status = 'ACTIVE'")
    long countActiveProducts();

    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' AND p.conditionType IN ('USED', 'SECONDHAND') ORDER BY p.createdAt DESC")
    List<Product> findSecondhandProducts(Pageable pageable);
}
