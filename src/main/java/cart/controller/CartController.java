package cart.controller;

import cart.authority.Authority;
import cart.controller.dto.request.CartItemCreationRequest;
import cart.controller.dto.request.MemberId;
import cart.controller.dto.response.CartItemResponse;
import cart.domain.dto.CartDto;
import cart.domain.dto.ProductDto;
import cart.service.cart.CartCreateService;
import cart.service.cart.CartDeleteService;
import cart.service.cart.CartReadService;
import cart.service.product.ProductReadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cart/items")
public class CartController {

    private final CartCreateService cartCreateService;
    private final CartReadService cartReadService;
    private final CartDeleteService cartDeleteService;
    private final ProductReadService productReadService;

    public CartController(
            final CartCreateService cartCreateService,
            final CartReadService cartReadService,
            final CartDeleteService cartDeleteService,
            final ProductReadService productReadService
    ) {
        this.cartCreateService = cartCreateService;
        this.cartReadService = cartReadService;
        this.cartDeleteService = cartDeleteService;
        this.productReadService = productReadService;
    }

    @PostMapping
    public ResponseEntity<String> addProduct(
            @RequestBody @Valid final CartItemCreationRequest cartItemCreationRequest,
            @Authority final MemberId memberId
    ) {
        cartCreateService.addProduct(cartItemCreationRequest, memberId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> findIdMemberId(@Authority final MemberId memberId) {
        final List<CartDto> cartDtos = cartReadService.getProducts(memberId);
        final List<ProductDto> productDtos = productReadService.findById(cartDtos);

        List<CartItemResponse> cartItemResponses = productDtos.stream()
                .map(productDto -> new CartItemResponse(
                        productDto.getId(),
                        productDto.getName(),
                        productDto.getImage(),
                        productDto.getPrice())
                )
                .collect(Collectors.toList());

        return ResponseEntity.ok(cartItemResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable("id") @NotNull(message = "아이디가 비어있습니다.") Long productIdRequest,
            @Authority final MemberId memberId
    ) {
        cartDeleteService.delete(memberId, productIdRequest);
        return ResponseEntity.noContent().build();
    }
}