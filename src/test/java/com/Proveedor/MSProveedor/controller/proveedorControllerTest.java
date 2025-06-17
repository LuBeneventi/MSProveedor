package com.Proveedor.MSProveedor.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.Proveedor.MSProveedor.model.Proveedor;
import com.Proveedor.MSProveedor.model.estadoProveedor;
import com.Proveedor.MSProveedor.service.proveedorService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(proveedorController.class)
class proveedorControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @SuppressWarnings("removal")
        @MockBean
        private proveedorService pService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test // Registro de proveedores
        void RegistroTestOK() throws Exception {
                Proveedor nuevo = new Proveedor(0, "Lacosta", "lacost@gmail.com", null, "56952482123",
                                "Los Hulmos 785");
                Proveedor registrado = new Proveedor(1, "Lacosta", "lacost@gmail.com", estadoProveedor.ACTIVO,
                                "56952482123",
                                "Los Hulmos 785");

                when(pService.registrarProveedor(any(Proveedor.class))).thenReturn(registrado);

                mockMvc.perform(post("/api/v1/proveedor")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevo)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.idProveedor").value(1))
                                .andExpect(jsonPath("$.nomProv").value("Lacosta"))
                                .andExpect(jsonPath("$.correoProv").value("lacost@gmail.com"))
                                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                                .andExpect(jsonPath("$.telProv").value("56952482123"))
                                .andExpect(jsonPath("$.dirProv").value("Los Hulmos 785"));
        }

        @Test // Registro de proveedores - Si el ID esta repetido
        void RegistroTestBAD() throws Exception {
                Proveedor nuevo = new Proveedor(10, "Carnes Mauricio", "carnesmau@gmail.com", estadoProveedor.ACTIVO,
                                "+56993326556", "Las Rosas 503");

                when(pService.existePorId(10)).thenReturn(true);

                mockMvc.perform(post("/api/v1/proveedor")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(nuevo)))
                                .andExpect(status().isBadRequest());
        }

        @Test // Listar Proveedores
        void ListarProveedoresOK() throws Exception {
                Proveedor p1 = new Proveedor(10, "Carnes Mauricio", "carnesmau@gmail.com", estadoProveedor.ACTIVO,
                                "+56993326556", "Las Rosas 503");
                Proveedor p2 = new Proveedor(1, "Lacosta", "lacost@gmail.com", estadoProveedor.ACTIVO, "56952482123",
                                "Los Hulmos 785");

                when(pService.listarProveedores()).thenReturn(Arrays.asList(p1, p2));

                mockMvc.perform(get("/api/v1/proveedor")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$._embedded.proveedorList[0].nomProv").value("Carnes Mauricio"))
                                .andExpect(jsonPath("$._embedded.proveedorList[1].nomProv").value("Lacosta"));
        }

        @Test // Listar Proveedores - Lista Vacia
        void ListarProveedoresBAD() throws Exception {
                when(pService.listarProveedores()).thenReturn(Arrays.asList());

                mockMvc.perform(get("/api/v1/proveedor")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNoContent());
        }
}
