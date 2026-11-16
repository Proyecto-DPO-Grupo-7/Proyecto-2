package clubseneca.pruebasunitarias;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.Date;
import java.util.GregorianCalendar;
import clubseneca.comercial.*;
import clubseneca.gestion.*;
import clubseneca.turnos.*;
import clubseneca.usuarios.*;

public class PruebasAdministracionTest {
    private GestionUsuarios usuarios;
    private GestionTurnos turnos;
    private GestionComercial comercial;
    private Administrador admin;
    private Fisioterapeuta fisio;
    private Date fecha;
    @BeforeEach
    public void preparar() {
        usuarios = new GestionUsuarios();
        turnos = new GestionTurnos();
        comercial = new GestionComercial();
        admin = new Administrador("admin","clave");
        fisio = new Fisioterapeuta("fisio","clave","F1");
        fecha = new GregorianCalendar(2027,0,4).getTime();
    }
    @Test
    public void huFis01RegistraSolicitudSiTieneTurno() {
        TurnoSemanal actual=new TurnoSemanal("LUNES","08:00","12:00",fisio);
        TurnoSemanal propuesto=new TurnoSemanal("LUNES","12:00","16:00",fisio);
        turnos.asignarTurno(actual);
        SolicitudCambioTurno s=new SolicitudCambioTurno(fecha,fisio,actual,propuesto);
        turnos.registrarSolicitud(s);
        assertEquals(1,turnos.getSolicitudes().size());
        assertEquals("PENDIENTE",s.getEstado());
    }
    @Test
    public void huAdm01RechazaCambioQueDejaSinCobertura() {
        Entrenador e1 = new Entrenador("e1","c","E1");
        Entrenador e2 = new Entrenador("e2","c","E2");
        TurnoSemanal a = new TurnoSemanal("LUNES","08:00","12:00",fisio);
        turnos.asignarTurno(a);
        turnos.asignarTurno(new TurnoSemanal("LUNES","08:00","12:00",e1));
        turnos.asignarTurno(new TurnoSemanal("LUNES","08:00","12:00",e2));
        SolicitudCambioTurno s=new SolicitudCambioTurno(fecha,fisio,a,new TurnoSemanal("LUNES","12:00","16:00",fisio));
        turnos.registrarSolicitud(s);
        assertFalse(turnos.aprobarSolicitud(s,admin));
        assertEquals("RECHAZADA",s.getEstado());
    }
    @Test
    public void huAdm03ReabasteceArticuloRegistrado() {
        ArticuloTienda articulo = new ArticuloTienda("Camiseta",10000,"Ropa");
        UbicacionInventario bodega = new UbicacionInventario("Bodega");
        comercial.agregarProducto(articulo);
        comercial.agregarUbicacion(bodega);
        assertTrue(comercial.reabastecerArticulo(articulo,bodega,3));
        assertEquals(3,bodega.buscarStock(articulo).getCantidad());
        assertTrue(comercial.reabastecerArticulo(articulo,bodega,2));
        assertEquals(5,bodega.buscarStock(articulo).getCantidad());
    }
    @Test
    public void huAdm03RechazaReabastecerArticuloInexistente() {
        ArticuloTienda articulo = new ArticuloTienda("Camiseta",10000,"Ropa");
        UbicacionInventario bodega = new UbicacionInventario("Bodega");
        comercial.agregarUbicacion(bodega);
        assertFalse(comercial.reabastecerArticulo(articulo,bodega,5));
    }
    @Test
    public void huAdm02ConsultaVentasPorRubroPeriodo() {
        Socio socio=new Socio("socio","clave",fecha);
        Venta venta=new Venta(fecha,socio);
        venta.agregarDetalle(new Bebida("Agua",5000,false),1);
        assertTrue(comercial.registrarVenta(venta));
        Date antes = new GregorianCalendar(2027,0,1).getTime();
        Date despues = new GregorianCalendar(2027,0,31).getTime();
        assertEquals(5000,comercial.calcularVentasPorRubro("cafeteria",antes,despues),0.01);
        assertEquals(0,comercial.calcularVentasPorRubro("tienda",antes,despues),0.01);
    }
    @Test
    public void gestionUsuariosAutenticaCorrectamente() {
        Socio socio=new Socio("socio","clave",fecha);
        assertTrue(usuarios.agregarUsuario(socio));
        assertEquals(socio,usuarios.autenticar("socio","clave"));
        assertNull(usuarios.autenticar("socio","otra"));
    }
    @Test
    public void loginDuplicadoSeRechaza() {
        assertTrue(usuarios.agregarUsuario(admin));
        assertFalse(usuarios.agregarUsuario(new Administrador("admin","otra")));
    }
    @Test
    public void descuentoEmpleadoSeAplicaSoloParaEmpleado() {
        Empleado empleado = new Entrenador("e","clave","E1");
        Venta venta = new Venta(fecha,empleado);
        venta.agregarDetalle(new ArticuloTienda("Pantalon",10000,"Ropa"),1);
        assertTrue(venta.aplicarDescuentoEmpleado());
        assertEquals(1500,venta.calcularDescuento(),0.01);
    }
    @Test
    public void puedeAprobarSugerenciasDeProductos() {
        Empleado empleado = new Entrenador("e","clave","E1");
        SugerenciaProducto sugerencia = new SugerenciaProducto(fecha,"Snack","Barra","Sin azucar",empleado);
        comercial.registrarSugerencia(sugerencia);
        comercial.aprobarSugerencia(sugerencia,admin);
        assertEquals("APROBADA",sugerencia.getEstado());
    }
}
