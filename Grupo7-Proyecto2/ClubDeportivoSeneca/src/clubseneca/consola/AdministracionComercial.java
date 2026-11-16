package clubseneca.consola;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import clubseneca.comercial.ArticuloTienda;
import clubseneca.comercial.Bebida;
import clubseneca.comercial.Producto;
import clubseneca.comercial.Snack;
import clubseneca.comercial.StockArticulo;
import clubseneca.comercial.SugerenciaProducto;
import clubseneca.comercial.UbicacionInventario;
import clubseneca.comercial.Venta;
import clubseneca.individual.DisciplinaIndividual;
import clubseneca.individual.Reserva;
import clubseneca.individual.SesionEntrenamiento;
import clubseneca.usuarios.Administrador;

public class AdministracionComercial {
    private ConsolaAdministrador c;
    public AdministracionComercial(ConsolaAdministrador c) { this.c = c; }
    public void menu() {
        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n=== ADMINISTRACION COMERCIAL ===");
            System.out.println("1. Crear producto");
            System.out.println("2. Crear ubicacion de inventario");
            System.out.println("3. Reabastecer articulo");
            System.out.println("4. Mover articulo entre ubicaciones");
            System.out.println("5. Consultar inventario");
            System.out.println("6. Aprobar o rechazar sugerencia");
            System.out.println("0. Volver");
            opcion = c.leerEntero(0,6);
            if (opcion == 1) producto();
            if (opcion == 2) ubicacion();
            if (opcion == 3) reabastecer();
            if (opcion == 4) mover();
            if (opcion == 5) inventario();
            if (opcion == 6) sugerencias();
        }
    }
    private void producto() {
        System.out.println("Tipo 1=Articulo, 2=Bebida, 3=Snack");
        int tipo = c.leerEntero(1,3);
        System.out.println("Nombre:");
        String nombre = c.leerTexto();
        System.out.println("Precio:");
        double precio = c.leerDecimal(1,100000000);
        if (tipo == 1) {
            System.out.println("Categoria:");
            c.club.getGestionComercial().agregarProducto(new ArticuloTienda(nombre,precio,c.leerTexto()));
        }
        if (tipo == 2) {
            System.out.println("¿Es caliente? 1=Si, 2=No");
            c.club.getGestionComercial().agregarProducto(new Bebida(nombre,precio,c.leerEntero(1,2)==1));
        }
        if (tipo == 3) {
            System.out.println("¿Cuantos alergenos? (0 si no contiene ninguno):");
            int cantidad = c.leerEntero(0,30);
            List<String> alergenos = new ArrayList<>();
            for (int i=0; i<cantidad; i++) { System.out.println("Alergeno:"); alergenos.add(c.leerTexto()); }
            c.club.getGestionComercial().agregarProducto(new Snack(nombre,precio,alergenos));
        }
        System.out.println("Producto procesado.");
    }
    private void ubicacion() {
        System.out.println("Nombre de la ubicacion:");
        c.club.getGestionComercial().agregarUbicacion(new UbicacionInventario(c.leerTexto()));
        System.out.println("Ubicacion agregada.");
    }
    private ArticuloTienda elegirArticulo() {
        System.out.println("Nombre del articulo:");
        Producto producto = c.club.getGestionComercial().buscarProducto(c.leerTexto());
        if (producto == null || !producto.esArticuloTienda()) {
            System.out.println("Articulo no registrado."); return null;
        }
        return (ArticuloTienda) producto;
    }
    private UbicacionInventario elegirUbicacion() {
        List<UbicacionInventario> lista = c.club.getGestionComercial().getUbicaciones();
        if (lista.isEmpty()) { System.out.println("No hay ubicaciones."); return null; }
        for (int i=0; i<lista.size(); i++) System.out.println((i+1)+". "+lista.get(i).getNombre());
        return lista.get(c.leerEntero(1,lista.size())-1);
    }
    private void reabastecer() {
        ArticuloTienda articulo = elegirArticulo();
        if (articulo == null) return;
        UbicacionInventario ubicacion = elegirUbicacion();
        if (ubicacion == null) return;
        System.out.println("Unidades a agregar:");
        int cantidad = c.leerEntero(1,1000000);
        c.club.getGestionComercial().reabastecerArticulo(articulo,ubicacion,cantidad);
        System.out.println("Inventario actualizado.");
    }
    private void mover() {
        ArticuloTienda articulo = elegirArticulo();
        if (articulo == null) return;
        System.out.println("Ubicacion origen:");
        UbicacionInventario origen = elegirUbicacion();
        System.out.println("Ubicacion destino:");
        UbicacionInventario destino = elegirUbicacion();
        if (origen == null || destino == null) return;
        System.out.println("Unidades a mover:");
        int cantidad = c.leerEntero(1,1000000);
        if (c.club.getGestionComercial().moverArticulo(articulo,origen,destino,cantidad)) System.out.println("Movimiento realizado.");
        else System.out.println("No se pudo mover: revisa existencias.");
    }
    private void inventario() {
        for (UbicacionInventario ubicacion : c.club.getGestionComercial().getUbicaciones()) {
            System.out.println("Ubicacion: " + ubicacion.getNombre());
            for (StockArticulo stock : ubicacion.getStocks()) {
                System.out.println(" - " + stock.getArticulo().getNombre() + ": " + stock.getCantidad());
            }
        }
    }
    private void sugerencias() {
        List<SugerenciaProducto> lista = c.club.getGestionComercial().getSugerencias();
        if (lista.isEmpty()) { System.out.println("No hay sugerencias."); return; }
        for (int i=0; i<lista.size(); i++) System.out.println((i+1)+". "+lista.get(i).getNombrePropuesto()+" - "+lista.get(i).getEstado());
        SugerenciaProducto sugerencia = lista.get(c.leerEntero(1,lista.size())-1);
        System.out.println("1 Aprobar, 2 Rechazar:");
        int accion = c.leerEntero(1,2);
        if (accion == 1) c.club.getGestionComercial().aprobarSugerencia(sugerencia,(Administrador)c.usuario);
        else c.club.getGestionComercial().rechazarSugerencia(sugerencia,(Administrador)c.usuario);
        System.out.println("Estado: " + sugerencia.getEstado());
    }
    public void reportes() {
        System.out.println("\n=== REPORTES ===");
        System.out.println("1 Ventas por rubro y periodo, 2 Reservas, 3 Partidos, 4 Entrenamientos, 5 Rankings");
        int opcion = c.leerEntero(1,5);
        if (opcion == 1) {
            System.out.println("Rubro (tienda, cafeteria, mensualidades):");
            String rubro = c.leerTexto();
            System.out.println("Fecha inicial:");
            Date desde = c.leerFecha();
            System.out.println("Fecha final:");
            Date hasta = c.leerFecha();
            if (hasta.before(desde)) { System.out.println("Fechas invertidas."); return; }
            System.out.println("Ventas: $"+c.club.getGestionComercial().calcularVentasPorRubro(rubro,desde,hasta));
        }
        if (opcion == 2) {
            for (Reserva reserva : c.club.getGestionDeportiva().getReservas()) {
                System.out.println(reserva.getSocio().getLogin()+" - "+reserva.getInstalacion().getNombre()+" - "+reserva.getFecha());
            }
        }
        if (opcion == 3) {
            for (clubseneca.deportes.Partido partido : c.club.getGestionDeportiva().getPartidos()) {
                System.out.println(partido.getFecha()+" "+partido.getMarcadorEquipo1()+" - "+partido.getMarcadorEquipo2());
            }
        }
        if (opcion == 4) {
            for (SesionEntrenamiento sesion : c.club.getGestionDeportiva().getSesionesEntrenamiento()) {
                System.out.println(sesion.getFecha()+" "+sesion.getEntrenador().getLogin()+" - Convocados: "+sesion.getConvocados().size()+" - Asistencias: "+sesion.getAsistencias().size());
            }
        }
        if (opcion == 5) {
            for (DisciplinaIndividual d : c.club.getGestionDeportiva().getDisciplinas()) {
                System.out.println(d.getNombre());
                for (clubseneca.individual.InscripcionDisciplina i : d.getInscripciones()) {
                    System.out.println(" - " + i.getTitular().getLogin()+": "+i.getRankingElo());
                }
            }
        }
    }
}
