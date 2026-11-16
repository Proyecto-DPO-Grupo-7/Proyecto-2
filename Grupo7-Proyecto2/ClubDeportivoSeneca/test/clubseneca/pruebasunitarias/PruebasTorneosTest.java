package clubseneca.pruebasunitarias;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.util.GregorianCalendar;
import clubseneca.deportes.Categoria;
import clubseneca.deportes.Deporte;
import clubseneca.deportes.Equipo;
import clubseneca.gestion.GestionDeportiva;
import clubseneca.gestion.GestionTorneos;
import clubseneca.gestion.GestionTurnos;
import clubseneca.individual.DisciplinaIndividual;
import clubseneca.torneos.*;
import clubseneca.turnos.TurnoSemanal;
import clubseneca.usuarios.*;

public class PruebasTorneosTest {
    private GestionTorneos gestion;
    private GestionDeportiva deportiva;
    private GestionTurnos turnos;
    private DisciplinaIndividual tenis;
    private Socio socio1;
    private Socio socio2;
    private Date fecha;

    @BeforeEach
    public void preparar() {
        gestion = new GestionTorneos();
        deportiva = new GestionDeportiva();
        turnos = new GestionTurnos();
        tenis = new DisciplinaIndividual("Tenis",4);
        deportiva.agregarDisciplina(tenis);
        socio1 = new Socio("s1","clave",new GregorianCalendar(2000,0,1).getTime());
        socio2 = new Socio("s2","clave",new GregorianCalendar(2000,0,1).getTime());
        deportiva.inscribirUsuarioEnDisciplina(socio1,tenis,"Intermedio");
        deportiva.inscribirUsuarioEnDisciplina(socio2,tenis,"Intermedio");
        fecha = new GregorianCalendar(2027,Calendar.JANUARY,4).getTime();
    }
    private TorneoRecreativo recreativo(int cupos) {
        TorneoRecreativo t = new TorneoRecreativo("Copa Recreativa",fecha,null,null,tenis,"Intermedio",false,cupos,20000);
        assertTrue(gestion.agregarTorneo(t));
        return t;
    }
    private TorneoCompetitivo competitivo() {
        TorneoCompetitivo t = new TorneoCompetitivo("Copa Competitiva",fecha,null,null,tenis,"Intermedio",false,5,40000,0.50);
        assertTrue(gestion.agregarTorneo(t));
        return t;
    }
    private InscripcionTorneo registrar(Torneo torneo,Usuario socio) {
        List<Usuario> lista = new ArrayList<>();
        lista.add(socio);
        return gestion.inscribirIndividual(torneo,lista,deportiva,turnos);
    }
    @Test
    public void noPermiteDosTorneosConMismoNombre() {
        recreativo(5);
        assertFalse(gestion.agregarTorneo(new TorneoRecreativo("Copa Recreativa",fecha,null,null,tenis,"Intermedio",false,5,20000)));
    }
    @Test
    public void reservaVeintePorCientoRedondeadoHaciaArriba() {
        assertEquals(2,recreativo(6).getCuposPreferenciales());
    }
    @Test
    public void permiteInscripcionSinCostoEnRecreativo() {
        InscripcionTorneo inscrito = registrar(recreativo(5),socio1);
        assertNotNull(inscrito);
        assertEquals(0,inscrito.getTarifa(),0.001);
    }
    @Test
    public void cobraInscripcionCompetitivaYCalculaRecaudo() {
        TorneoCompetitivo t = competitivo();
        assertNotNull(registrar(t,socio1));
        assertNotNull(registrar(t,socio2));
        assertEquals(80000,t.getTotalRecaudado(),0.001);
    }
    @Test
    public void rechazaParticipantesNoInscritosEnDisciplina() {
        TorneoRecreativo t = recreativo(5);
        Socio otro = new Socio("otro","c",fecha);
        assertNull(registrar(t,otro));
    }
    @Test
    public void rechazaNivelIncorrecto() {
        TorneoRecreativo t = new TorneoRecreativo("Copa Avanzada",fecha,null,null,tenis,"Avanzado",false,5,10000);
        assertTrue(gestion.agregarTorneo(t));
        assertNull(registrar(t,socio1));
    }
    @Test
    public void rechazaInscripcionRepetida() {
        TorneoRecreativo t = recreativo(5);
        assertNotNull(registrar(t,socio1));
        assertNull(registrar(t,socio1));
    }
    @Test
    public void reservaUnCupoParaDestacado() {
        TorneoRecreativo t = recreativo(3);
        assertNotNull(registrar(t,socio1));
        assertNotNull(registrar(t,socio2));
        Socio tercero = new Socio("s3","clave",fecha);
        deportiva.inscribirUsuarioEnDisciplina(tercero,tenis,"Intermedio");
        assertNull(registrar(t,tercero));
        assertTrue(t.registrarDestacado("s3"));
        assertNotNull(registrar(t,tercero));
        assertTrue(t.getInscripciones().get(2).isPreferencial());
    }
    @Test
    public void liberaCuposPreferencialesNoOcupados() {
        TorneoRecreativo t = recreativo(3);
        assertNotNull(registrar(t,socio1));
        assertNotNull(registrar(t,socio2));
        Socio tercero = new Socio("s3","clave",fecha);
        deportiva.inscribirUsuarioEnDisciplina(tercero,tenis,"Intermedio");
        assertNull(registrar(t,tercero));
        assertTrue(t.liberarPreferenciales());
        assertNotNull(registrar(t,tercero));
    }
    @Test
    public void retiraAntesDeIniciarYLiberarCupos() {
        TorneoRecreativo t = recreativo(3);
        assertNotNull(registrar(t,socio1));
        assertEquals(1,t.getCuposOcupados());
        assertTrue(gestion.retirar(t,"s1"));
        assertEquals(0,t.getCuposOcupados());
    }
    @Test
    public void noSePuedeRetirarLuegoDeIniciar() {
        TorneoRecreativo t = recreativo(3);
        registrar(t,socio1); registrar(t,socio2);
        assertTrue(t.iniciar());
        assertFalse(t.retirar("s1"));
    }
    @Test
    public void guardaEncuentroYResultadoEnHistorial() {
        TorneoRecreativo t = recreativo(3);
        InscripcionTorneo a=registrar(t,socio1); InscripcionTorneo b=registrar(t,socio2);
        assertTrue(t.iniciar());
        EncuentroTorneo encuentro = new EncuentroTorneo(fecha,a,b);
        assertTrue(t.registrarEncuentro(encuentro));
        assertTrue(encuentro.registrarMarcador(6,4));
        assertTrue(t.finalizar(a));
        assertEquals("FINALIZADO",t.getEstado());
        assertEquals(1,t.getEncuentros().size());
        assertEquals(1,a.getPosicion());
    }
    @Test
    public void entregaBonoAlSocioGanador() {
        TorneoRecreativo t = recreativo(3);
        InscripcionTorneo a = registrar(t,socio1); registrar(t,socio2);
        assertTrue(t.iniciar());
        EncuentroTorneo partido = new EncuentroTorneo(fecha,a,t.getInscripciones().get(1));
        assertTrue(t.registrarEncuentro(partido));
        assertTrue(partido.registrarMarcador(2,1));
        assertTrue(t.finalizar(a));
        assertEquals(1,socio1.getBonos().size());
        assertEquals(20000,socio1.getBonos().get(0).getValor(),0.001);
        assertEquals(0,socio2.getBonos().size());
    }
    @Test
    public void empleadoCompiteGratisFueraDelTurnoYSinPremio() {
        TorneoCompetitivo t=competitivo();
        Entrenador entrenador = new Entrenador("e1","clave","EM1");
        assertNotNull(registrar(t,entrenador));
        assertEquals(0,t.getInscripciones().get(0).getTarifa(),0.001);
        registrar(t,socio1);
        assertTrue(t.iniciar());
        EncuentroTorneo partido = new EncuentroTorneo(fecha,t.getInscripciones().get(0),t.getInscripciones().get(1));
        assertTrue(t.registrarEncuentro(partido));
        assertTrue(partido.registrarMarcador(2,1));
        assertTrue(t.finalizar(t.getInscripciones().get(0)));
        assertEquals(0,t.getPremioEfectivo(),0.001);
        assertEquals(0,t.premioPara(entrenador),0.001);
    }
    @Test
    public void empleadoDeTurnoNoSePuedeInscribir() {
        Entrenador entrenador=new Entrenador("ent","clave","EM1");
        turnos.asignarTurno(new TurnoSemanal("LUNES","08:00","16:00",entrenador));
        assertNull(registrar(competitivo(),entrenador));
    }
    @Test
    public void permiteParticiparExternoYEstudiante() {
        TorneoRecreativo t = recreativo(4);
        InvitadoTorneo externo=new InvitadoTorneo("externo","clave","EXTERNO","Intermedio");
        InvitadoTorneo estudiante=new InvitadoTorneo("estudiante","clave","ESTUDIANTE","Intermedio");
        assertNotNull(registrar(t,externo));
        assertNotNull(registrar(t,estudiante));
    }
    @Test
    public void validaDoblesYNoPermiteJugadorRepetido() {
        TorneoRecreativo t = new TorneoRecreativo("Dobles",fecha,null,null,tenis,"Intermedio",true,5,30000);
        assertTrue(gestion.agregarTorneo(t));
        assertNull(registrar(t,socio1));
        List<Usuario> pareja = new ArrayList<>(); pareja.add(socio1); pareja.add(socio2);
        assertNotNull(gestion.inscribirIndividual(t,pareja,deportiva,turnos));
        assertNull(gestion.inscribirIndividual(t,pareja,deportiva,turnos));
    }
    @Test
    public void noAceptaEquipoDeOtroDeporte() {
        TorneoRecreativo t = new TorneoRecreativo("Copa Futbol",fecha,new Deporte("Futbol",7),new Categoria("Mayores",18,120),null,null,false,4,50000);
        assertTrue(gestion.agregarTorneo(t));
        Equipo equipo = new Equipo("Basket",12,new Deporte("Baloncesto",5),new Categoria("Mayores",18,120));
        assertNull(gestion.inscribirEquipo(t,equipo));
    }
    @Test
    public void noAceptaEquipoSinMinimoDeHabilitados() {
        TorneoRecreativo t = new TorneoRecreativo("Copa Futbol",fecha,new Deporte("Futbol",7),new Categoria("Mayores",18,120),null,null,false,4,50000);
        assertTrue(gestion.agregarTorneo(t));
        Equipo equipo = new Equipo("Futbol",15,new Deporte("Futbol",7),new Categoria("Mayores",18,120));
        assertNull(gestion.inscribirEquipo(t,equipo));
    }
    @Test
    public void premioCompetitivoEsPorcentajeDelRecaudo() {
        TorneoCompetitivo t=competitivo();
        InscripcionTorneo a=registrar(t,socio1);registrar(t,socio2);
        assertTrue(t.iniciar());
        EncuentroTorneo partido = new EncuentroTorneo(fecha,a,t.getInscripciones().get(1));
        assertTrue(t.registrarEncuentro(partido));
        assertTrue(partido.registrarMarcador(2,1));
        assertTrue(t.finalizar(a));
        assertEquals(40000,t.getPremioEfectivo(),0.001);
        assertEquals(40000,t.premioPara(socio1),0.001);
    }
}
