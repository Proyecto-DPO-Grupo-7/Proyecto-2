package clubseneca.pruebasunitarias;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import clubseneca.comercial.*;
import clubseneca.deportes.*;
import clubseneca.gestion.*;
import clubseneca.individual.*;
import clubseneca.principal.ClubDeportivoSeneca;
import clubseneca.torneos.*;
import clubseneca.turnos.*;
import clubseneca.usuarios.*;

public class PruebasIntegracionRequerimientosTest {
    private ClubDeportivoSeneca club;
    private Socio socio;
    private Administrador admin;
    private Date fecha;
    private Deporte futbol;
    private Categoria mayores;
    @BeforeEach
    public void preparar() {
        club = new ClubDeportivoSeneca();
        socio = new Socio("socio","clave",new GregorianCalendar(2000,0,1).getTime());
        admin = new Administrador("admin","clave");
        fecha = new GregorianCalendar(2027,0,4).getTime();
        futbol = new Deporte("Futbol",7);
        mayores = new Categoria("Mayores",18,120);
        club.getGestionUsuarios().agregarUsuario(socio);
        club.getGestionUsuarios().agregarUsuario(admin);
    }
    private Equipo crearEquipo(String nombre) {
        Equipo equipo = new Equipo(nombre,15,futbol,mayores);
        club.getGestionDeportiva().agregarEquipo(equipo);
        return equipo;
    }
    private void completarEquipo(Equipo equipo, String prefijo) {
        for (int i=0;i<7;i++) {
            Socio jugador = new Socio(prefijo+i,"clave",new GregorianCalendar(2000,0,1).getTime());
            assertTrue(equipo.agregarJugador(jugador,"Jugador",i+1));
        }
    }
    @Test
    public void rfSoc01InscripcionValidaCategoriaCupoYCamiseta() {
        Equipo equipo = crearEquipo("Seneca");
        assertTrue(club.getGestionDeportiva().inscribirSocioEnEquipo(socio,equipo,"Delantero",10));
        Socio otro = new Socio("otro","c",new GregorianCalendar(2000,0,1).getTime());
        assertFalse(equipo.agregarJugador(otro,"Defensa",10));
        assertEquals(1,equipo.getJugadores().size());
    }
    @Test
    public void rfSoc02RegistraPartidoInternoYEstadistica() {
        Equipo a=crearEquipo("A"); Equipo b=crearEquipo("B");
        completarEquipo(a,"a"); completarEquipo(b,"b");
        PartidoInterno partido=new PartidoInterno(fecha,a,b);
        assertTrue(club.getGestionDeportiva().registrarPartido(partido));
        partido.registrarMarcador(2,1);
        EstadisticaFutbol estadistica = new EstadisticaFutbol(a.getJugadores().get(0),partido);
        estadistica.registrarGol();
        partido.agregarEstadistica(estadistica);
        assertEquals(1,estadistica.getGoles());
        assertEquals(1,club.getGestionDeportiva().getPartidos().size());
    }
    @Test
    public void rfSoc03InscribirAcademiaAsignaNivel() {
        DisciplinaIndividual tenis = new DisciplinaIndividual("Tenis",2);
        club.getGestionDeportiva().agregarDisciplina(tenis);
        InscripcionDisciplina inscripcion = club.getGestionDeportiva().inscribirUsuarioEnDisciplina(socio,tenis,"Intermedio");
        assertNotNull(inscripcion);
        assertEquals("Intermedio",inscripcion.getNivel());
        assertNull(club.getGestionDeportiva().inscribirUsuarioEnDisciplina(socio,tenis,"Intermedio"));
    }
    @Test
    public void rfSoc06ActualizaRankingTrasPartidoPractica() {
        DisciplinaIndividual tenis = new DisciplinaIndividual("Tenis",2);
        Socio otro = new Socio("otro","clave",new GregorianCalendar(2000,0,1).getTime());
        InscripcionDisciplina uno = club.getGestionDeportiva().inscribirUsuarioEnDisciplina(socio,tenis,"Intermedio");
        InscripcionDisciplina dos = club.getGestionDeportiva().inscribirUsuarioEnDisciplina(otro,tenis,"Intermedio");
        PartidoPractica partido = new PartidoPractica(fecha,tenis);
        assertTrue(partido.agregarParticipante(uno));
        assertTrue(partido.agregarParticipante(dos));
        EstadisticaTenisPadel primero = new EstadisticaTenisPadel(uno,partido);
        EstadisticaTenisPadel segundo = new EstadisticaTenisPadel(dos,partido);
        primero.registrarSetGanado();
        partido.agregarEstadistica(primero);
        partido.agregarEstadistica(segundo);
        partido.actualizarRankings();
        assertTrue(uno.getRankingElo()>1000);
    }
    @Test
    public void rfSoc10PuntosSePuedenRedimirDespues() {
        socio.agregarPuntos(5000);
        Venta venta = new Venta(fecha,socio);
        venta.agregarDetalle(new Bebida("Agua",3000,false),1);
        assertTrue(venta.redimirPuntos(1000));
        assertTrue(club.registrarCompra(socio,venta,null));
        assertTrue(socio.getPuntosFidelidad()<5000);
    }
    @Test
    public void rfEmp06RegistraEstadisticasFutbol() {
        Equipo equipo = crearEquipo("Seneca");
        equipo.agregarJugador(socio,"Delantero",10);
        EstadisticaFutbol estadistica = new EstadisticaFutbol(equipo.getJugadores().get(0),null);
        estadistica.registrarGol();
        estadistica.registrarAsistencia();
        assertEquals(1,estadistica.getGoles());
        assertEquals(1,estadistica.getAsistencias());
    }
    @Test
    public void rfEmp07ConsultaTurnosDeFisioterapeuta() {
        Fisioterapeuta f = new Fisioterapeuta("fisio","c","F1");
        club.getGestionTurnos().asignarTurno(new TurnoSemanal("LUNES","08:00","12:00",f));
        assertEquals(1,club.getGestionTurnos().getTurnosEmpleado(f).size());
    }
    @Test
    public void rfAdm01CreaYDadoBajaSocio() {
        assertTrue(club.getGestionUsuarios().darDeBajaSocio("socio"));
        assertFalse(socio.isActivo());
        assertFalse(club.getGestionUsuarios().darDeBajaSocio("nobody"));
    }
    @Test
    public void rfAdm02InscribeEquipoCompetenciaExterna() {
        Equipo equipo=crearEquipo("A");
        completarEquipo(equipo,"jug");
        Competencia liga = new Competencia("Liga","Universidad",2027,"Liga",futbol,mayores);
        club.getGestionDeportiva().registrarCompetencia(liga);
        assertNotNull(club.getGestionDeportiva().inscribirEquipoEnCompetencia(equipo,liga,fecha,"A"));
        assertEquals(1,liga.getInscripciones().size());
    }
    @Test
    public void rfAdm03ConsultaStockDeArticulos() {
        ArticuloTienda articulo=new ArticuloTienda("Camiseta",10000,"Ropa");
        UbicacionInventario bodega=new UbicacionInventario("Bodega");
        club.getGestionComercial().agregarProducto(articulo);
        club.getGestionComercial().agregarUbicacion(bodega);
        assertTrue(club.getGestionComercial().reabastecerArticulo(articulo,bodega,10));
        assertEquals(10,club.getGestionComercial().buscarStock(articulo,bodega).getCantidad());
    }
    @Test
    public void rfAdm04MueveArticulosEntreUbicaciones() {
        ArticuloTienda articulo = new ArticuloTienda("Raqueta",10000,"Ropa");
        UbicacionInventario a=new UbicacionInventario("Bodega"), b=new UbicacionInventario("Tienda");
        club.getGestionComercial().agregarProducto(articulo);
        club.getGestionComercial().agregarUbicacion(a);
        club.getGestionComercial().agregarUbicacion(b);
        club.getGestionComercial().reabastecerArticulo(articulo,a,10);
        assertTrue(club.getGestionComercial().moverArticulo(articulo,a,b,4));
        assertEquals(6,a.buscarStock(articulo).getCantidad());
        assertEquals(4,b.buscarStock(articulo).getCantidad());
    }
    @Test
    public void rfAdm05ApruebaSugerencias() {
        Entrenador entrenador = new Entrenador("ent","c","E1");
        SugerenciaProducto sugerencia = new SugerenciaProducto(fecha,"Snack","Fruta","Fruta fresca",entrenador);
        club.getGestionComercial().registrarSugerencia(sugerencia);
        club.getGestionComercial().aprobarSugerencia(sugerencia,admin);
        assertEquals("APROBADA",sugerencia.getEstado());
    }
    @Test
    public void rfAdm06AdministraTurnosDeEmpleados() {
        Entrenador entrenador = new Entrenador("ent","c","E1");
        TurnoSemanal turno = new TurnoSemanal("LUNES","08:00","12:00",entrenador);
        club.getGestionTurnos().asignarTurno(turno);
        assertEquals(1,club.getGestionTurnos().getTurnosEmpleado(entrenador).size());
    }
    @Test
    public void rfAdm07ApruebaCambioCuandoCumpleCobertura() {
        Entrenador e1=new Entrenador("e1","c","1");
        Entrenador e2=new Entrenador("e2","c","2");
        Entrenador e3=new Entrenador("e3","c","3");
        Entrenador e4=new Entrenador("e4","c","4");
        Entrenador e5=new Entrenador("e5","c","5");
        Fisioterapeuta f1=new Fisioterapeuta("f1","c","6");
        Fisioterapeuta f2=new Fisioterapeuta("f2","c","7");
        TurnoSemanal actual=new TurnoSemanal("LUNES","08:00","12:00",e1);
        TurnoSemanal propuesto=new TurnoSemanal("LUNES","12:00","16:00",e1);
        club.getGestionTurnos().asignarTurno(actual);
        club.getGestionTurnos().asignarTurno(new TurnoSemanal("LUNES","08:00","12:00",e2));
        club.getGestionTurnos().asignarTurno(new TurnoSemanal("LUNES","08:00","12:00",e3));
        club.getGestionTurnos().asignarTurno(new TurnoSemanal("LUNES","08:00","12:00",f1));
        club.getGestionTurnos().asignarTurno(new TurnoSemanal("LUNES","12:00","16:00",e4));
        club.getGestionTurnos().asignarTurno(new TurnoSemanal("LUNES","12:00","16:00",e5));
        club.getGestionTurnos().asignarTurno(new TurnoSemanal("LUNES","12:00","16:00",f2));
        SolicitudCambioTurno cambio = new SolicitudCambioTurno(fecha,e1,actual,propuesto);
        club.getGestionTurnos().registrarSolicitud(cambio);
        assertTrue(club.getGestionTurnos().aprobarSolicitud(cambio,admin));
        assertEquals("APROBADA",cambio.getEstado());
    }
    @Test
    public void rfAdm08ConsultaHistorialReservasYPartidos() {
        DisciplinaIndividual tenis=new DisciplinaIndividual("Tenis",4);
        Instalacion cancha=new Instalacion("Cancha",4,false,tenis);
        club.getGestionDeportiva().agregarDisciplina(tenis);
        club.getGestionDeportiva().inscribirUsuarioEnDisciplina(socio,tenis,"Intermedio");
        assertNotNull(club.getGestionDeportiva().reservarInstalacion(socio,cancha,fecha,"08:00","09:00",2,"Sencillos"));
        assertEquals(1,club.getGestionDeportiva().getReservas().size());
    }
    @Test
    public void rfAdm09ConsultaReporteMensualidades() {
        club.getGestionComercial().registrarMensualidad(new PagoMensualidad(fecha,50000,socio));
        assertEquals(50000,club.getGestionComercial().calcularVentasPorRubro("mensualidades",new GregorianCalendar(2027,0,1).getTime(),new GregorianCalendar(2027,0,31).getTime()),0.01);
    }
    @Test
    public void rfAdm10ConsultaReporteDeEvolucionRankings() {
        DisciplinaIndividual tenis=new DisciplinaIndividual("Tenis",4);
        club.getGestionDeportiva().agregarDisciplina(tenis);
        InscripcionDisciplina inscripcion=club.getGestionDeportiva().inscribirUsuarioEnDisciplina(socio,tenis,"Avanzado");
        inscripcion.ajustarRanking(30);
        assertEquals(1030,club.getGestionDeportiva().getDisciplinas().get(0).getInscripciones().get(0).getRankingElo(),0.01);
    }
    @Test
    public void torneoSeleccionaMejorRankingEnCupoPreferencial() {
        DisciplinaIndividual tenis = new DisciplinaIndividual("Tenis",4);
        club.getGestionDeportiva().agregarDisciplina(tenis);
        InscripcionDisciplina mejor=club.getGestionDeportiva().inscribirUsuarioEnDisciplina(socio,tenis,"Intermedio");
        mejor.ajustarRanking(100);
        Socio otro=new Socio("otro","c",new GregorianCalendar(2000,0,1).getTime());
        club.getGestionDeportiva().inscribirUsuarioEnDisciplina(otro,tenis,"Intermedio");
        TorneoRecreativo torneo=new TorneoRecreativo("Torneo Ranking",fecha,null,null,tenis,"Intermedio",false,5,5000);
        club.getGestionTorneos().agregarTorneo(torneo);
        java.util.List<Usuario> lista=new ArrayList<>();lista.add(socio);
        InscripcionTorneo inscrito=club.getGestionTorneos().inscribirIndividual(torneo,lista,club.getGestionDeportiva(),club.getGestionTurnos());
        assertNotNull(inscrito);
        assertTrue(inscrito.isPreferencial());
    }
    @Test
    public void torneoPermiteEquipoExternoConNOMinaCompleta() {
        TorneoRecreativo torneo=new TorneoRecreativo("Torneo Externo",fecha,futbol,mayores,null,null,false,5,40000);
        assertTrue(club.getGestionTorneos().agregarTorneo(torneo));
        EquipoInvitadoTorneo invitado=new EquipoInvitadoTorneo("Visitantes",futbol,mayores);
        for (int i=0;i<7;i++) {
            assertTrue(invitado.agregarJugador(new InvitadoTorneo("externo"+i,"clave","EXTERNO","Intermedio")));
        }
        InscripcionTorneo inscripcion=club.getGestionTorneos().inscribirEquipoInvitado(torneo,invitado);
        assertNotNull(inscripcion);
        assertEquals(7,inscripcion.getJugadores().size());
    }
    @Test
    public void invitadoGanadorRecibeBonoQuePuedeRedimir() {
        InvitadoTorneo externo = new InvitadoTorneo("externo","clave","EXTERNO","Intermedio");
        InvitadoTorneo otro = new InvitadoTorneo("otro","clave","ESTUDIANTE","Intermedio");
        DisciplinaIndividual tenis = new DisciplinaIndividual("Tenis",4);
        club.getGestionDeportiva().agregarDisciplina(tenis);
        TorneoRecreativo torneo = new TorneoRecreativo("Copa",fecha,null,null,tenis,"Intermedio",false,4,6000);
        club.getGestionTorneos().agregarTorneo(torneo);
        java.util.List<Usuario> lista = new ArrayList<>(); lista.add(externo);
        InscripcionTorneo ganador = club.getGestionTorneos().inscribirIndividual(torneo,lista,club.getGestionDeportiva(),club.getGestionTurnos());
        lista.clear();lista.add(otro);
        assertNotNull(club.getGestionTorneos().inscribirIndividual(torneo,lista,club.getGestionDeportiva(),club.getGestionTurnos()));
        assertTrue(torneo.iniciar());
        EncuentroTorneo partido = new EncuentroTorneo(fecha,ganador,torneo.getInscripciones().get(1));
        assertTrue(torneo.registrarEncuentro(partido));
        assertTrue(partido.registrarMarcador(2,1));
        assertTrue(torneo.finalizar(ganador));
        BonoDescuento bono = externo.buscarBonoDisponible("Copa");
        assertNotNull(bono);
        Venta venta = new Venta(fecha,externo);
        venta.agregarDetalle(new Bebida("Agua",10000,false),1);
        assertTrue(venta.aplicarBono(bono));
        assertTrue(club.registrarCompra(externo,venta,null));
        assertTrue(bono.isUsado());
    }
}
