package clubseneca.consola;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import clubseneca.comercial.ArticuloTienda;
import clubseneca.comercial.Producto;
import clubseneca.comercial.UbicacionInventario;
import clubseneca.comercial.Venta;
import clubseneca.individual.DisciplinaIndividual;
import clubseneca.individual.Instalacion;
import clubseneca.principal.ClubDeportivoSeneca;
import clubseneca.torneos.InscripcionTorneo;
import clubseneca.torneos.Torneo;
import clubseneca.usuarios.Socio;
import clubseneca.usuarios.InvitadoTorneo;
import clubseneca.usuarios.Usuario;

public abstract class ConsolaBase {
    protected Scanner lector;
    protected ClubDeportivoSeneca club;
    protected Usuario usuario;

    protected ConsolaBase() {
        lector = new Scanner(System.in);
        club = new ClubDeportivoSeneca();
        club.cargarDatos();
        System.out.println("Datos guardados en: " + new java.io.File("datos").getAbsolutePath());
        DatosIniciales.preparar(club);
    }
    protected boolean ingresar(String tipo) {
        System.out.println("=== CLUB DEPORTIVO SENECA ===");
        System.out.println("Usuario:");
        String login = leerTexto();
        System.out.println("Contraseña:");
        String clave = lector.nextLine();
        Usuario encontrado = club.getGestionUsuarios().autenticar(login, clave);
        if (encontrado == null) {
            System.out.println("Usuario o contraseña incorrectos.");
            return false;
        }
        if (tipo.equals("ADMIN") && !encontrado.esAdministrador()) {
            System.out.println("Esta consola es para administradores.");
            return false;
        }
        if (tipo.equals("SOCIO") && !encontrado.esSocio() && !encontrado.esInvitadoTorneo()) {
            System.out.println("Esta consola es para socios e invitados a torneos.");
            return false;
        }
        if (tipo.equals("EMPLEADO") && !encontrado.esEmpleado()) {
            System.out.println("Esta consola es para empleados.");
            return false;
        }
        usuario = encontrado;
        System.out.println("Bienvenido, " + usuario.getLogin());
        return true;
    }
    protected void finalizar() {
        club.guardarDatos();
        System.out.println("Datos guardados. Hasta luego.");
    }
    protected String leerTexto() {
        String texto = lector.nextLine().trim();
        while (texto.isEmpty()) {
            System.out.println("El dato no puede estar vacio. Intenta otra vez:");
            texto = lector.nextLine().trim();
        }
        return texto;
    }
    protected int leerEntero(int minimo, int maximo) {
        while (true) {
            try {
                int numero = Integer.parseInt(leerTexto());
                if (numero >= minimo && numero <= maximo) return numero;
            } catch (NumberFormatException e) {
                System.out.println("Debes escribir un numero entero.");
            }
            System.out.println("Ingresa un numero entre " + minimo + " y " + maximo + ":");
        }
    }
    protected double leerDecimal(double minimo, double maximo) {
        while (true) {
            try {
                double numero = Double.parseDouble(leerTexto());
                if (numero >= minimo && numero <= maximo) return numero;
            } catch (NumberFormatException e) {
                System.out.println("Debes escribir un numero valido.");
            }
            System.out.println("Ingresa un valor entre " + minimo + " y " + maximo + ":");
        }
    }
    protected Date leerFecha() {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        formato.setLenient(false);
        while (true) {
            System.out.println("Fecha (AAAA-MM-DD):");
            try { return formato.parse(leerTexto()); }
            catch (ParseException e) { System.out.println("Fecha invalida."); }
        }
    }
    protected String leerHora() {
        while (true) {
            System.out.println("Hora (HH:mm):");
            String hora = leerTexto();
            if (hora.length() == 5 && hora.charAt(2) == ':') {
                try {
                    int horas = Integer.parseInt(hora.substring(0,2));
                    int minutos = Integer.parseInt(hora.substring(3,5));
                    if (horas >= 0 && horas <= 23 && minutos >= 0 && minutos <= 59) return hora;
                } catch (NumberFormatException e) {
                    System.out.println("La hora debe tener numeros.");
                }
            }
            System.out.println("Hora incorrecta.");
        }
    }
    protected String leerDia() {
        while (true) {
            System.out.println("Dia (LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO):");
            String dia = leerTexto();
            if (dia.equalsIgnoreCase("LUNES") || dia.equalsIgnoreCase("MARTES")
                || dia.equalsIgnoreCase("MIERCOLES") || dia.equalsIgnoreCase("JUEVES")
                || dia.equalsIgnoreCase("VIERNES") || dia.equalsIgnoreCase("SABADO")
                || dia.equalsIgnoreCase("DOMINGO")) return dia;
            System.out.println("Dia invalido.");
        }
    }
    protected String leerNivel() {
        while (true) {
            System.out.println("Nivel (Principiante, Intermedio o Avanzado):");
            String nivel = leerTexto();
            if (nivel.equalsIgnoreCase("Principiante") || nivel.equalsIgnoreCase("Intermedio") || nivel.equalsIgnoreCase("Avanzado")) return nivel;
            System.out.println("Nivel invalido.");
        }
    }
    protected DisciplinaIndividual seleccionarDisciplina() {
        List<DisciplinaIndividual> lista = club.getGestionDeportiva().getDisciplinas();
        if (lista.isEmpty()) { System.out.println("No hay disciplinas."); return null; }
        for (int i = 0; i < lista.size(); i++) System.out.println((i + 1) + ". " + lista.get(i).getNombre());
        int opcion = leerEntero(1, lista.size());
        return lista.get(opcion - 1);
    }
    protected Instalacion seleccionarInstalacion(DisciplinaIndividual disciplina) {
        if (disciplina == null) return null;
        List<Instalacion> lista = disciplina.getInstalaciones();
        if (lista.isEmpty()) { System.out.println("No hay instalaciones."); return null; }
        for (int i = 0; i < lista.size(); i++) System.out.println((i + 1) + ". " + lista.get(i).getNombre());
        return lista.get(leerEntero(1, lista.size()) - 1);
    }
    protected Torneo seleccionarTorneo() {
        List<Torneo> lista = club.getGestionTorneos().getTorneos();
        if (lista.isEmpty()) { System.out.println("No hay torneos creados."); return null; }
        for (int i = 0; i < lista.size(); i++) {
            Torneo torneo = lista.get(i);
            System.out.println((i + 1) + ". " + torneo.getNombre() + " - " + torneo.getTipo() + " - " + torneo.getEstado());
        }
        return lista.get(leerEntero(1, lista.size()) - 1);
    }
    protected void mostrarTorneo(Torneo torneo) {
        if (torneo == null) return;
        System.out.println("Torneo: " + torneo.getNombre());
        System.out.println("Tipo: " + torneo.getTipo() + " | Estado: " + torneo.getEstado());
        System.out.println("Cupos: " + torneo.getCuposOcupados() + "/" + torneo.getCupos() + " (preferenciales: " + torneo.getCuposPreferenciales() + ")");
        System.out.println("Participantes:");
        for (InscripcionTorneo actual : torneo.getInscripciones()) {
            System.out.println(" - " + actual.getIdentificador() + " | Posicion: " + actual.getPosicion() + " | Tarifa: " + actual.getTarifa());
        }
        System.out.println("Encuentros:");
        for (clubseneca.torneos.EncuentroTorneo encuentro : torneo.getEncuentros()) {
            String marcador = "Pendiente";
            if (encuentro.isFinalizado()) marcador = encuentro.getMarcadorPrimero() + " - " + encuentro.getMarcadorSegundo();
            System.out.println(" - " + encuentro.getPrimero().getIdentificador() + " vs " + encuentro.getSegundo().getIdentificador() + ": " + marcador);
        }
        if (torneo.getGanador() != null) System.out.println("Ganador: " + torneo.getGanador().getIdentificador());
    }
    protected void realizarCompra() {
        if (!usuario.esSocio() && !usuario.esEmpleado() && !usuario.esInvitadoTorneo()) {
            System.out.println("Esta compra requiere ser socio o empleado."); return;
        }
        List<Producto> productos = club.getGestionComercial().getProductos();
        if (productos.isEmpty()) { System.out.println("No hay productos."); return; }
        for (int i = 0; i < productos.size(); i++) {
            Producto producto = productos.get(i);
            System.out.println((i+1) + ". " + producto.getNombre() + " ($" + producto.getPrecio() + ")");
            if (producto.esSnack()) {
                System.out.println("   Alergenos: " + ((clubseneca.comercial.Snack) producto).getAlergenos());
            }
        }
        Producto producto = productos.get(leerEntero(1, productos.size()) - 1);
        if (producto.esSnack()) {
            if (((clubseneca.comercial.Snack) producto).getAlergenos().isEmpty()) {
                System.out.println("No se puede comprar: faltan alergenos."); return;
            }
            System.out.println("¿Confirmas los alergenos? 1=Si 2=No");
            if (leerEntero(1, 2) == 2) return;
        }
        Venta venta = new Venta(new Date(), usuario);
        System.out.println("Cantidad:");
        int cantidad = leerEntero(1, 10000);
        venta.agregarDetalle(producto, cantidad);
        if (usuario.esEmpleado()) venta.aplicarDescuentoEmpleado();
        if (usuario.esInvitadoTorneo()) {
            InvitadoTorneo invitado = (InvitadoTorneo) usuario;
            if (!invitado.getBonos().isEmpty()) {
                System.out.println("¿Aplicar bono? 1=Si 2=No");
                if (leerEntero(1,2) == 1) {
                    System.out.println("Nombre del torneo que genero el bono:");
                    if (!venta.aplicarBono(invitado.buscarBonoDisponible(leerTexto()))) System.out.println("Bono no disponible.");
                }
            }
        }
        if (usuario.esSocio()) {
            Socio socio = (Socio) usuario;
            if (!socio.getBonos().isEmpty()) {
                System.out.println("¿Aplicar bono? 1=Si 2=No");
                if (leerEntero(1,2) == 1) {
                    System.out.println("Nombre del torneo que genero el bono:");
                    if (!venta.aplicarBono(socio.buscarBonoDisponible(leerTexto()))) System.out.println("Bono no disponible.");
                }
            }
            System.out.println("¿Usar puntos? 1=Si 2=No");
            if (leerEntero(1,2) == 1) {
                System.out.println("Puntos a redimir (disponibles: " + socio.getPuntosFidelidad() + "):");
                if (!venta.redimirPuntos(leerDecimal(0, 10000000))) System.out.println("No se pudieron aplicar puntos.");
            }
        }
        if (producto.esProductoCafeteria()) {
            System.out.println("Propina sugerida 10%. Indica porcentaje entre 0 y 100:");
            venta.configurarPropina(leerDecimal(0, 100) / 100);
        }
        System.out.println("Total con impuestos y descuentos: $" + venta.calcularTotal());
        System.out.println("¿Confirmar? 1=Si 2=No");
        if (leerEntero(1,2) == 2) return;
        Instalacion instalacionActual = null;
        if (venta.contieneBebidaCaliente() && usuario.esSocio()) {
            System.out.println("¿Estas dentro de una instalacion restringida? 1=Si 2=No");
            if (leerEntero(1,2) == 1) {
                System.out.println("Compra rechazada: no se permiten bebidas calientes alli."); return;
            }
        }
        boolean exito = club.registrarCompra(usuario, venta, instalacionActual);
        if (exito) System.out.println("Compra registrada.");
        else System.out.println("Compra rechazada: revisa el inventario y las reglas.");
    }
}
