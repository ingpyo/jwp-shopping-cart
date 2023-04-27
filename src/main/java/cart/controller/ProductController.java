package cart.controller;

import cart.domain.Product;
import cart.dto.ProductRequest;
import cart.dto.ProductResponse;
import cart.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(final ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody @Valid ProductRequest productRequest) {
        productService.add(productRequest.getName(), productRequest.getImage(), productRequest.getPrice());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> readProducts() {
        return ResponseEntity.ok(mapProducts(productService.getAll()));
    }

    private List<ProductResponse> mapProducts(List<Product> products) {
        return products.stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getImage(),
                        product.getPrice())
                ).collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateProduct(
            @PathVariable("id") Integer id,
            @RequestBody @Valid ProductRequest productRequest) {
        Objects.requireNonNull(id);
        productService.update(id, productRequest.getName(), productRequest.getImage(), productRequest.getPrice());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") Integer id) {
        Objects.requireNonNull(id);
        productService.delete(id);
        return ResponseEntity.ok().build();
    }
}
