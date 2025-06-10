package com.Proveedor.MSProveedor.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.Proveedor.MSProveedor.model.Proveedor;
import com.Proveedor.MSProveedor.model.estadoProveedor;
import com.Proveedor.MSProveedor.repository.proveedorRepository;

class ProveedorServiceTest {
    @Mock
    proveedorRepository pRepo;

    @InjectMocks
    proveedorService pService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGuardarProveedor() {
        Proveedor proveedor = new Proveedor(0, "Carniceria Martinez", "camartinez@gmail.com", null, "54528785",
                "Las rosas 75");
        Proveedor proveedorGuardado = new Proveedor(1, "Carniceria Martinez", "camartinez@gmail.com",
                estadoProveedor.ACTIVO, "54528785", "Las rosas 75");
        when(pRepo.findByCorreoProv("camartinez@gmail.com")).thenReturn(null);
        when(pRepo.save(proveedor)).thenReturn(proveedorGuardado);

        Proveedor resultado = pService.registrarProveedor(proveedor);
        assertThat(resultado.getIdProveedor()).isEqualTo(1);
        assertThat(resultado.getEstado()).isEqualTo(estadoProveedor.ACTIVO);
        verify(pRepo).save(proveedor);
    }

    @Test
    void TestCorreoExistente() {
        Proveedor existente = new Proveedor();
        existente.setCorreoProv("existe@correo.com");

        Proveedor nuevo = new Proveedor();
        nuevo.setCorreoProv("existe@correo.com");

        when(pRepo.findByCorreoProv("existe@correo.com")).thenReturn(existente);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            pService.registrarProveedor(nuevo);
        });

        assertThat(ex.getMessage()).isEqualTo("Correo ya registrado");

        verify(pRepo, never()).save(any());
    }

    @Test
    void TestActualizarInfo() {
        Proveedor proveedor = new Proveedor();
        proveedor.setCorreoProv("proveedor@correo.com");
        proveedor.setEstado(estadoProveedor.ACTIVO);

        when(pRepo.save(proveedor)).thenReturn(proveedor);

        Proveedor resultado = pService.actualizarInfo(proveedor);

        assertThat(resultado).isEqualTo(proveedor);
        verify(pRepo).save(proveedor);
    }

    @Test
    void TestActivarProveedor() {
        Proveedor inactivo = new Proveedor();
        inactivo.setIdProveedor(1);
        inactivo.setEstado(estadoProveedor.INACTIVO);

        Proveedor activado = new Proveedor();
        activado.setIdProveedor(1);
        activado.setEstado(estadoProveedor.ACTIVO);

        when(pRepo.findById(1)).thenReturn(Optional.of(inactivo));
        when(pRepo.save(inactivo)).thenReturn(activado);

        Proveedor resultado = pService.activaProveedor(1);

        assertThat(resultado.getEstado()).isEqualTo(estadoProveedor.ACTIVO);
        verify(pRepo).save(inactivo);
    }

    @Test
    void TestActivarProveedorYaActivo() {
        Proveedor activo = new Proveedor();
        activo.setIdProveedor(2);
        activo.setEstado(estadoProveedor.ACTIVO);

        when(pRepo.findById(2)).thenReturn(Optional.of(activo));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            pService.activaProveedor(2);
        });

        assertThat(ex.getMessage()).isEqualTo("Este proveedor ya esta activo");
        verify(pRepo, never()).save(any());
    }

    @Test
    void TestActivarNoExistente() {
        // Arrange
        when(pRepo.findById(3)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            pService.activaProveedor(3);
        });

        verify(pRepo, never()).save(any());
    }

    @Test
    void TestDesactivarProveedor() {
        Proveedor inactivo = new Proveedor();
        inactivo.setIdProveedor(1);
        inactivo.setEstado(estadoProveedor.INACTIVO);

        Proveedor activado = new Proveedor();
        activado.setIdProveedor(1);
        activado.setEstado(estadoProveedor.ACTIVO);

        when(pRepo.findById(1)).thenReturn(Optional.of(activado));
        when(pRepo.save(activado)).thenReturn(inactivo);

        Proveedor resultado = pService.desactivaProveedor(1);

        assertThat(resultado.getEstado()).isEqualTo(estadoProveedor.INACTIVO);
        verify(pRepo).save(activado);
    }

    @Test
    void TestDesactivarProveedorYaInactivo() {
        Proveedor inactivo = new Proveedor();
        inactivo.setIdProveedor(2);
        inactivo.setEstado(estadoProveedor.INACTIVO);

        when(pRepo.findById(2)).thenReturn(Optional.of(inactivo));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            pService.desactivaProveedor(2);
        });

        assertThat(ex.getMessage()).isEqualTo("Este proveedor ya esta inactivo");
        verify(pRepo, never()).save(any());
    }

    @Test
    void TestDesactivarNoExistente() {
        when(pRepo.findById(3)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            pService.desactivaProveedor(3);
        });

        verify(pRepo, never()).save(any());
    }

}
