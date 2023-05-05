package cart.controller.dto.request;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

public class ProductCreationRequest {

    @NotBlank(message = "이름이 비어있습니다.")
    private String name;
    @NotBlank(message = "이미지가 비어있습니다.")
    private String image;
    @Range(
            min = 0,
            max = 100000000,
            message = "상품의 가격은 0~100000000 사이의 값이여야 합니다."
    )
    private Long price;

    public ProductCreationRequest() {
    }

    public ProductCreationRequest(final String name, final String image, final Long price) {
        this.name = name;
        this.image = image;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public Long getPrice() {
        return price;
    }
}
