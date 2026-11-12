package com.jefferson.notifications;

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
            opcion = mostrarMenu("EcoRuta", new String[]{"Gestionar aportantes", "Gestionar entregas", "Consultas", "Salir"});
            if (opcion == null) {
                opcion = "Salir";
            }
            try {
                switch (opcion) {
                    case "Gestionar aportantes" -> menuAportantes();
                    case "Gestionar entregas" -> menuEntregas();
                    case "Consultas" -> menuConsultas();
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
                    "Registrar aportante", "Buscar aportante", "Listar aportantes", "Eliminar aportante", "Volver"});
            if (opcion == null) {
                opcion = "Volver";
            }
            try {
                switch (opcion) {
                    case "Registrar aportante" -> registrarAportante();
                    case "Buscar aportante" -> buscarAportante();
                    case "Listar aportantes" -> listarAportantes();
                    case "Eliminar aportante" -> eliminarAportante();
                    case "Volver" -> {
                    }
                }
            } catch (OperacionCancelada e) {
                // operacion cancelada por el usuario, se vuelve al submenu
            }
        } while (!opcion.equals("Volver"));
    }

    private static void registrarAportante() {
        String identificacion = leerTexto("Identificacion:");
        String nombre = leerTexto("Nombre:");
        String correo = leerTexto("Correo:");

        boolean exito = ecoRuta.registrarAportante(identificacion, nombre, correo);
        mostrarMensaje(exito ? "Aportante registrado con exito."
                : "No se pudo registrar: la identificacion ya existe.");
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

    private static void eliminarAportante() {
        String identificacion = leerTexto("Identificacion a eliminar:");
        boolean exito = ecoRuta.eliminarAportante(identificacion);
        mostrarMensaje(exito ? "Aportante eliminado." : "No existe un aportante con esa identificacion.");
    }

    // ---------- MENU ENTREGAS ----------

    private static void menuEntregas() {
        String opcion;
        do {
            opcion = mostrarMenu("Entregas", new String[]{
                    "Crear entrega", "Agregar lote", "Actualizar lote", "Eliminar lote",
                    "Buscar entrega", "Listar entregas", "Actualizar fecha", "Eliminar entrega",
                    "Aceptar entrega", "Volver"});
            if (opcion == null) {
                opcion = "Volver";
            }
            try {
                switch (opcion) {
                    case "Crear entrega" -> crearEntrega();
                    case "Agregar lote" -> agregarLoteAEntrega();
                    case "Actualizar lote" -> actualizarLote();
                    case "Eliminar lote" -> eliminarLote();
                    case "Buscar entrega" -> buscarEntrega();
                    case "Listar entregas" -> listarEntregas();
                    case "Actualizar fecha" -> actualizarFechaEntrega();
                    case "Eliminar entrega" -> eliminarEntrega();
                    case "Aceptar entrega" -> aceptarEntrega();
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

    private static void actualizarLote() {
        Entrega entrega = seleccionarEntrega();
        String codigoLote = leerTexto("Codigo del lote a actualizar:");
        String nuevaDescripcion = leerTexto("Nueva descripcion:");
        int nuevoPeso = leerEntero("Nuevo peso en kg (entero positivo):");

        boolean exito = entrega.actualizarLote(codigoLote, nuevaDescripcion, nuevoPeso);
        mostrarMensaje(exito ? "Lote actualizado." : "No se pudo actualizar el lote.");
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

    private static void actualizarFechaEntrega() {
        String codigo = leerTexto("Codigo de la entrega:");
        LocalDate nuevaFecha = leerFecha("Nueva fecha (yyyy-MM-dd):");
        boolean exito = ecoRuta.actualizarFechaEntrega(codigo, nuevaFecha);
        mostrarMensaje(exito ? "Fecha actualizada." : "No existe una entrega con ese codigo.");
    }

    private static void eliminarEntrega() {
        String codigo = leerTexto("Codigo de la entrega a eliminar:");
        boolean exito = ecoRuta.eliminarEntrega(codigo);
        mostrarMensaje(exito ? "Entrega eliminada." : "No existe una entrega con ese codigo.");
    }

    private static void aceptarEntrega() {
        String codigo = leerTexto("Codigo de la entrega a aceptar:");
        boolean exito = ecoRuta.aceptarEntrega(codigo);
        if (exito) {
            mostrarMensaje("Entrega aceptada. Comprobante generado:\n" + ecoRuta.consultarComprobante(codigo));
        } else {
            mostrarMensaje("No se pudo aceptar: la entrega no existe, no tiene lotes o ya fue aceptada.");
        }
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
        sb.append("Peso total: ").append(entrega.calcularPesoTotal()).append("kg\n");
        sb.append(String.format("Valor total: $%.2f%n", entrega.calcularValorTotal()));
        sb.append("Lotes:\n");
        if (entrega.getLotes().isEmpty()) {
            sb.append("  (sin lotes)\n");
        } else {
            entrega.getLotes().forEach(lote -> sb.append("  ").append(lote).append("\n"));
        }
        return sb.toString();
    }

    // ---------- MENU CONSULTAS ----------

    private static void menuConsultas() {
        String opcion;
        do {
            opcion = mostrarMenu("Consultas", new String[]{
                    "Consultar comprobante", "Consultar etiquetas", "Consultar valor acumulado", "Volver"});
            if (opcion == null) {
                opcion = "Volver";
            }
            try {
                switch (opcion) {
                    case "Consultar comprobante" -> consultarComprobante();
                    case "Consultar etiquetas" -> consultarEtiquetas();
                    case "Consultar valor acumulado" -> consultarValorAcumulado();
                    case "Volver" -> {
                    }
                }
            } catch (OperacionCancelada e) {
                // operacion cancelada por el usuario, se vuelve al submenu
            }
        } while (!opcion.equals("Volver"));
    }

    private static void consultarComprobante() {
        String codigo = leerTexto("Codigo de la entrega:");
        Comprobante comprobante = ecoRuta.consultarComprobante(codigo);
        mostrarMensaje(comprobante != null ? comprobante.toString() : "No hay comprobante disponible para esa entrega.");
    }

    private static void consultarEtiquetas() {
        String codigo = leerTexto("Codigo de la entrega:");
        ArrayList<String> etiquetas = ecoRuta.consultarEtiquetas(codigo);
        if (etiquetas.isEmpty()) {
            mostrarMensaje("No hay etiquetas para esa entrega.");
        } else {
            mostrarMensaje(String.join("\n", etiquetas));
        }
    }

    private static void consultarValorAcumulado() {
        String idAportante = leerTexto("Identificacion del aportante:");
        double valor = ecoRuta.consultarValorAcumulado(idAportante);
        mostrarMensaje(String.format("Valor acumulado: $%.2f", valor));
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
