
/* ============================================================
   CONFIGURACIÓN GLOBAL DEL SISTEMA
   ============================================================ */

const ASISTENCIA_CONFIG_KEY = "asistenciaConfiguracion";

const AsistenciaConfig = {

    valoresPredeterminados: {
        notificaciones: true,
        confirmarEliminaciones: true,
        animaciones: true,
        notificacionesAsistencia: true,
        tema: "claro",
        colorPrincipal: "#1769c2",
        modoCompacto: false,
        nombreSistema: "Asistencia",
        nombreInstitucion: ""
    },

    obtener: function () {
        try {
            const guardado = localStorage.getItem(ASISTENCIA_CONFIG_KEY);

            if (!guardado) {
                return { ...this.valoresPredeterminados };
            }

            return {
                ...this.valoresPredeterminados,
                ...JSON.parse(guardado)
            };

        } catch (error) {
            console.error("Error al obtener la configuración:", error);
            return { ...this.valoresPredeterminados };
        }
    },

    guardar: function (configuracion) {
        try {
            const nuevaConfiguracion = {
                ...this.valoresPredeterminados,
                ...configuracion
            };

            localStorage.setItem(
                ASISTENCIA_CONFIG_KEY,
                JSON.stringify(nuevaConfiguracion)
            );

            this.aplicar();

            return true;

        } catch (error) {
            console.error("Error al guardar la configuración:", error);
            return false;
        }
    },

    aplicarColor: function (configuracion) {
        const color = configuracion.colorPrincipal || "#1769c2";

        document.documentElement.style.setProperty(
            "--color-principal",
            color
        );

        document.documentElement.style.setProperty(
            "--color-principal-hover",
            color
        );
    },

    aplicarTema: function (configuracion) {
        document.body.classList.toggle(
            "tema-oscuro",
            configuracion.tema === "oscuro"
        );
    },

    aplicarModoCompacto: function (configuracion) {
        document.body.classList.toggle(
            "modo-compacto",
            configuracion.modoCompacto === true
        );
    },

    aplicarAnimaciones: function (configuracion) {
        document.body.classList.toggle(
            "sin-animaciones",
            configuracion.animaciones === false
        );
    },

    aplicarNombre: function (configuracion) {
        const nombre = configuracion.nombreSistema || "Asistencia";
        const institucion = configuracion.nombreInstitucion || "";

        document.querySelectorAll("[data-nombre-sistema]").forEach(
            elemento => {
                elemento.textContent = nombre;
            }
        );

        document.querySelectorAll("[data-nombre-institucion]").forEach(
            elemento => {
                elemento.textContent = institucion;
                elemento.style.display = institucion ? "" : "none";
            }
        );
    },

    aplicar: function () {
        const configuracion = this.obtener();

        this.aplicarColor(configuracion);
        this.aplicarTema(configuracion);
        this.aplicarModoCompacto(configuracion);
        this.aplicarAnimaciones(configuracion);
        this.aplicarNombre(configuracion);
    },

    notificar: function (mensaje, tipo = "success") {
        const configuracion = this.obtener();

        if (!configuracion.notificaciones) {
            return;
        }

        const anterior = document.getElementById(
            "asistencia-notificacion"
        );

        if (anterior) {
            anterior.remove();
        }

        const notificacion = document.createElement("div");
        notificacion.id = "asistencia-notificacion";
        notificacion.textContent = mensaje;

        const fondos = {
            success: "#167447",
            error: "#b42318",
            warning: "#b45309",
            info: "#1769c2"
        };

        Object.assign(notificacion.style, {
            position: "fixed",
            top: "25px",
            right: "25px",
            zIndex: "99999",
            background: fondos[tipo] || fondos.success,
            color: "#ffffff",
            padding: "14px 20px",
            borderRadius: "10px",
            fontFamily: "Arial, Helvetica, sans-serif",
            fontSize: "14px",
            fontWeight: "bold",
            boxShadow: "0 8px 25px rgba(0,0,0,.20)",
            maxWidth: "380px",
            opacity: "0",
            transform: "translateY(-10px)",
            transition: configuracion.animaciones
                ? "all .25s ease"
                : "none"
        });

        document.body.appendChild(notificacion);

        requestAnimationFrame(() => {
            notificacion.style.opacity = "1";
            notificacion.style.transform = "translateY(0)";
        });

        setTimeout(() => {
            notificacion.style.opacity = "0";
            notificacion.style.transform = "translateY(-10px)";

            setTimeout(() => notificacion.remove(), 300);
        }, 3500);
    },

    confirmarEliminacion: function (
        mensaje = "¿Está seguro de eliminar este registro?"
    ) {
        const configuracion = this.obtener();

        if (!configuracion.confirmarEliminaciones) {
            return true;
        }

        return window.confirm(mensaje);
    }
};


document.addEventListener("DOMContentLoaded", function () {
    const buscador = document.getElementById("buscarPadre");
    const tabla = document.getElementById("tablaPadres");
    const contador = document.getElementById("contadorPadres");
    const sinResultados = document.getElementById("sinResultados");

    if (!buscador || !tabla || !contador || !sinResultados) {
        return;
    }

    const filas = Array.from(
        tabla.querySelectorAll("tbody tr")
    ).filter(fila =>
        fila.hasAttribute("th:each") ||
        fila.querySelector("a.small-btn.edit")
    );

    function normalizar(texto) {
        return texto
            .toLocaleLowerCase("es")
            .normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "")
            .trim();
    }

    function buscarPadres() {
        const termino = normalizar(buscador.value);
        let visibles = 0;

        filas.forEach(fila => {
            const contenido = normalizar(fila.innerText);
            const coincide = contenido.includes(termino);

            fila.style.display = coincide ? "" : "none";

            if (coincide) {
                visibles++;
            }
        });

        sinResultados.style.display =
            visibles === 0 ? "table-row" : "none";

        contador.textContent =
            visibles + (visibles === 1
                ? " padre encontrado"
                : " padres encontrados");
    }

    buscador.addEventListener("input", buscarPadres);
    buscarPadres();
});




document.addEventListener("DOMContentLoaded", function () {
    const buscador = document.getElementById("buscarAsistencia");
    const tabla = document.querySelector(".table-wrap table");
    const contador = document.getElementById("contadorAsistencia");
    const sinResultados = document.getElementById("sinResultadosAsistencia");

    if (!buscador || !tabla || !contador || !sinResultados) {
        return;
    }

    const filas = Array.from(tabla.querySelectorAll("tbody tr"))
        .filter(fila => fila.hasAttribute("th:each") ||
                        fila.querySelector("a.small-btn.edit"));

    function normalizar(texto) {
        return texto
            .toLocaleLowerCase("es")
            .normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "")
            .trim();
    }

    function buscarAsistencias() {
        const termino = normalizar(buscador.value);
        let visibles = 0;

        filas.forEach(function (fila) {
            const contenido = normalizar(fila.innerText);
            const coincide = contenido.includes(termino);

            fila.style.display = coincide ? "" : "none";

            if (coincide) {
                visibles++;
            }
        });

        sinResultados.style.display =
            visibles === 0 ? "table-row" : "none";

        contador.textContent =
            visibles + (visibles === 1
                ? " registro encontrado"
                : " registros encontrados");
    }

    buscador.addEventListener("input", buscarAsistencias);
    buscarAsistencias();
});




document.addEventListener("DOMContentLoaded", function () {
    const buscador = document.getElementById("buscarReunion");
    const tabla = document.getElementById("tablaReuniones");
    const contador = document.getElementById("contadorReuniones");
    const sinResultados = document.getElementById("sinResultadosReuniones");

    if (!buscador || !tabla || !contador || !sinResultados) {
        return;
    }

    const filas = Array.from(tabla.querySelectorAll("tbody tr"))
        .filter(fila =>
            fila.hasAttribute("th:each") ||
            fila.querySelector("a.small-btn.edit")
        );

    function normalizar(texto) {
        return texto
            .toLocaleLowerCase("es")
            .normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "")
            .trim();
    }

    function buscarReuniones() {
        const termino = normalizar(buscador.value);
        let visibles = 0;

        filas.forEach(function (fila) {
            const contenido = normalizar(fila.innerText);
            const coincide = contenido.includes(termino);

            fila.style.display = coincide ? "" : "none";

            if (coincide) {
                visibles++;
            }
        });

        sinResultados.style.display =
            visibles === 0 ? "table-row" : "none";

        contador.textContent =
            visibles + (
                visibles === 1
                    ? " reunión encontrada"
                    : " reuniones encontradas"
            );
    }

    buscador.addEventListener("input", buscarReuniones);
    buscarReuniones();
});



/* Aplicar los ajustes al cargar la página */
document.addEventListener("DOMContentLoaded", function () {
    AsistenciaConfig.aplicar();
});