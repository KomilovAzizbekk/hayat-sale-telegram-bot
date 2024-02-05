package uz.mediasolutions.saleservicebot.controller.abs;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import uz.mediasolutions.saleservicebot.manual.ApiResult;
import uz.mediasolutions.saleservicebot.utills.constants.Rest;

@RequestMapping(CategoryController.CATEGORY)
public interface CategoryController {

    String CATEGORY = Rest.BASE_PATH + "category/";


}
