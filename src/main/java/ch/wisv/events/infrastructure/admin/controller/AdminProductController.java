package ch.wisv.events.infrastructure.admin.controller;

import ch.wisv.events.domain.model.product.Product;
import ch.wisv.events.services.ProductService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

/**
 * AdminProductController.
 */
@Controller
@RequestMapping("/administrator/products")
public class AdminProductController extends AbstractAdminController<Product> {

    /**
     * AdminProductController constructor.
     *
     * @param productService of type ProductService
     */
    @Autowired
    public AdminProductController(ProductService productService) {
        super(productService, new Product(), "products", "product");
    }

    /**
     * Save a file.
     *
     * @param model of type AbstractModel
     * @param file  of type MultipartFile
     *
     * @return T
     */
    @Override
    Product saveFile(Product model, MultipartFile file) {
        return model;
    }

    /**
     * Add Model to the index page.
     *
     * @return Map
     */
    @Override
    Map<String, ?> beforeIndex() {
        return null;
    }

    /**
     * Add Model to the view page.
     *
     * @return Map
     */
    @Override
    Map<String, ?> beforeView() {
        return null;
    }

    /**
     * Add Model to the edit page.
     *
     * @return Map
     */
    @Override
    Map<String, ?> beforeCreateEdit() {
        return null;
    }
}
