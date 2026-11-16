package clubseneca.pruebasunitarias;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import clubseneca.comercial.*;
import clubseneca.gestion.*;
import clubseneca.individual.*;
import clubseneca.principal.ClubDeportivoSeneca;
import clubseneca.torneos.BonoDescuento;
import clubseneca.usuarios.*;

public class PruebasSociosTest {
    private ClubDeportivoSeneca club;
    private Socio socio;
    private DisciplinaIndividual tenis;
    private Instalacion cancha;
    private Date fecha;

    @BeforeEach
    public void preparar() {
        club = new ClubDeportivoSeneca();
        fecha = new GregorianCalendar(2027,0,2).getTime();
        socio = new Socio("socio","clave",new GregorianCalendar(2000,0,1).getTime());
        club.getGestionUsuarios().agregarUsuario(socio);
        tenis = new DisciplinaIndividual("Tenis",1);
        cancha = new Instalacion("Cancha 1",4,false,tenis);
        tenis.agregarInstalacion(cancha);
        club.getGestionDeportiva().agregarDisciplina(tenis);
        club.getGestionDeportiva().inscribirUsuarioEnDisciplina(socio,tenis,"Intermedio");
    }
    @Test
    public void huSoc01PermiteReservarInstalacionDisponible() {
        Reserva r = club.getGestionDeportiva().reservarInstalacion(socio,cancha,fecha,"08:00","09:00",2,"Sencillos");
        assertNotNull(r);
        assertEquals(1,club.getGestionDeportiva().getReservas().size());
    }
    @Test
    public void huSoc01RechazaReservaSimultanea() {
        assertNotNull(club.getGestionDeportiva().reservarInstalacion(socio,cancha,fecha,"08:00","09:00",2,"Sencillos"));
        assertNull(club.getGestionDeportiva().reservarInstalacion(socio,cancha,fecha,"08:30","09:30",2,"Sencillos"));
    }
    @Test
    public void huSoc01RechazaCapacidadIncorrecta() {
        assertNull(club.getGestionDeportiva().reservarInstalacion(socio,cancha,fecha,"08:00","09:00",5,"Dobles"));
    }
    @Test
    public void huSoc01RechazaReservaSinInscripcion() {
        Socio otro = new Socio("otro","clave",fecha);
        assertNull(club.getGestionDeportiva().reservarInstalacion(otro,cancha,fecha,"08:00","09:00",2,"Sencillos"));
    }
    @Test
    public void huSoc02PermitePracticaEntreNivelesCompatibles() {
        Socio otro = new Socio("otro","clave",fecha);
        club.getGestionDeportiva().inscribirUsuarioEnDisciplina(otro,tenis,"Principiante");
        PartidoPractica partido = new PartidoPractica(fecha,tenis);
        partido.agregarParticipante(club.getGestionDeportiva().buscarInscripcion(socio,tenis));
        partido.agregarParticipante(club.getGestionDeportiva().buscarInscripcion(otro,tenis));
        assertTrue(club.getGestionDeportiva().programarPartidoPractica(partido));
    }
    @Test
    public void huSoc02RechazaPracticaEntreNivelesMuyDistintos() {
        Socio otro = new Socio("otro","clave",fecha);
        club.getGestionDeportiva().inscribirUsuarioEnDisciplina(otro,tenis,"Principiante");
        club.getGestionDeportiva().buscarInscripcion(socio,tenis).cambiarNivel("Avanzado");
        PartidoPractica partido = new PartidoPractica(fecha,tenis);
        partido.agregarParticipante(club.getGestionDeportiva().buscarInscripcion(socio,tenis));
        partido.agregarParticipante(club.getGestionDeportiva().buscarInscripcion(otro,tenis));
        assertFalse(club.getGestionDeportiva().programarPartidoPractica(partido));
    }
    @Test
    public void huSoc02PermiteExcepcionConEntrenador() {
        Entrenador entrenador = new Entrenador("ent","clave","COD");
        InscripcionDisciplina i = club.getGestionDeportiva().inscribirUsuarioEnDisciplina(entrenador,tenis,"Avanzado");
        club.getGestionDeportiva().buscarInscripcion(socio,tenis).cambiarNivel("Principiante");
        PartidoPractica partido = new PartidoPractica(fecha,tenis);
        partido.agregarParticipante(i);
        partido.agregarParticipante(club.getGestionDeportiva().buscarInscripcion(socio,tenis));
        assertTrue(club.getGestionDeportiva().programarPartidoPractica(partido));
    }
    @Test
    public void huSoc03CompraArticuloConIvaYActualizaInventario() {
        ArticuloTienda pelota = new ArticuloTienda("Pelota",10000,"Deportes");
        UbicacionInventario bodega = new UbicacionInventario("Bodega");
        club.getGestionComercial().agregarProducto(pelota);
        club.getGestionComercial().agregarUbicacion(bodega);
        assertTrue(club.getGestionComercial().reabastecerArticulo(pelota,bodega,3));
        Venta venta = new Venta(fecha,socio);
        venta.agregarDetalle(pelota,2);
        assertEquals(23800,venta.calcularTotal(),0.01);
        assertTrue(club.registrarCompra(socio,venta,null));
        assertEquals(1,bodega.buscarStock(pelota).getCantidad());
        assertTrue(socio.getPuntosFidelidad()>0);
    }
    @Test
    public void huSoc03RechazaCompraSinStock() {
        ArticuloTienda pelota = new ArticuloTienda("Pelota",10000,"Deportes");
        club.getGestionComercial().agregarProducto(pelota);
        Venta venta = new Venta(fecha,socio);
        venta.agregarDetalle(pelota,1);
        assertFalse(club.registrarCompra(socio,venta,null));
    }
    @Test
    public void huSoc04PermiteConsultarAlergenosAntesDeComprar() {
        Snack snack = new Snack("Barra",1000,Arrays.asList("Nueces","Leche"));
        assertTrue(snack.tieneAlergeno("Nueces"));
        assertEquals(2,snack.getAlergenos().size());
        Venta venta = new Venta(fecha,socio);
        venta.agregarDetalle(snack,1);
        assertTrue(club.registrarCompra(socio,venta,null));
    }
    @Test
    public void huSoc04NoPermiteComprarSnackSinAlergenosInformados() {
        Snack snack = new Snack("Barra",1000,new ArrayList<String>());
        Venta venta = new Venta(fecha,socio);
        venta.agregarDetalle(snack,1);
        assertFalse(club.registrarCompra(socio,venta,null));
    }
    @Test
    public void huSoc05RechazaBebidaCalienteEnPadel() {
        DisciplinaIndividual padel = new DisciplinaIndividual("Padel",2);
        Instalacion restringida = new Instalacion("Padel 1",4,true,padel);
        Bebida cafe = new Bebida("Cafe",5000,true);
        Venta venta = new Venta(fecha,socio);
        venta.agregarDetalle(cafe,1);
        assertFalse(club.registrarCompra(socio,venta,restringida));
    }
    @Test
    public void huSoc05NoDejaEntrarConBebidaCaliente() {
        DisciplinaIndividual padel = new DisciplinaIndividual("Padel",2);
        Instalacion restringida = new Instalacion("Padel 1",4,false,padel);
        socio.setTieneBebidaCaliente(true);
        assertFalse(club.getGestionDeportiva().puedeUsarInstalacion(socio,restringida));
        assertTrue(club.getGestionDeportiva().puedeUsarInstalacion(socio,cancha));
    }
    @Test
    public void bonoNoSeAcumulaConOtrosDescuentos() {
        BonoDescuento bono = new BonoDescuento("Copa",socio,5000);
        Venta venta = new Venta(fecha,socio);
        venta.agregarDetalle(new ArticuloTienda("Camiseta",10000,"Ropa"),1);
        assertTrue(venta.aplicarBono(bono));
        assertFalse(venta.redimirPuntos(100));
        assertEquals(6900,venta.calcularTotal(),0.01);
    }
    @Test
    public void bonoSoloSeConsumeEnVentaExitosa() {
        BonoDescuento bono = new BonoDescuento("Copa",socio,5000);
        ArticuloTienda articulo = new ArticuloTienda("Pelota",10000,"Deportes");
        Venta venta = new Venta(fecha,socio);
        venta.agregarDetalle(articulo,1);
        assertTrue(venta.aplicarBono(bono));
        assertFalse(club.registrarCompra(socio,venta,null));
        assertFalse(bono.isUsado());
        club.getGestionComercial().agregarProducto(articulo);
        UbicacionInventario bodega=new UbicacionInventario("Bodega");
        club.getGestionComercial().agregarUbicacion(bodega);
        club.getGestionComercial().reabastecerArticulo(articulo,bodega,1);
        assertTrue(club.registrarCompra(socio,venta,null));
        assertTrue(bono.isUsado());
    }
}
