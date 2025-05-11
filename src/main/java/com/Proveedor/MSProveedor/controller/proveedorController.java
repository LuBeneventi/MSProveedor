package com.Proveedor.MSProveedor.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Proveedor.MSProveedor.model.Proveedor;
import com.Proveedor.MSProveedor.service.proveedorService;

@RestController
@RequestMapping("api/proveedor")
public class proveedorController {
    
    @Autowired
    private proveedorService proveedorService;

    @PostMapping("/registro")
    public Proveedor registrar(@RequestBody Proveedor proveedor){
        return proveedorService.registrarProveedor(proveedor);
    }

     @GetMapping
    public ResponseEntity<List<Proveedor>> listar() {
        return ResponseEntity.ok(proveedorService.listarProveedores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> obtener(@PathVariable int id) {
        return proveedorService.buscarProveedor(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/editar")
    public Proveedor editarDatos(@RequestBody Proveedor proveedor, @PathVariable int id) {
        proveedor.setIdProveedor(id);;
        return proveedorService.actualizarInfo(proveedor);
    }

    @DeleteMapping("/{id}/borrar")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        proveedorService.eliminarProveedor(id);
        return ResponseEntity.noContent().build();
    }
}
