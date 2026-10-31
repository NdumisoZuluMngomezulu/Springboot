package com.shop.config;

import com.shop.model.Product;
import com.shop.repo.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class DataSeeder implements CommandLineRunner {
    private final ProductRepository repo;
    public DataSeeder(ProductRepository repo) { this.repo = repo; }

    @Override public void run(String... args) {
        if (repo.count() > 0) return;
        add("Stoneware Mug", "Hand-glazed 350 ml mug, dishwasher safe.", "18.00", 40, "Kitchen");
        add("Linen Apron", "Washed linen with deep front pocket.", "42.50", 25, "Kitchen");
        add("Walnut Cutting Board", "Reversible end-grain board, 40 x 28 cm.", "79.00", 12, "Kitchen");
        add("Wool Throw", "Merino blend throw, 130 x 180 cm.", "120.00", 8, "Home");
        add("Brass Desk Lamp", "Adjustable arm, warm 2700K bulb included.", "95.00", 15, "Home");
        add("Canvas Tote", "Heavy cotton tote with inner zip pocket.", "29.00", 60, "Accessories");
    }
    private void add(String n, String d, String price, int stock, String cat) {
        Product p = new Product();
        p.setName(n); p.setDescription(d); p.setPrice(new BigDecimal(price)); p.setStock(stock); p.setCategory(cat);
        p.setImageUrl("https://picsum.photos/seed/" + n.replace(' ', '-') + "/600/450");
        repo.save(p);
    }
}