package com.shop.repo;

import com.shop.model.CustomerOrder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface  OrderRepository extends JpaRepository<CustomerOrder, Long>{
    //**Fetch items in the same query: with open-in-view=false, la\y items can't be loaded duirng Json serialization */
    @Override 
    @EntityGraph(attributePaths = "items")
    List<CustomerOrder> findAll();

    @EntityGraph(attributePaths = "items")
    Optional<CustomerOrder> findByIdempotencyKey(String idempotentyKey);
}

