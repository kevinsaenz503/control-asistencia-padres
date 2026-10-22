package com.saenz.login.controller;

import com.saenz.login.model.Asistencia;
import com.saenz.login.model.LoginModel;
import com.saenz.login.model.Padre;
import com.saenz.login.model.Reunion;
import com.saenz.login.model.User;
import com.saenz.login.repository.AsistenciaRepository;
import com.saenz.login.repository.DatabaseConnectionException;
import com.saenz.login.repository.PadreRepository;
import com.saenz.login.repository.ReunionRepository;
import com.saenz.login.repository.UserRepository;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalTime;
import java.util.List;

@Controller
public class LoginWebController {

    private final UserRepository userRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final PadreRepository padreRepository;
    private final ReunionRepository reunionRepository;

    public LoginWebController(UserRepository userRepository, AsistenciaRepository asistenciaRepository,
                              PadreRepository padreRepository, ReunionRepository reunionRepository) {
        this.userRepository = userRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.padreRepository = padreRepository;
        this.reunionRepository = reunionRepository;
    }

    @GetMapping("/") public String root(){ return "redirect:/login"; }

    @GetMapping("/login")
    public String showLogin(Model model){
        if(!model.containsAttribute("loginModel")) model.addAttribute("loginModel", new LoginModel());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginModel loginModel, Model model, HttpSession session){
        if(loginModel.getUsername()==null || loginModel.getPassword()==null ||
           loginModel.getUsername().isBlank() || loginModel.getPassword().isBlank()){
            model.addAttribute("error","Ingrese usuario y contraseña"); return "login";
        }
        try {
            User user=userRepository.authenticate(loginModel.getUsername(), loginModel.getPassword());
            if(user==null){ model.addAttribute("error","Usuario no encontrado o contraseña incorrecta"); return "login"; }
            session.setAttribute("user", user); return "redirect:/home";
        } catch(DatabaseConnectionException ex){
            ex.printStackTrace(); model.addAttribute("error","No se pudo consultar la base de datos PostgreSQL"); return "login";
        }
    }

    @GetMapping("/home")
    public String home(Model model, HttpSession session){
        User user=getUser(session); if(user==null) return "redirect:/login";
        model.addAttribute("username",user.getUsername());
        try {
            model.addAttribute("usuarios",userRepository.findAll());
            // El dashboard muestra los datos reales acumulados de PostgreSQL.
            model.addAttribute("asistencias", asistenciaRepository.findAll());
            model.addAttribute("totalPadres", padreRepository.count());
            model.addAttribute("asistenciasHoy", asistenciaRepository.countAll());
            model.addAttribute("presentes", asistenciaRepository.countPresent());
            model.addAttribute("ausentes", asistenciaRepository.countAbsent());
        } catch(Exception ex){
            ex.printStackTrace();
            model.addAttribute("usuarios",List.of()); model.addAttribute("asistencias",List.of());
            model.addAttribute("totalPadres",0); model.addAttribute("asistenciasHoy",0);
            model.addAttribute("presentes",0); model.addAttribute("ausentes",0);
            model.addAttribute("dashboardError","No se pudieron cargar algunos datos de PostgreSQL.");
        }
        return "home";
    }

    // ==================== PADRES ====================
    @GetMapping("/padres")
    public String padres(Model model, HttpSession session){
        User user=getUser(session); if(user==null) return "redirect:/login";
        model.addAttribute("username",user.getUsername());
        try { model.addAttribute("padres",padreRepository.findAll()); }
        catch(Exception ex){ ex.printStackTrace(); model.addAttribute("padres",List.of()); model.addAttribute("error","No se pudieron cargar los padres."); }
        return "padres";
    }

    @GetMapping("/padres/agregar")
    public String agregarPadreForm(Model model,HttpSession session){
        User user=getUser(session); if(user==null) return "redirect:/login";
        model.addAttribute("username",user.getUsername()); model.addAttribute("padre",new Padre());
        return "agregar-padre";
    }

    @GetMapping("/padres/editar/{id}")
    public String editarPadreForm(@PathVariable Long id, Model model,HttpSession session,RedirectAttributes redirect){
        User user=getUser(session); if(user==null) return "redirect:/login";
        return padreRepository.findById(id).map(p->{ model.addAttribute("username",user.getUsername()); model.addAttribute("padre",p); return "agregar-padre"; })
                .orElseGet(()->{ redirect.addFlashAttribute("error","Padre no encontrado."); return "redirect:/padres"; });
    }

    @PostMapping("/padres/guardar")
    public String guardarPadre(@ModelAttribute Padre padre,HttpSession session,RedirectAttributes redirect){
        if(getUser(session)==null) return "redirect:/login";
        try { padreRepository.save(padre); redirect.addFlashAttribute("success", padre.getIdPadre()==null?"Padre agregado correctamente.":"Padre actualizado correctamente."); }
        catch(Exception ex){ ex.printStackTrace(); redirect.addFlashAttribute("error",ex.getMessage()); }
        return "redirect:/padres";
    }

    @PostMapping("/padres/eliminar/{id}")
    public String eliminarPadre(@PathVariable Long id,HttpSession session,RedirectAttributes redirect){
        if(getUser(session)==null) return "redirect:/login";
        try { padreRepository.deleteById(id); redirect.addFlashAttribute("success","Padre eliminado correctamente."); }
        catch(Exception ex){ ex.printStackTrace(); redirect.addFlashAttribute("error",ex.getMessage()); }
        return "redirect:/padres";
    }

    // ==================== REUNIONES ====================
    @GetMapping("/reuniones")
    public String reuniones(Model model,HttpSession session){
        User user=getUser(session); if(user==null) return "redirect:/login";
        model.addAttribute("username",user.getUsername());
        try { model.addAttribute("reuniones",reunionRepository.findAll()); }
        catch(Exception ex){ ex.printStackTrace(); model.addAttribute("reuniones",List.of()); model.addAttribute("error","No se pudieron cargar las reuniones."); }
        return "reuniones";
    }

    @GetMapping("/reuniones/agregar")
    public String agregarReunionForm(Model model,HttpSession session){
        User user=getUser(session); if(user==null) return "redirect:/login";
        model.addAttribute("username",user.getUsername()); model.addAttribute("reunion",new Reunion());
        return "agregar-reunion";
    }

    @GetMapping("/reuniones/editar/{id}")
    public String editarReunionForm(@PathVariable Long id,Model model,HttpSession session,RedirectAttributes redirect){
        User user=getUser(session); if(user==null) return "redirect:/login";
        return reunionRepository.findById(id).map(r->{ model.addAttribute("username",user.getUsername()); model.addAttribute("reunion",r); return "agregar-reunion"; })
                .orElseGet(()->{ redirect.addFlashAttribute("error","Reunión no encontrada."); return "redirect:/reuniones"; });
    }

    @PostMapping("/reuniones/guardar")
    public String guardarReunion(@ModelAttribute Reunion reunion,HttpSession session,RedirectAttributes redirect){
        if(getUser(session)==null) return "redirect:/login";
        try { reunionRepository.save(reunion); redirect.addFlashAttribute("success", reunion.getIdReunion()==null?"Reunión agregada correctamente.":"Reunión actualizada correctamente."); }
        catch(Exception ex){ ex.printStackTrace(); redirect.addFlashAttribute("error",ex.getMessage()); }
        return "redirect:/reuniones";
    }

    @PostMapping("/reuniones/eliminar/{id}")
    public String eliminarReunion(@PathVariable Long id,HttpSession session,RedirectAttributes redirect){
        if(getUser(session)==null) return "redirect:/login";
        try { reunionRepository.deleteById(id); redirect.addFlashAttribute("success","Reunión eliminada correctamente."); }
        catch(Exception ex){ ex.printStackTrace(); redirect.addFlashAttribute("error",ex.getMessage()); }
        return "redirect:/reuniones";
    }

    // ==================== ASISTENCIAS ====================
    @GetMapping("/asistencia")
    public String asistencia(Model model,HttpSession session){
        User user=getUser(session); if(user==null) return "redirect:/login";
        model.addAttribute("username",user.getUsername());
        try { model.addAttribute("asistencias",asistenciaRepository.findAll()); }
        catch(Exception ex){ ex.printStackTrace(); model.addAttribute("asistencias",List.of()); model.addAttribute("error","No se pudieron cargar las asistencias."); }
        return "asistencia";
    }
    
    
    // ==================== REPORTES ====================
    @GetMapping("/reporte")
    public String reporte(Model model, HttpSession session) {

        User user = getUser(session);

        // Verificar que el usuario haya iniciado sesión.
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("username", user.getUsername());

        try {
            // Cargar los registros reales desde PostgreSQL.
            model.addAttribute(
                    "asistencias",
                    asistenciaRepository.findAll()
            );

        } catch (Exception ex) {
            ex.printStackTrace();

            model.addAttribute("asistencias", List.of());

            model.addAttribute(
                    "error",
                    "No se pudieron cargar los registros del reporte."
            );
        }

        return "reporte";
    }
    

    @GetMapping("/asistencia/agregar")
    public String mostrarFormularioAsistencia(Model model,HttpSession session){
        User user=getUser(session); if(user==null) return "redirect:/login";
        cargarCatalogos(model,user.getUsername()); model.addAttribute("asistencia",new Asistencia());
        return "agregar-asistencia";
    }

    @GetMapping("/asistencia/editar/{id}")
public String editarAsistenciaForm(
        @PathVariable Long id,
        Model model,
        HttpSession session,
        RedirectAttributes redirect) {

    User user = getUser(session);

    if (user == null) {
        return "redirect:/login";
    }

    try {

        Optional<Asistencia> resultado =
                asistenciaRepository.findById(id);

        if (resultado.isEmpty()) {

            redirect.addFlashAttribute(
                    "error",
                    "Asistencia no encontrada."
            );

            return "redirect:/asistencia";
        }

        cargarCatalogos(
                model,
                user.getUsername()
        );

        model.addAttribute(
                "username",
                user.getUsername()
        );

        model.addAttribute(
                "asistencia",
                resultado.get()
        );

        return "agregar-asistencia";

    } catch (Exception ex) {

        ex.printStackTrace();

        redirect.addFlashAttribute(
                "error",
                "No se pudo cargar la asistencia."
        );

        return "redirect:/asistencia";
    }
}

    @PostMapping("/asistencia/guardar")
    public String guardarAsistencia(@ModelAttribute Asistencia asistencia,HttpSession session,RedirectAttributes redirect){
        if(getUser(session)==null) return "redirect:/login";
        try { asistenciaRepository.save(asistencia); redirect.addFlashAttribute("success", asistencia.getIdAsistencia()==null?"Asistencia registrada correctamente.":"Asistencia actualizada correctamente."); }
        catch(Exception ex){ ex.printStackTrace(); redirect.addFlashAttribute("error",ex.getMessage()); }
        return "redirect:/asistencia";
    }

    @PostMapping("/asistencia/eliminar/{id}")
public String eliminarAsistencia(
        @PathVariable Long id,
        HttpSession session,
        RedirectAttributes redirect) {

    User user = getUser(session);

    if (user == null) {
        return "redirect:/login";
    }

    try {

        boolean eliminado =
                asistenciaRepository.deleteById(id);

        if (eliminado) {

            redirect.addFlashAttribute(
                    "success",
                    "Asistencia eliminada correctamente."
            );

        } else {

            redirect.addFlashAttribute(
                    "error",
                    "No se encontró la asistencia."
            );
        }

    } catch (Exception ex) {

        ex.printStackTrace();

        redirect.addFlashAttribute(
                "error",
                "No se pudo eliminar la asistencia: "
                + ex.getMessage()
        );
    }

    return "redirect:/asistencia";
}

    // Compatibilidad con formulario anterior
    @PostMapping("/asistencia/agregar")
    public String agregarAsistenciaLegacy(@RequestParam Long idPadre,@RequestParam Long idReunion,@RequestParam String estado,
                                          @RequestParam(required=false) String horaLlegada,@RequestParam(required=false) String firmaDigital,
                                          HttpSession session,RedirectAttributes redirect){
        if(getUser(session)==null) return "redirect:/login";
        try { asistenciaRepository.guardarAsistencia(idPadre,idReunion,estado,horaLlegada,firmaDigital); redirect.addFlashAttribute("success","Asistencia registrada correctamente."); }
        catch(Exception ex){ ex.printStackTrace(); redirect.addFlashAttribute("error",ex.getMessage()); }
        return "redirect:/asistencia";
    }

    private void cargarCatalogos(Model model,String username){
        model.addAttribute("username",username);
        try { model.addAttribute("padres",padreRepository.findAll()); model.addAttribute("reuniones",reunionRepository.findAll()); }
        catch(Exception ex){ ex.printStackTrace(); model.addAttribute("padres",List.of()); model.addAttribute("reuniones",List.of()); model.addAttribute("error","No se pudieron cargar padres o reuniones."); }
    }

    // ==================== USUARIOS ====================
    @GetMapping("/usuarios")
    public String usuarios(Model model,HttpSession session){
        User user=getUser(session); if(user==null) return "redirect:/login";
        model.addAttribute("username",user.getUsername());
        try { model.addAttribute("usuarios",userRepository.findAll()); }
        catch(Exception ex){ ex.printStackTrace(); model.addAttribute("usuarios",List.of()); model.addAttribute("error","No se pudieron cargar los usuarios."); }
        return "usuarios";
    }

    @PostMapping("/agregar") public String agregar(@ModelAttribute User user,HttpSession session,RedirectAttributes redirect){
        if(getUser(session)==null)return "redirect:/login"; try{userRepository.save(user);redirect.addFlashAttribute("success","Usuario agregado correctamente.");}catch(Exception ex){ex.printStackTrace();redirect.addFlashAttribute("error",ex.getMessage());}return "redirect:/usuarios";
    }
    @PostMapping("/modificar") public String modificar(@ModelAttribute User user,HttpSession session,RedirectAttributes redirect){
        if(getUser(session)==null)return "redirect:/login"; try{userRepository.save(user);redirect.addFlashAttribute("success","Usuario actualizado correctamente.");}catch(Exception ex){ex.printStackTrace();redirect.addFlashAttribute("error",ex.getMessage());}return "redirect:/usuarios";
    }
    @PostMapping("/eliminar/{id}") public String eliminar(@PathVariable Long id,HttpSession session,RedirectAttributes redirect){
        User activo=getUser(session); if(activo==null)return "redirect:/login"; try{if(!id.equals(activo.getId())){userRepository.deleteById(id);redirect.addFlashAttribute("success","Usuario eliminado correctamente.");}else redirect.addFlashAttribute("error","No puedes eliminar el usuario con el que estás conectado.");}catch(Exception ex){ex.printStackTrace();redirect.addFlashAttribute("error",ex.getMessage());}return "redirect:/usuarios";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session){
        User user=getUser(session); if(user!=null) try{userRepository.registrarSalida(user);}catch(Exception ex){ex.printStackTrace();}
        session.invalidate(); return "redirect:/login";
    }

    private User getUser(HttpSession session){ return (User)session.getAttribute("user"); }
    
    
    @GetMapping("/configuracion")
public String configuracion(
        Model model,
        HttpSession session) {

    User user = (User) session.getAttribute("user");

    if (user == null) {
        return "redirect:/login";
    }

    model.addAttribute(
            "username",
            user.getUsername()
    );

    return "configuracion";
}
}
