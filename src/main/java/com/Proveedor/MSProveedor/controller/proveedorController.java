package com.Proveedor.MSProveedor.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Proveedor.MSProveedor.model.Proveedor;
import com.Proveedor.MSProveedor.model.estadoProveedor;
import com.Proveedor.MSProveedor.service.proveedorService;

@RestController
@RequestMapping("api/v1/proveedor")
public class proveedorController {
    
    @Autowired
    private proveedorService proveedorService;

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Proveedor proveedor){
        if(proveedor.getIdProveedor() != 0 && proveedorService.existePorId(proveedor.getIdProveedor())){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        proveedor.setEstado(estadoProveedor.ACTIVO);
        return new ResponseEntity<>(proveedorService.registrarProveedor(proveedor), HttpStatus.OK);
    }

     @GetMapping
    public ResponseEntity<List<Proveedor>> listar() {
        List<Proveedor> proveedores = proveedorService.listarProveedores();
        if(proveedores.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(proveedorService.listarProveedores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> obtener(@PathVariable int id) {
        return proveedorService.buscarProveedor(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/editar")
    public ResponseEntity<Proveedor> editarDatos(@RequestBody Proveedor proveedor, @PathVariable int id) {
        Proveedor actualizado = proveedorService.actualizarInfo(id,proveedor);
        return ResponseEntity.ok(actualizado);
    }

    @PutMapping("/{id}/activar")
    public ResponseEntity<Proveedor> activarProv(@PathVariable int id){
        try{
            return ResponseEntity.ok(proveedorService.activaProveedor(id));
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<Proveedor> desactivarProv(@PathVariable int id){
        try{
            return ResponseEntity.ok(proveedorService.desactivaProveedor(id));
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }
}
