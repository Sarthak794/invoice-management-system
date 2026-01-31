package invoice.service;

import invoice.entity.Product;
import invoice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllActiveProducts() {
        return productRepository.findByActiveTrue();
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public void save(Product product) {
        productRepository.save(product);
    }

    public void deleteById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setActive(false);
        productRepository.save(product);
    }

    public void reduceStock(Product product, int quantity) {
        if (product.getStock() < quantity) {
            throw new RuntimeException(
                "Insufficient stock for product: " + product.getName()
            );
        }
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }
}
