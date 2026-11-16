package clubseneca.pruebasunitarias;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.Date;
import java.util.GregorianCalendar;
import clubseneca.deportes.*;
import clubseneca.gestion.*;
import clubseneca.individual.*;
import clubseneca.usuarios.*;

public class PruebasEntrenamientosTest {
    private GestionDeportiva gestion;
    private Entrenador entrenador;
    private Socio socio;
    private Equipo equipo;
    private Instalacion cancha;
    private Date fecha;
    @BeforeEach
    public void preparar() {
        gestion = new GestionDeportiva();
        entrenador = new Entrenador("ent","clave","COD");
        socio = new Socio("socio","clave",new GregorianCalendar(2000,0,1).getTime());
        Deporte futbol = new Deporte("Futbol",7);
        equipo = new Equipo("Seneca",15,futbol,new Categoria("Mayores",18,110));
        equipo.agregarJugador(socio,"Delantero",10);
        DisciplinaIndividual tenis = new DisciplinaIndividual("Tenis",4);
        cancha = new Instalacion("Cancha",4,false,tenis);
        gestion.agregarDisciplina(tenis);
        fecha = new GregorianCalendar(2027,0,10).getTime();
    }
    @Test
    public void huEnt01ProgramaSesionConEntrenadorEInstalacion() {
        SesionEntrenamiento sesion = new SesionEntrenamiento(fecha,"10:00",60,entrenador,cancha,equipo);
        assertTrue(gestion.programarSesion(sesion));
        assertEquals(1,gestion.getSesionesEntrenamiento().size());
    }
    @Test
    public void huEnt01RechazaSesionConDuracionInvalida() {
        SesionEntrenamiento sesion = new SesionEntrenamiento(fecha,"10:00",0,entrenador,cancha,equipo);
        assertFalse(gestion.programarSesion(sesion));
    }
    @Test
    public void huEnt01SoloConvocaJugadoresDelEquipo() {
        SesionEntrenamiento sesion = new SesionEntrenamiento(fecha,"10:00",60,entrenador,cancha,equipo);
        Socio noPertenece = new Socio("extra","clave",new GregorianCalendar(2000,0,1).getTime());
        assertTrue(sesion.convocarSocio(socio));
        assertFalse(sesion.convocarSocio(noPertenece));
    }
    @Test
    public void huEnt02RegistraAsistenciaDeConvocado() {
        SesionEntrenamiento sesion = new SesionEntrenamiento(fecha,"10:00",60,entrenador,cancha,equipo);
        sesion.convocarSocio(socio);
        assertTrue(sesion.registrarAsistencia(new Asistencia(sesion,socio,"ASISTIO")));
        assertEquals("ASISTIO",sesion.getAsistencias().get(0).getEstado());
    }
    @Test
    public void huEnt02RechazaAsistenciaDeNoConvocado() {
        SesionEntrenamiento sesion = new SesionEntrenamiento(fecha,"10:00",60,entrenador,cancha,equipo);
        assertFalse(sesion.registrarAsistencia(new Asistencia(sesion,socio,"ASISTIO")));
    }
    @Test
    public void huEnt02RechazaEstadoInvalido() {
        SesionEntrenamiento sesion = new SesionEntrenamiento(fecha,"10:00",60,entrenador,cancha,equipo);
        sesion.convocarSocio(socio);
        assertFalse(sesion.registrarAsistencia(new Asistencia(sesion,socio,"DESCONOCIDO")));
    }
    @Test
    public void huEnt03AceptaEvaluacionValida() {
        SesionEntrenamiento sesion = new SesionEntrenamiento(fecha,"10:00",60,entrenador,cancha,equipo);
        Asistencia asistencia = new Asistencia(sesion,socio,"ASISTIO");
        assertTrue(asistencia.registrarEvaluacion(5,"Muy bien"));
        assertTrue(asistencia.tieneEvaluacion());
    }
    @Test
    public void huEnt03EvaluacionEsOpcional() {
        SesionEntrenamiento sesion = new SesionEntrenamiento(fecha,"10:00",60,entrenador,cancha,equipo);
        Asistencia asistencia = new Asistencia(sesion,socio,"ASISTIO");
        assertFalse(asistencia.tieneEvaluacion());
    }
    @Test
    public void huEnt03RechazaNotaFueraDeRango() {
        SesionEntrenamiento sesion = new SesionEntrenamiento(fecha,"10:00",60,entrenador,cancha,equipo);
        Asistencia asistencia = new Asistencia(sesion,socio,"ASISTIO");
        assertFalse(asistencia.registrarEvaluacion(6,"Nota invalida"));
    }
    @Test
    public void huEnt04ExigeMinimoHabilitadosParaPartido() {
        assertFalse(equipo.puedeJugarPartido());
        for (int i=0;i<6;i++) {
            Socio nuevo = new Socio("jug"+i,"clave",new GregorianCalendar(2000,0,1).getTime());
            assertTrue(equipo.agregarJugador(nuevo,"Jugador",i+1));
        }
        assertTrue(equipo.puedeJugarPartido());
    }
    @Test
    public void huEnt04SuspensionImpideJugar() {
        for (int i=0;i<6;i++) {
            Socio nuevo = new Socio("jug"+i,"clave",new GregorianCalendar(2000,0,1).getTime());
            equipo.agregarJugador(nuevo,"Jugador",i+1);
        }
        assertTrue(equipo.puedeJugarPartido());
        JugadorEquipo jugador = equipo.buscarJugador(socio);
        jugador.registrarRoja();
        assertFalse(equipo.puedeJugarPartido());
    }
}
