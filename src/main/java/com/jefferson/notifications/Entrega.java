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
        if (!estaAbierta() || lote == null || lote.getPesoKg() <= 0) {
            return false;
        }
        if (lote instanceof LoteElectronico electronico && electronico.getEquiposCompletos() < 0) {
            return false;
        }
        if (buscarLote(lote.getCodigo()) != null) {
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
        if (lote == null || !estaAbierta() || nuevoPeso <= 0) {
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
