package uz.mediasolutions.saleservicebot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import uz.mediasolutions.saleservicebot.entity.Product;
import uz.mediasolutions.saleservicebot.payload.ProductDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category.id", target = "categoryId")
    List<ProductDTO> toDTOList(List<Product> products);

    @Mapping(source = "category.id", target = "categoryId")
    ProductDTO toDTO(Product product);

}
