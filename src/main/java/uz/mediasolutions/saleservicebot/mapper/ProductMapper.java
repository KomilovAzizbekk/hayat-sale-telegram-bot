package uz.mediasolutions.saleservicebot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import uz.mediasolutions.saleservicebot.entity.Product;
import uz.mediasolutions.saleservicebot.payload.ProductDTO;
import uz.mediasolutions.saleservicebot.payload.ProductResDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(source = "category.id", target = "categoryId")
    List<ProductDTO> toDTOList(List<Product> products);

    @Mapping(source = "category.id", target = "categoryId")
    ProductDTO toDTO(Product product);

    ProductResDTO toResDTO(Product product);

}
