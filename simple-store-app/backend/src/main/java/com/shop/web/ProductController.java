package com.shop.web;

import com.shop.model.Product;
import com.shop.repo.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController @RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository repo;
    public ProductController(ProductRepository repo) {this.repo = repo; }

    @GetMapping public List<Product> list(@RequestParam(required = false) String q) {
        return (q == null || q.isBlank()) ? repo.findAll() : repo.findByNameContainingIgnoreCase(q);
    }

    @GetMapping("/{id}") public Product get(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Product create(@Valid @RequestBody Product p) {
        p.setId(null);
        return repo.save(p);
    }

    @PutMapping("/{id}") public Product update(@PathVariable Long id, @Valid @RequestBody Product p) {
        if (!repo.existsById(id) throw new ResponseStatusException(HttpStatus.NOT_FOUND));
        p.setId(id); return repo.save(p);
    }

    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) {repo.deleteById(id); }
}