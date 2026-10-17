package edu.sv.ues.dam235.apirestdemo.services;

import edu.sv.ues.dam235.apirestdemo.dtos.ProductRequestDTO;
import edu.sv.ues.dam235.apirestdemo.dtos.ProductsDTO;

import java.util.List;

public interface ProductServices {

    /**
     * Obtiene todos los productos de la base de datos.
     * @return lista de ProductsDTO
     */
    List<ProductsDTO> getAllProducts();

    /**
     * Obtiene un producto por su código.
     * @param code código del producto
     * @return ProductsDTO o null si no existe
     */
    ProductsDTO getProductById(Integer code);

    /**
     * Crea un nuevo producto.
     * @param productRequestDTO datos del producto a crear
     * @return ProductsDTO del producto creado
     */
    ProductsDTO createProduct(ProductRequestDTO productRequestDTO);

    /**
     * Actualiza un producto existente.
     * @param code código del producto a actualizar
     * @param productRequestDTO nuevos datos del producto
     * @return ProductsDTO actualizado o null si no existe
     */
    ProductsDTO updateProduct(Integer code, ProductRequestDTO productRequestDTO);

    /**
     * Elimina un producto por su código.
     * @param code código del producto a eliminar
     * @return true si se eliminó, false si no existía
     */
    boolean deleteProduct(Integer code);
}