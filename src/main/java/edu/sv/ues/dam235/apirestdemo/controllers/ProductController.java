package edu.sv.ues.dam235.apirestdemo.controllers;

import edu.sv.ues.dam235.apirestdemo.dtos.ProductRequestDTO;
import edu.sv.ues.dam235.apirestdemo.dtos.ProductsDTO;
import edu.sv.ues.dam235.apirestdemo.services.ProductServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductServices productServices;

    public ProductController(ProductServices productServices) {
        this.productServices = productServices;
    }

    /**
     * Lista todos los productos.
     * GET /products
     * Header: Authorization: Bearer <token>
     */
    @GetMapping
    public ResponseEntity<List<ProductsDTO>> getAllItems() {
        try {
            List<ProductsDTO> items = productServices.getAllProducts();
            if (items.isEmpty()) {
                return ResponseEntity.status(204).build();
            }
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            log.error("Error al listar productos: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Obtiene un producto por su código.
     * GET /products/{code}
     */
    @GetMapping("/{code}")
    public ResponseEntity<ProductsDTO> getProductById(@PathVariable Integer code) {
        ProductsDTO product = productServices.getProductById(code);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

    /**
     * Crea un nuevo producto.
     * POST /products
     * Body: { "name": "...", "status": true/false }
     */
    @PostMapping
    public ResponseEntity<ProductsDTO> createProduct(@RequestBody ProductRequestDTO dto) {
        ProductsDTO created = productServices.createProduct(dto);
        return ResponseEntity.status(201).body(created);
    }

    /**
     * Actualiza un producto existente.
     * PUT /products/{code}
     * Body: { "name": "...", "status": true/false }
     */
    @PutMapping("/{code}")
    public ResponseEntity<ProductsDTO> updateProduct(@PathVariable Integer code,
                                                     @RequestBody ProductRequestDTO dto) {
        ProductsDTO updated = productServices.updateProduct(code, dto);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    /**
     * Elimina un producto por su código.
     * DELETE /products/{code}
     */
    @DeleteMapping("/{code}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer code) {
        boolean deleted = productServices.deleteProduct(code);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}