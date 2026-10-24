package com.shop.repo;

import com.shop.model.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String q);

    /** SELECT ... FOR UPDATE: serliaze concurrent orders on the same product so stock can't be oversold */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);
}

// public interface ProductRepository extends JpaRepository<Product, Long> {
//     List<Product> findByNameContainingIgnoreCase(String q);

//     /** SELECT ... FOR UPDATE: serialises concurrent orders on the same product so stock can't be oversold. */
//     @Lock(LockModeType.PESSIMISTIC_WRITE)
//     @Query("select p from Product p where p.id = :id")
//     Optional<Product> findByIdForUpdate(@Param("id") Long id);
// }
