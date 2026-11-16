package clubseneca.consola;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Calendar;
import clubseneca.comercial.ArticuloTienda;
import clubseneca.comercial.Bebida;
import clubseneca.comercial.Snack;
import clubseneca.comercial.UbicacionInventario;
import clubseneca.deportes.Categoria;
import clubseneca.deportes.Deporte;
import clubseneca.deportes.Equipo;
import clubseneca.individual.DisciplinaIndividual;
import clubseneca.individual.Instalacion;
import clubseneca.principal.ClubDeportivoSeneca;
import clubseneca.usuarios.Administrador;
import clubseneca.usuarios.Entrenador;
import clubseneca.usuarios.Fisioterapeuta;
import clubseneca.usuarios.Socio;
import java.util.Arrays;

public class DatosIniciales {
    public static void preparar(ClubDeportivoSeneca club) {
        if (!club.getGestionUsuarios().getUsuarios().isEmpty()) return;
        club.getGestionUsuarios().agregarUsuario(new Administrador("admin", "admin123"));
        club.getGestionUsuarios().agregarUsuario(new Socio("socio", "socio123", new GregorianCalendar(2000, Calendar.JANUARY, 1).getTime()));
        club.getGestionUsuarios().agregarUsuario(new Entrenador("entrenador", "entrenador123", "ENT08"));
        club.getGestionUsuarios().agregarUsuario(new Fisioterapeuta("fisio", "fisio123", "FIS08"));
        DisciplinaIndividual tenis = new DisciplinaIndividual("Tenis", 4);
        DisciplinaIndividual padel = new DisciplinaIndividual("Padel", 2);
        DisciplinaIndividual mesa = new DisciplinaIndividual("Tenis de mesa", 4);
        tenis.agregarInstalacion(new Instalacion("Cancha 1", 4, false, tenis));
        padel.agregarInstalacion(new Instalacion("Padel 1", 4, true, padel));
        mesa.agregarInstalacion(new Instalacion("Mesa 1", 4, true, mesa));
        club.getGestionDeportiva().agregarDisciplina(tenis);
        club.getGestionDeportiva().agregarDisciplina(padel);
        club.getGestionDeportiva().agregarDisciplina(mesa);
        club.getGestionDeportiva().inscribirUsuarioEnDisciplina(club.getGestionUsuarios().buscarUsuario("socio"), tenis, "Intermedio");
        Deporte futbol = new Deporte("Futbol", 7);
        Categoria mayores = new Categoria("Mayores", 18, 110);
        club.getGestionDeportiva().agregarEquipo(new Equipo("Seneca A", 20, futbol, mayores));
        UbicacionInventario bodega = new UbicacionInventario("Bodega");
        club.getGestionComercial().agregarUbicacion(bodega);
        ArticuloTienda pelota = new ArticuloTienda("Pelota", 35000, "Deportes");
        club.getGestionComercial().agregarProducto(pelota);
        club.getGestionComercial().reabastecerArticulo(pelota, bodega, 20);
        club.getGestionComercial().agregarProducto(new Bebida("Cafe", 6500, true));
        club.getGestionComercial().agregarProducto(new Bebida("Agua", 3500, false));
        club.getGestionComercial().agregarProducto(new Snack("Barra", 4500, Arrays.asList("Nueces")));
    }
}
