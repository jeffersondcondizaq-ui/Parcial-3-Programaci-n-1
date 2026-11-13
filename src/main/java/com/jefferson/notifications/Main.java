package com.jefferson.notifications;

import enums.TipoMetal;
import models.*;

import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class Main {
    private static final EcoRuta ecoRuta = new EcoRuta();
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static class OperacionCancelada extends RuntimeException {
    }

    public static void main(String[] args) {
        cargarDatosIniciales();
        menuPrincipal();
    }

    private static void cargarDatosIniciales() {
        ecoRuta.registrarAportante("1001", "Juan Perez", "juan.perez@mail.com");
        ecoRuta.registrarAportante("1002", "Maria Gomez", "maria.gomez@mail.com");
    }

    // ---------- MENU PRINCIPAL ----------

    private static void menuPrincipal() {
        String opcion;
        do {
            opcion = mostrarMenu("EcoRuta", new String[]{"Gestionar aportantes", "Gestionar entregas", "Salir"});
            if (opcion == null) {
                opcion = "Salir";
            }
            try {
                switch (opcion) {
                    case "Gestionar aportantes" -> menuAportantes();
                    case "Gestionar entregas" -> menuEntregas();
                    case "Salir" -> {
                    }
                }
            } catch (OperacionCancelada e) {
                // operacion cancelada por el usuario, se vuelve al menu
            }
        } while (!opcion.equals("Salir"));
    }

    // ---------- MENU APORTANTES ----------

    private static void menuAportantes() {
        String opcion;
        do {
            opcion = mostrarMenu("Aportantes", new String[]{
                    "Buscar aportante", "Listar aportantes", "Volver"});
            if (opcion == null) {
                opcion = "Volver";
            }
            try {
                switch (opcion) {
                    case "Buscar aportante" -> buscarAportante();
                    case "Listar aportantes" -> listarAportantes();
                    case "Volver" -> {
                    }
                }
            } catch (OperacionCancelada e) {
                // operacion cancelada por el usuario, se vuelve al submenu
            }
        } while (!opcion.equals("Volver"));
    }

    private static void buscarAportante() {
        String identificacion = leerTexto("Identificacion a buscar:");
        Aportante aportante = ecoRuta.buscarAportante(identificacion);
        mostrarMensaje(aportante != null ? aportante.toString() : "No existe un aportante con esa identificacion.");
    }

    private static void listarAportantes() {
        ArrayList<Aportante> aportantes = ecoRuta.listarAportantes();
        if (aportantes.isEmpty()) {
            mostrarMensaje("No hay aportantes registrados.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        aportantes.forEach(a -> sb.append(a).append("\n"));
        mostrarMensaje(sb.toString());
    }

    // ---------- MENU ENTREGAS ----------

    private static void menuEntregas() {
        String opcion;
        do {
            opcion = mostrarMenu("Entregas", new String[]{
                    "Crear entrega", "Agregar lote", "Buscar lote", "Eliminar lote",
                    "Buscar entrega", "Listar entregas", "Eliminar entrega", "Volver"});
            if (opcion == null) {
                opcion = "Volver";
            }
            try {
                switch (opcion) {
                    case "Crear entrega" -> crearEntrega();
                    case "Agregar lote" -> agregarLoteAEntrega();
                    case "Buscar lote" -> buscarLote();
                    case "Eliminar lote" -> eliminarLote();
                    case "Buscar entrega" -> buscarEntrega();
                    case "Listar entregas" -> listarEntregas();
                    case "Eliminar entrega" -> eliminarEntrega();
                    case "Volver" -> {
                    }
                }
            } catch (OperacionCancelada e) {
                // operacion cancelada por el usuario, se vuelve al submenu
            }
        } while (!opcion.equals("Volver"));
    }

    private static void crearEntrega() {
        String codigo = leerTexto("Codigo de la entrega:");
        if (ecoRuta.buscarEntrega(codigo) != null) {
            mostrarMensaje("Ya existe una entrega con ese codigo.");
            return;
        }
        String idAportante = leerTexto("Identificacion del aportante:");
        if (ecoRuta.buscarAportante(idAportante) == null) {
            mostrarMensaje("No existe un aportante con esa identificacion.");
            return;
        }
        LocalDate fecha = leerFecha("Fecha (yyyy-MM-dd):");

        boolean exito = ecoRuta.crearEntrega(codigo, fecha, idAportante);
        mostrarMensaje(exito ? "Entrega creada en estado ABIERTA." : "No se pudo crear la entrega.");
    }

    private static void agregarLoteAEntrega() {
        Entrega entrega = seleccionarEntrega();
        if (!entrega.estaAbierta()) {
            mostrarMensaje("La entrega ya fue aceptada, no admite nuevos lotes.");
            return;
        }

        String codigo = leerTexto("Codigo del lote:");
        String descripcion = leerTexto("Descripcion:");
        int pesoKg = leerEntero("Peso en kg (entero positivo):");

        Lote lote = construirLotePorTipo(codigo, descripcion, pesoKg);

        boolean exito = ecoRuta.agregarLoteAEntrega(entrega.getCodigo(), lote);
        mostrarMensaje(exito ? "Lote agregado a la entrega."
                : "No se pudo agregar el lote (peso invalido o codigo repetido).");
    }

    private static Lote construirLotePorTipo(String codigo, String descripcion, int pesoKg) {
        String tipo = mostrarMenu("Tipo de lote", new String[]{"Papel/Carton", "Metal", "Residuo electronico"});
        if (tipo == null) {
            throw new OperacionCancelada();
        }

        return switch (tipo) {
            case "Papel/Carton" -> {
                boolean clasificado = leerBooleano("Esta clasificado?");
                yield new LotePapelCarton(codigo, descripcion, pesoKg, clasificado);
            }
            case "Metal" -> {
                TipoMetal tipoMetal = leerTipoMetal();
                yield new LoteMetal(codigo, descripcion, pesoKg, tipoMetal);
            }
            case "Residuo electronico" -> {
                int equiposCompletos = leerEntero("Cantidad de equipos completos:");
                yield new LoteElectronico(codigo, descripcion, pesoKg, equiposCompletos);
            }
            default -> throw new OperacionCancelada();
        };
    }

    private static TipoMetal leerTipoMetal() {
        String tipo = mostrarMenu("Tipo de metal", new String[]{"Aluminio", "Acero"});
        if (tipo == null) {
            throw new OperacionCancelada();
        }
        return tipo.equals("Aluminio") ? TipoMetal.ALUMINIO : TipoMetal.ACERO;
    }

    private static void buscarLote() {
        Entrega entrega = seleccionarEntrega();
        String codigoLote = leerTexto("Codigo del lote a buscar:");
        Lote lote = entrega.buscarLote(codigoLote);
        mostrarMensaje(lote != null ? lote.toString() : "No existe un lote con ese codigo en la entrega.");
    }

    private static void eliminarLote() {
        Entrega entrega = seleccionarEntrega();
        String codigoLote = leerTexto("Codigo del lote a eliminar:");
        boolean exito = entrega.eliminarLote(codigoLote);
        mostrarMensaje(exito ? "Lote eliminado." : "No se pudo eliminar el lote.");
    }

    private static void buscarEntrega() {
        Entrega entrega = seleccionarEntrega();
        mostrarMensaje(detalleEntrega(entrega));
    }

    private static void listarEntregas() {
        ArrayList<Entrega> entregas = ecoRuta.listarEntregas();
        if (entregas.isEmpty()) {
            mostrarMensaje("No hay entregas registradas.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Entrega entrega : entregas) {
            sb.append(String.format("%s | %s | %s | %s | %d lote(s)%n",
                    entrega.getCodigo(), entrega.getFecha(), entrega.getAportante().nombre(),
                    entrega.getEstado(), entrega.getLotes().size()));
        }
        mostrarMensaje(sb.toString());
    }

    private static void eliminarEntrega() {
        String codigo = leerTexto("Codigo de la entrega a eliminar:");
        boolean exito = ecoRuta.eliminarEntrega(codigo);
        mostrarMensaje(exito ? "Entrega eliminada."
                : "No se pudo eliminar: la entrega no existe o ya fue aceptada.");
    }

    private static Entrega seleccionarEntrega() {
        String codigo = leerTexto("Codigo de la entrega:");
        Entrega entrega = ecoRuta.buscarEntrega(codigo);
        if (entrega == null) {
            mostrarMensaje("No existe una entrega con ese codigo.");
            throw new OperacionCancelada();
        }
        return entrega;
    }

    private static String detalleEntrega(Entrega entrega) {
        StringBuilder sb = new StringBuilder();
        sb.append("Codigo: ").append(entrega.getCodigo()).append("\n");
        sb.append("Fecha: ").append(entrega.getFecha()).append("\n");
        sb.append("Aportante: ").append(entrega.getAportante()).append("\n");
        sb.append("Estado: ").append(entrega.getEstado()).append("\n");
        sb.append("Lotes:\n");
        if (entrega.getLotes().isEmpty()) {
            sb.append("  (sin lotes)\n");
        } else {
            entrega.getLotes().forEach(lote -> sb.append("  ").append(lote).append("\n"));
        }
        return sb.toString();
    }

    // ---------- UTILIDADES DE INTERFAZ ----------

    private static String mostrarMenu(String titulo, String[] opciones) {
        return (String) JOptionPane.showInputDialog(null, "Seleccione una opcion:", titulo,
                JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
    }

    private static void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje);
    }

    private static String leerTexto(String mensaje) {
        String valor = JOptionPane.showInputDialog(null, mensaje);
        if (valor == null) {
            throw new OperacionCancelada();
        }
        return valor.trim();
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            String entrada = JOptionPane.showInputDialog(null, mensaje);
            if (entrada == null) {
                throw new OperacionCancelada();
            }
            try {
                return Integer.parseInt(entrada.trim());
            } catch (NumberFormatException e) {
                mostrarMensaje("Debe ingresar un numero entero valido.");
            }
        }
    }

    private static boolean leerBooleano(String mensaje) {
        int respuesta = JOptionPane.showConfirmDialog(null, mensaje, "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.CLOSED_OPTION) {
            throw new OperacionCancelada();
        }
        return respuesta == JOptionPane.YES_OPTION;
    }

    private static LocalDate leerFecha(String mensaje) {
        while (true) {
            String entrada = JOptionPane.showInputDialog(null, mensaje);
            if (entrada == null) {
                throw new OperacionCancelada();
            }
            try {
                return LocalDate.parse(entrada.trim(), FORMATO_FECHA);
            } catch (DateTimeParseException e) {
                mostrarMensaje("Formato de fecha invalido. Use yyyy-MM-dd.");
            }
        }
    }
}
