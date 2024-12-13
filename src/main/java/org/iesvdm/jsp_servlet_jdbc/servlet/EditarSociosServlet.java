package org.iesvdm.jsp_servlet_jdbc.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.iesvdm.jsp_servlet_jdbc.dao.SocioDAO;
import org.iesvdm.jsp_servlet_jdbc.dao.SocioDAOImpl;
import org.iesvdm.jsp_servlet_jdbc.model.Socio;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "EditarSociosServlet", value = "/EditarSociosServlet")
public class EditarSociosServlet extends HttpServlet {
    private SocioDAO socioDAO = new SocioDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String rawId = request.getParameter("codigo");

        if (rawId != null) {
            int id = Integer.parseInt(rawId);
            Optional<Socio> optionalSocio = this.socioDAO.find(id);

            if (optionalSocio.isPresent()) {
                request.setAttribute("socio", optionalSocio.get());
            }
        } else {
            request.setAttribute("error", "No has introducido ningun id");
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/formularioEditarSocioB.jsp");;
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = null;
        int id = Integer.parseInt(request.getParameter("codigo"));

        Optional<Socio> findedSocio = this.socioDAO.find(id);
        if (!findedSocio.isPresent()) {
            request.setAttribute("error", "Error en la validación");
            dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/formularioEditarSocioB.jsp");

            return;
        }

        Optional<Socio> optionalSocio = UtilServlet.validaGrabar(request);

        if (optionalSocio.isPresent()) {
            Socio socio = optionalSocio.get();
            socio.setSocioId(id);

            socioDAO.update(socio);

            List<Socio> listado = this.socioDAO.getAll();
            request.setAttribute("listado", listado);

            dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/pideNumeroSocio.jsp");
        } else {
            request.setAttribute("error", "Error en la validación");
            request.setAttribute("socio", findedSocio.get());

            dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/formularioEditarSocioB.jsp");
        }

        dispatcher.forward(request, response);
    }
}
