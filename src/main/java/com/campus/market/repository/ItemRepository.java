package com.campus.market.repository;

import com.campus.market.entity.Item;
import com.campus.market.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByUserOrderByCreatedAtDesc(User user);

    List<Item> findByIsDemoTrue();

    void deleteByIsDemoTrue();

    @Query("SELECT i FROM Item i WHERE " +
           "(:category IS NULL OR :category = '' OR i.category = :category) AND " +
           "(:status IS NULL OR :status = '' OR i.status = :status) AND " +
           "(:minPrice IS NULL OR i.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR i.price <= :maxPrice) AND " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           " LOWER(i.itemName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(i.category) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           " LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Item> searchItems(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("status") String status,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );
}

