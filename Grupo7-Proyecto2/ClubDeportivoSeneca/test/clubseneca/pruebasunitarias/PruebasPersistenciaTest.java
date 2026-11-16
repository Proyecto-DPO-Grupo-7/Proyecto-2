package clubseneca.pruebasunitarias;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.Date;
import java.util.GregorianCalendar;
import clubseneca.deportes.*;
import clubseneca.individual.*;
import clubseneca.principal.ClubDeportivoSeneca;
import clubseneca.torneos.*;
import clubseneca.usuarios.*;

public class PruebasPersistenciaTest {
    private void borrarCarpeta(File carpeta) {
        if (!carpeta.exists()) return;
        File[] archivos = carpeta.listFiles();
        if (archivos != null) {
            for (File archivo : archivos) archivo.delete();
        }
        carpeta.delete();
    }
    @Test
    public void guardaYRecuperaUsuariosYEquiposConReferenciasCoherentes() {
        File carpeta = new File("datos_prueba_integracion");
        borrarCarpeta(carpeta);
        ClubDeportivoSeneca club = new ClubDeportivoSeneca(carpeta.getPath());
        Socio socio = new Socio("socioPersistente","clave",new GregorianCalendar(2000,0,1).getTime());
        club.getGestionUsuarios().agregarUsuario(socio);
        Equipo equipo = new Equipo("Equipo Persistente",10,new Deporte("Futbol",7),new Categoria("Mayores",18,120));
        club.getGestionDeportiva().agregarEquipo(equipo);
        assertTrue(club.getGestionDeportiva().inscribirSocioEnEquipo(socio,equipo,"Defensa",8));
        club.guardarDatos();
        ClubDeportivoSeneca recuperado = new ClubDeportivoSeneca(carpeta.getPath());
        recuperado.cargarDatos();
        Usuario socioRecuperado = recuperado.getGestionUsuarios().autenticar("socioPersistente","clave");
        Equipo equipoRecuperado = recuperado.getGestionDeportiva().buscarEquipo("Equipo Persistente");
        assertNotNull(socioRecuperado);
        assertNotNull(equipoRecuperado);
        assertTrue(equipoRecuperado.getJugadores().get(0).getSocio() == socioRecuperado);
        borrarCarpeta(carpeta);
    }
    @Test
    public void guardaYRecuperaTorneoYBono() {
        File carpeta = new File("datos_prueba_torneos");
        borrarCarpeta(carpeta);
        ClubDeportivoSeneca club = new ClubDeportivoSeneca(carpeta.getPath());
        Socio socio = new Socio("ganador","clave",new GregorianCalendar(2000,0,1).getTime());
        Socio otro = new Socio("otro","clave",new GregorianCalendar(2000,0,1).getTime());
        club.getGestionUsuarios().agregarUsuario(socio);
        club.getGestionUsuarios().agregarUsuario(otro);
        DisciplinaIndividual tenis = new DisciplinaIndividual("Tenis",4);
        club.getGestionDeportiva().agregarDisciplina(tenis);
        club.getGestionDeportiva().inscribirUsuarioEnDisciplina(socio,tenis,"Intermedio");
        club.getGestionDeportiva().inscribirUsuarioEnDisciplina(otro,tenis,"Intermedio");
        TorneoRecreativo torneo = new TorneoRecreativo("Abierto",new Date(),null,null,tenis,"Intermedio",false,5,10000);
        club.getGestionTorneos().agregarTorneo(torneo);
        java.util.List<Usuario> uno = new java.util.ArrayList<>(); uno.add(socio);
        java.util.List<Usuario> dos = new java.util.ArrayList<>(); dos.add(otro);
        InscripcionTorneo primero = club.getGestionTorneos().inscribirIndividual(torneo,uno,club.getGestionDeportiva(),club.getGestionTurnos());
        assertNotNull(primero);
        assertNotNull(club.getGestionTorneos().inscribirIndividual(torneo,dos,club.getGestionDeportiva(),club.getGestionTurnos()));
        assertTrue(torneo.iniciar());
        EncuentroTorneo partido = new EncuentroTorneo(new Date(),primero,torneo.getInscripciones().get(1));
        assertTrue(torneo.registrarEncuentro(partido));
        assertTrue(partido.registrarMarcador(2,1));
        assertTrue(torneo.finalizar(primero));
        club.guardarDatos();
        ClubDeportivoSeneca recuperado = new ClubDeportivoSeneca(carpeta.getPath());
        recuperado.cargarDatos();
        Socio recuperadoSocio = (Socio) recuperado.getGestionUsuarios().buscarUsuario("ganador");
        assertNotNull(recuperado.getGestionTorneos().buscarTorneo("Abierto"));
        assertNotNull(recuperadoSocio.buscarBonoDisponible("Abierto"));
        borrarCarpeta(carpeta);
    }
}
