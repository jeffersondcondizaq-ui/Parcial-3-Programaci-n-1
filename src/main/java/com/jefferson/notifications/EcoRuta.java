package com.jefferson.notifications;

import java.time.LocalDate;
import java.util.ArrayList;

public class EcoRuta {
    private ArrayList<Aportante> aportantes;
    private ArrayList<Entrega> entregas;

    public EcoRuta() {
        this.aportantes = new ArrayList<>();
        this.entregas = new ArrayList<>();
    }

    public boolean registrarAportante(String identificacion, String nombre, String correo) {
        if (buscarAportante(identificacion) != null) {
            return false;
        }
        return aportantes.add(new Aportante(identificacion, nombre, correo));
    }

    public Aportante buscarAportante(String identificacion) {
        for (Aportante aportante : aportantes) {
            if (aportante.identificacion().equals(identificacion)) {
                return aportante;
            }
        }
        return null;
    }

    public ArrayList<Aportante> listarAportantes() {
        return aportantes;
    }

    public boolean eliminarAportante(String identificacion) {
        Aportante aportante = buscarAportante(identificacion);
        return aportante != null && aportantes.remove(aportante);
    }

    public boolean crearEntrega(String codigo, LocalDate fecha, String idAportante) {
        if (buscarEntrega(codigo) != null) {
            return false;
        }
        Aportante aportante = buscarAportante(idAportante);
        if (aportante == null) {
            return false;
        }
        return entregas.add(new Entrega(codigo, fecha, aportante));
    }

    public Entrega buscarEntrega(String codigo) {
        for (Entrega entrega : entregas) {
            if (entrega.getCodigo().equals(codigo)) {
                return entrega;
            }
        }
        return null;
    }

    public ArrayList<Entrega> listarEntregas() {
        return entregas;
    }

    public boolean actualizarFechaEntrega(String codigo, LocalDate nuevaFecha) {
        Entrega entrega = buscarEntrega(codigo);
        if (entrega == null) {
            return false;
        }
        entrega.setFecha(nuevaFecha);
        return true;
    }

    public boolean eliminarEntrega(String codigo) {
        Entrega entrega = buscarEntrega(codigo);
        return entrega != null && entrega.estaAbierta() && entregas.remove(entrega);
    }

    public boolean agregarLoteAEntrega(String codigoEntrega, Lote lote) {
        Entrega entrega = buscarEntrega(codigoEntrega);
        return entrega != null && entrega.agregarLote(lote);
    }


    public double consultarValorAcumulado(String idAportante) {
        double total = 0;
        for (Entrega entrega : entregas) {
            Comprobante comprobante = entrega.getComprobante();
            if (comprobante != null && comprobante.idAportante().equals(idAportante)) {
                total += comprobante.valorTotal();
            }
        }
        return total;
    }

    public boolean aceptarEntrega(String codigo) {
        Entrega entrega = buscarEntrega(codigo);
        if (entrega == null) {
            return false;
        }
        return entrega.aceptar();
    }

    public Comprobante consultarComprobante(String codigoEntrega) {
        Entrega entrega = buscarEntrega(codigoEntrega);
        if (entrega == null) {
            return null;
        }
        return entrega.getComprobante();
    }

    public ArrayList<String> consultarEtiquetas(String codigoEntrega) {
        Entrega entrega = buscarEntrega(codigoEntrega);
        if (entrega == null) {
            return new ArrayList<>();
        }
        return entrega.obtenerEtiquetas();
    }

}
