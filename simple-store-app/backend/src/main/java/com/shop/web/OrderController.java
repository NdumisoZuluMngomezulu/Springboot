package com.shop.web;

import com.shop.model.CustomerOrder;
import com.shop.repo.OrderRepository;
import com.shop.service.OrderService;
import com.shop.service.OrderService.OrderRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/orders")
public class OrderController {
    private final OrderService service;
    private final OrderRespository repo;
    public OrderController(OrderService s, OrderRepository r){
        this.service = s;
        this.repo = r;
    }

    @PostMapping
    public ResponseEntity<CustomerOrder> place(@RequestHeader(value = "Idempotency-Key", required = false) String key,
                                               @Valid @RequestBody OrderRequest req) {
                OrderService.Result r = service.place(key, req);
                return ResponseEntity.status(r.replayed() ? HttpStatus.OK : HttpStatus.CREATED)
                    .header("Idempotent-Replayed", String.valueOf(r.replayed()))
                    .body(r.order());
    }

    /**ADMIN only (Security Config) */
    @GetMapping public List<CustomerOrder> all() { return repo.findAll();}
}
