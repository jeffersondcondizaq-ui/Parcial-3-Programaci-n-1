package com.jefferson.notifications;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Logger;

import models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import enums.EstadoEntrega;
import enums.TipoMetal;


public class EcoRutaTest {


    private static final Logger LOG = Logger.getLogger(EcoRutaTest.class.getName());

    private EcoRuta ecoRuta;
    private LocalDate fecha;

    // Se ejecuta antes de cada prueba: sistema nuevo con un aportante registrado
    @BeforeEach
    public void setUp() {
        ecoRuta = new EcoRuta();
        ecoRuta.registrarAportante("1094", "Ana Pérez", "ana@mail.com");
        fecha = LocalDate.of(2026, 11, 12);
    }

    @Test
    @DisplayName("P1. Registro de una entrega")
    public void registroEntrega() {
        LOG.info("Inicio de prueba registroEntrega...");


        assertTrue(ecoRuta.crearEntrega("E01", fecha, "1094"));
        Entrega e01 = ecoRuta.buscarEntrega("E01");
        assertNotNull(e01);
        assertEquals(EstadoEntrega.ABIERTA, e01.getEstado());
        assertEquals(0, e01.getLotes().size());
        assertNull(e01.getComprobante());

        // Código repetido
        assertFalse(ecoRuta.crearEntrega("E01", fecha, "1094"));
        // Código nuevo con aportante desconocido
        assertFalse(ecoRuta.crearEntrega("E02", fecha, "9999"));

        assertNull(ecoRuta.buscarEntrega("E02"));
        assertEquals(1, ecoRuta.listarEntregas().size());
        assertSame(e01, ecoRuta.buscarEntrega("E01"));

        LOG.info("Fin de prueba registroEntrega...");
    }

    @Test
    @DisplayName("P2. Validación de lotes")
    public void validacionLotes() {
        LOG.info("Inicio de prueba validacionLotes...");

        ecoRuta.crearEntrega("E01", fecha, "1094");
        Entrega e01 = ecoRuta.buscarEntrega("E01");

        assertTrue(e01.agregarLote(new LotePapelCarton("L01", "Cartón", 5, false)));
        // Código repetido
        assertFalse(e01.agregarLote(new LoteMetal("L01", "Latas", 3, TipoMetal.ALUMINIO)));
        // Peso 0 kg
        assertFalse(e01.agregarLote(new LoteElectronico("L02", "Equipos", 0, 1)));

        assertEquals(1, e01.getLotes().size());
        assertEquals("L01", e01.getLotes().get(0).getCodigo());
        assertEquals(5, e01.getLotes().get(0).getPesoKg());

        LOG.info("Fin de prueba validacionLotes...");
    }

    @Test
    @DisplayName("P3. Rechazo de una entrega vacía")
    public void rechazoEntregaVacia() {
        LOG.info("Inicio de prueba rechazoEntregaVacia...");

        ecoRuta.crearEntrega("E01", fecha, "1094");
        Entrega e01 = ecoRuta.buscarEntrega("E01");

        assertFalse(ecoRuta.aceptarEntrega("E01"));
        assertEquals(EstadoEntrega.ABIERTA, e01.getEstado());
        assertNull(e01.getComprobante());

        LOG.info("Fin de prueba rechazoEntregaVacia...");
    }

    @Test
    @DisplayName("P4. Aceptación y cálculo con los tres tipos de lote")
    public void aceptacionYCalculo() {
        LOG.info("Inicio de prueba aceptacionYCalculo...");

        ecoRuta.crearEntrega("E01", fecha, "1094");
        Entrega e01 = ecoRuta.buscarEntrega("E01");

        LotePapelCarton papel = new LotePapelCarton("P01", "Cartón", 10, true);
        LoteMetal metal = new LoteMetal("M01", "Latas", 5, TipoMetal.ALUMINIO);
        LoteElectronico electronico = new LoteElectronico("R01", "Computadores", 4, 2);

        assertTrue(ecoRuta.agregarLoteAEntrega("E01", papel));
        assertTrue(ecoRuta.agregarLoteAEntrega("E01", metal));
        assertTrue(ecoRuta.agregarLoteAEntrega("E01", electronico));

        assertEquals(6000, papel.calcularValor());
        assertEquals(11000, metal.calcularValor());
        assertEquals(7200, electronico.calcularValor());

        assertTrue(ecoRuta.aceptarEntrega("E01"));
        assertEquals(EstadoEntrega.ACEPTADA, e01.getEstado());

        Comprobante comprobante = ecoRuta.consultarComprobante("E01");
        assertNotNull(comprobante);
        assertEquals("E01", comprobante.codigoEntrega());
        assertEquals("1094", comprobante.idAportante());
        assertEquals(19, comprobante.pesoTotal());
        assertEquals(24200, comprobante.valorTotal());

        LOG.info("Fin de prueba aceptacionYCalculo...");
    }

    @Test
    @DisplayName("P5. Consulta de etiquetas")
    public void consultaEtiquetas() {
        LOG.info("Inicio de prueba consultaEtiquetas...");

        // Misma entrega aceptada de la prueba 4 (cada @Test se ejecuta por separado)
        ecoRuta.crearEntrega("E01", fecha, "1094");
        ecoRuta.agregarLoteAEntrega("E01", new LotePapelCarton("P01", "Cartón", 10, true));
        ecoRuta.agregarLoteAEntrega("E01", new LoteMetal("M01", "Latas", 5, TipoMetal.ALUMINIO));
        ecoRuta.agregarLoteAEntrega("E01", new LoteElectronico("R01", "Computadores", 4, 2));
        assertTrue(ecoRuta.aceptarEntrega("E01"));

        ArrayList<String> etiquetas = ecoRuta.consultarEtiquetas("E01");

        assertEquals(2, etiquetas.size());
        assertTrue(etiquetas.get(0).contains("M01") && etiquetas.get(0).contains("ALUMINIO"));
        assertTrue(etiquetas.get(1).contains("R01") && etiquetas.get(1).contains("2"));

        LOG.info("Fin de prueba consultaEtiquetas...");
    }
}
