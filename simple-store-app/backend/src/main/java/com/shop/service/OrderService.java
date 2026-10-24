package com.shop.service;

import com.shop.model.*;
import com.shop.repo.*;

public class OrderService {
    
}

// package com.shop.service;

// import com.shop.model.*;
// import com.shop.repo.*;
// import jakarta.validation.Valid;
// import jakarta.validation.constraints.*;
// import org.springframework.dao.DataIntegrityViolationException;
// import org.springframework.dao.PessimisticLockingFailureException;
// import org.springframework.http.HttpStatus;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.PlatformTransactionManager;
// import org.springframework.transaction.support.TransactionTemplate;
// import org.springframework.web.server.ResponseStatusException;
// import java.math.BigDecimal;
// import java.nio.charset.StandardCharsets;
// import java.security.MessageDigest;
// import java.security.NoSuchAlgorithmException;
// import java.util.HexFormat;
// import java.util.List;
// import java.util.Locale;
// import java.util.Map;
// import java.util.Optional;
// import java.util.TreeMap;

// @Service
// public class OrderService {
//     public record Line(@NotNull Long productId, @Min(1) int quantity) {}
//     public record OrderRequest(@NotBlank String customerName, @Email @NotBlank String email,
//                                @NotBlank String address, @NotEmpty List<@Valid Line> items) {}
//     /** replayed = true when the key had already produced this order and we returned it instead of creating another. */
//     public record Result(CustomerOrder order, boolean replayed) {}

//     private static final java.util.regex.Pattern KEY = java.util.regex.Pattern.compile("[A-Za-z0-9_-]{8,64}");

//     private final ProductRepository products;
//     private final OrderRepository orders;
//     private final TransactionTemplate tx;

//     public OrderService(ProductRepository p, OrderRepository o, PlatformTransactionManager tm) {
//         products = p; orders = o; tx = new TransactionTemplate(tm);
//     }

//     /**
//      * Places an order exactly once per Idempotency-Key.
//      *  - Same key, same request  -> the original order is returned (replayed = true); no stock is touched.
//      *  - Same key, different one -> 422, because the client is reusing a key for something else.
//      *  - Two requests with the same key at the same time -> one creates the order, the other waits on the
//      *    unique index and then gets the first one's order.
//      * Only successful orders are remembered. A failed attempt (e.g. out of stock) rolls back completely,
//      * including the key, so the client can retry with the same key.
//      *
//      * Deliberately not @Transactional: the duplicate-key error has to be caught outside the transaction
//      * it poisons, so the transaction is opened explicitly below.
//      */
//     public Result place(String key, OrderRequest req) {
//         if (key == null || !KEY.matcher(key).matches())
//             throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//                 "Idempotency-Key header is required (8-64 characters: letters, digits, - or _).");

//         Map<Long, Long> wanted = merge(req);
//         String hash = fingerprint(req, wanted);

//         Optional<CustomerOrder> prior = orders.findByIdempotencyKey(key);
//         if (prior.isPresent()) return replay(prior.get(), hash);

//         try {
//             return new Result(tx.execute(status -> create(key, hash, req, wanted)), false);
//         } catch (DataIntegrityViolationException | PessimisticLockingFailureException e) {
//             // A concurrent request with the same key won the race (or we were picked as a deadlock victim).
//             Optional<CustomerOrder> winner = orders.findByIdempotencyKey(key);
//             if (winner.isPresent()) return replay(winner.get(), hash);
//             if (e instanceof PessimisticLockingFailureException)
//                 throw new ResponseStatusException(HttpStatus.CONFLICT, "Your order is still being processed. Please try again in a moment.");
//             throw e;
//         }
//     }

//     /**
//      * Prices and stock are always taken from the database, never from the client.
//      * The order row (with its unique key) is inserted FIRST, so a duplicate request blocks there before it can
//      * touch stock. Each product row is then locked (SELECT ... FOR UPDATE) before its stock is checked.
//      * Lines are merged per product and locked in ascending id order so concurrent orders can't deadlock.
//      */
//     private CustomerOrder create(String key, String hash, OrderRequest req, Map<Long, Long> wanted) {
//         CustomerOrder order = new CustomerOrder();
//         order.setCustomerName(req.customerName());
//         order.setEmail(req.email());
//         order.setAddress(req.address());
//         order.setTotal(BigDecimal.ZERO);
//         order.setIdempotencyKey(key);
//         order.setRequestHash(hash);
//         order = orders.saveAndFlush(order);

//         BigDecimal total = BigDecimal.ZERO;
//         for (Map.Entry<Long, Long> e : wanted.entrySet()) {
//             Product p = products.findByIdForUpdate(e.getKey()).orElseThrow(() ->
//                 new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown product " + e.getKey()));
//             long qty = e.getValue();
//             if (p.getStock() < qty)
//                 throw new ResponseStatusException(HttpStatus.CONFLICT, "Not enough stock for " + p.getName());
//             int q = (int) qty; // safe: qty <= stock, which is an int
//             p.setStock(p.getStock() - q);
//             OrderItem it = new OrderItem();
//             it.setProductId(p.getId());
//             it.setProductName(p.getName());
//             it.setQuantity(q);
//             it.setUnitPrice(p.getPrice());
//             order.getItems().add(it);
//             total = total.add(p.getPrice().multiply(BigDecimal.valueOf(q)));
//         }
//         order.setTotal(total);
//         return order;
//     }

//     private Result replay(CustomerOrder existing, String hash) {
//         if (!hash.equals(existing.getRequestHash()))
//             throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
//                 "This Idempotency-Key was already used for a different order.");
//         return new Result(existing, true);
//     }

//     /** Duplicate product lines are summed; TreeMap gives a stable, ascending lock order. */
//     private static Map<Long, Long> merge(OrderRequest req) {
//         Map<Long, Long> wanted = new TreeMap<>();
//         for (Line l : req.items()) wanted.merge(l.productId(), (long) l.quantity(), Long::sum);
//         return wanted;
//     }

//     /** Hash of the request's meaning (not its formatting): trimmed text, lower-cased email, merged sorted lines. */
//     private static String fingerprint(OrderRequest r, Map<Long, Long> wanted) {
//         StringBuilder sb = new StringBuilder();
//         field(sb, r.customerName().trim());
//         field(sb, r.email().trim().toLowerCase(Locale.ROOT));
//         field(sb, r.address().trim());
//         wanted.forEach((id, q) -> field(sb, id + ":" + q));
//         try {
//             byte[] d = MessageDigest.getInstance("SHA-256").digest(sb.toString().getBytes(StandardCharsets.UTF_8));
//             return HexFormat.of().formatHex(d);
//         } catch (NoSuchAlgorithmException ex) {
//             throw new IllegalStateException(ex);
//         }
//     }

//     private static void field(StringBuilder sb, String s) { sb.append(s.length()).append(':').append(s).append(';'); }
// }
