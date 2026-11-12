package com.jefferson.notifications;

import java.time.LocalDate;
import java.util.ArrayList;

public class Entrega {
    private String codigo;
    private LocalDate fecha;
    private Aportante aportante;
    private EstadoEntrega estado;
    private ArrayList<Lote> lotes;
    private Comprobante comprobante;

    public Entrega(String codigo, LocalDate fecha, Aportante aportante) {
        this.codigo = codigo;
        this.fecha = fecha;
        this.aportante = aportante;
        this.estado = EstadoEntrega.ABIERTA;
        this.lotes = new ArrayList<>();
        this.comprobante = null;
    }

    public boolean agregarLote(Lote lote) {
        if (!estaAbierta() || lote == null) {
            return false;
        }
        return lotes.add(lote);
    }

    public Lote buscarLote(String codigo) {
        for (Lote lote : lotes) {
            if (lote.getCodigo().equals(codigo)) {
                return lote;
            }
        }
        return null;
    }

    public boolean actualizarLote(String codigo, String nuevaDescripcion, int nuevoPeso) {
        Lote lote = buscarLote(codigo);
        if (lote == null || !estaAbierta()) {
            return false;
        }
        lote.setDescripcion(nuevaDescripcion);
        lote.setPesoKg(nuevoPeso);
        return true;
    }

    public boolean eliminarLote(String codigo) {
        if (!estaAbierta()) {
            return false;
        }
        Lote lote = buscarLote(codigo);
        return lote != null && lotes.remove(lote);
    }

    public boolean aceptar() {
        if (!estaAbierta()) {
            return false;
        }
        this.estado = EstadoEntrega.ACEPTADA;
        this.comprobante = new Comprobante(codigo, aportante.identificacion(), calcularPesoTotal(), calcularValorTotal());
        return true;
    }

    public int calcularPesoTotal() {
        int total = 0;
        for (Lote lote : lotes) {
            total += lote.getPesoKg();
        }
        return total;
    }

    public double calcularValorTotal() {
        double total = 0;
        for (Lote lote : lotes) {
            total += lote.calcularValor();
        }
        return total;
    }

    public ArrayList<String> obtenerEtiquetas() {
        ArrayList<String> etiquetas = new ArrayList<>();
        for (Lote lote : lotes) {
            if (lote instanceof IEtiquetable etiquetable) {
                etiquetas.add(etiquetable.generarEtiqueta());
            }
        }
        return etiquetas;
    }

    public boolean estaAbierta() {
        return estado == EstadoEntrega.ABIERTA;
    }

    public String getCodigo() {
        return codigo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Aportante getAportante() {
        return aportante;
    }

    public EstadoEntrega getEstado() {
        return estado;
    }

    public ArrayList<Lote> getLotes() {
        return lotes;
    }

    public Comprobante getComprobante() {
        return comprobante;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
}
