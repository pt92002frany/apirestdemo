package edu.sv.ues.dam235.apirestdemo.implementations;

import edu.sv.ues.dam235.apirestdemo.dtos.ProductRequestDTO;
import edu.sv.ues.dam235.apirestdemo.dtos.ProductsDTO;
import edu.sv.ues.dam235.apirestdemo.entities.Product;
import edu.sv.ues.dam235.apirestdemo.repositories.ProductRepository;
import edu.sv.ues.dam235.apirestdemo.services.ProductServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ProductsImpl implements ProductServices {

    private final ProductRepository productRepository;

    public ProductsImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductsDTO> getAllProducts() {
        List<ProductsDTO> result = new ArrayList<>();
        List<Product> items = this.productRepository.findAll();
        for (Product item : items) {
            result.add(new ProductsDTO(item.getCode(), item.getName(), item.isStatus()));
        }
        return result;
    }

    @Override
    public ProductsDTO getProductById(Integer code) {
        Product product = productRepository.findById(code).orElse(null);
        if (product == null) return null;
        return new ProductsDTO(product.getCode(), product.getName(), product.isStatus());
    }

    @Override
    public ProductsDTO createProduct(ProductRequestDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setStatus(dto.isStatus());
        Product saved = productRepository.save(product);
        return new ProductsDTO(saved.getCode(), saved.getName(), saved.isStatus());
    }

    @Override
    public ProductsDTO updateProduct(Integer code, ProductRequestDTO dto) {
        Product product = productRepository.findById(code).orElse(null);
        if (product == null) return null;
        product.setName(dto.getName());
        product.setStatus(dto.isStatus());
        Product updated = productRepository.save(product);
        return new ProductsDTO(updated.getCode(), updated.getName(), updated.isStatus());
    }

    @Override
    public boolean deleteProduct(Integer code) {
        if (!productRepository.existsById(code)) return false;
        productRepository.deleteById(code);
        return true;
    }
}