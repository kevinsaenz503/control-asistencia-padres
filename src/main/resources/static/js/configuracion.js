/* =========================================================
   CONFIGURACIÓN DEL SISTEMA
   ========================================================= */

document.addEventListener("DOMContentLoaded", function () {

    console.log("Configuración cargada correctamente");


    /* =====================================================
       ELEMENTOS
       ===================================================== */

    const formConfiguracion =
        document.getElementById("formConfiguracion");

    const btnGuardar =
        document.getElementById("btnGuardar");

    const btnRestaurar =
        document.getElementById("btnRestaurar");

    const mensaje =
        document.getElementById("mensajeConfiguracion");


    /* =====================================================
       CARGAR CONFIGURACIÓN GUARDADA
       ===================================================== */

    cargarConfiguracion();


    /* =====================================================
       GUARDAR CONFIGURACIÓN
       ===================================================== */

    if (formConfiguracion) {

        formConfiguracion.addEventListener("submit", function (event) {

            event.preventDefault();

            guardarConfiguracion();

        });

    }


    /* =====================================================
       BOTÓN GUARDAR
       ===================================================== */

    if (btnGuardar) {

        btnGuardar.addEventListener("click", function () {

            if (formConfiguracion) {
                guardarConfiguracion();
            }

        });

    }


    /* =====================================================
       RESTAURAR CONFIGURACIÓN
       ===================================================== */

    if (btnRestaurar) {

        btnRestaurar.addEventListener("click", function () {

            const confirmar = confirm(
                "¿Deseas restaurar la configuración predeterminada?"
            );

            if (!confirmar) {
                return;
            }

            restaurarConfiguracion();

        });

    }


    /* =====================================================
       FUNCIONES
       ===================================================== */


    function obtenerConfiguracion() {

        const configuracion = {

            nombreSistema:
                obtenerValor("nombreSistema", "Asistencia"),

            nombreInstitucion:
                obtenerValor("nombreInstitucion", ""),

            colorPrincipal:
                obtenerValor("colorPrincipal", "#1769c2"),

            mostrarNotificaciones:
                obtenerCheckbox("mostrarNotificaciones", true),

            confirmarEliminacion:
                obtenerCheckbox("confirmarEliminacion", true),

            modoCompacto:
                obtenerCheckbox("modoCompacto", false)

        };

        return configuracion;
    }


    /* =====================================================
       GUARDAR
       ===================================================== */

    function guardarConfiguracion() {

        const configuracion =
            obtenerConfiguracion();

        localStorage.setItem(
            "configuracionSistema",
            JSON.stringify(configuracion)
        );

        mostrarMensaje(
            "Configuración guardada correctamente.",
            "success"
        );

        aplicarConfiguracion(
            configuracion
        );

    }


    /* =====================================================
       CARGAR
       ===================================================== */

    function cargarConfiguracion() {

        const datos =
            localStorage.getItem(
                "configuracionSistema"
            );

        if (!datos) {

            aplicarConfiguracion(
                obtenerConfiguracion()
            );

            return;
        }


        try {

            const configuracion =
                JSON.parse(datos);


            /* Nombre del sistema */

            establecerValor(
                "nombreSistema",
                configuracion.nombreSistema
            );


            /* Institución */

            establecerValor(
                "nombreInstitucion",
                configuracion.nombreInstitucion
            );


            /* Color */

            establecerValor(
                "colorPrincipal",
                configuracion.colorPrincipal
            );


            /* Notificaciones */

            establecerCheckbox(
                "mostrarNotificaciones",
                configuracion.mostrarNotificaciones
            );


            /* Confirmación */

            establecerCheckbox(
                "confirmarEliminacion",
                configuracion.confirmarEliminacion
            );


            /* Modo compacto */

            establecerCheckbox(
                "modoCompacto",
                configuracion.modoCompacto
            );


            aplicarConfiguracion(
                configuracion
            );

        } catch (error) {

            console.error(
                "Error al cargar la configuración:",
                error
            );

        }

    }


    /* =====================================================
       RESTAURAR
       ===================================================== */

    function restaurarConfiguracion() {

        const configuracion = {

            nombreSistema:
                "Asistencia",

            nombreInstitucion:
                "",

            colorPrincipal:
                "#1769c2",

            mostrarNotificaciones:
                true,

            confirmarEliminacion:
                true,

            modoCompacto:
                false

        };


        localStorage.setItem(
            "configuracionSistema",
            JSON.stringify(configuracion)
        );


        establecerValor(
            "nombreSistema",
            configuracion.nombreSistema
        );

        establecerValor(
            "nombreInstitucion",
            configuracion.nombreInstitucion
        );

        establecerValor(
            "colorPrincipal",
            configuracion.colorPrincipal
        );

        establecerCheckbox(
            "mostrarNotificaciones",
            configuracion.mostrarNotificaciones
        );

        establecerCheckbox(
            "confirmarEliminacion",
            configuracion.confirmarEliminacion
        );

        establecerCheckbox(
            "modoCompacto",
            configuracion.modoCompacto
        );


        aplicarConfiguracion(
            configuracion
        );


        mostrarMensaje(
            "Configuración restaurada.",
            "success"
        );

    }


    /* =====================================================
       APLICAR CONFIGURACIÓN
       ===================================================== */

    function aplicarConfiguracion(configuracion) {

        /* ================================================
           COLOR PRINCIPAL
           ================================================ */

        if (configuracion.colorPrincipal) {

            document.documentElement.style.setProperty(
                "--color-principal",
                configuracion.colorPrincipal
            );

        }


        /* ================================================
           MODO COMPACTO
           ================================================ */

        if (configuracion.modoCompacto) {

            document.body.classList.add(
                "modo-compacto"
            );

        } else {

            document.body.classList.remove(
                "modo-compacto"
            );

        }


        /* ================================================
           TÍTULO DEL DOCUMENTO
           ================================================ */

        if (configuracion.nombreSistema) {

            document.title =
                configuracion.nombreSistema +
                " | Configuración";

        }

    }


    /* =====================================================
       OBTENER VALOR
       ===================================================== */

    function obtenerValor(id, valorPredeterminado) {

        const elemento =
            document.getElementById(id);

        if (!elemento) {
            return valorPredeterminado;
        }

        return elemento.value;

    }


    /* =====================================================
       ESTABLECER VALOR
       ===================================================== */

    function establecerValor(id, valor) {

        const elemento =
            document.getElementById(id);

        if (!elemento) {
            return;
        }

        elemento.value =
            valor !== undefined && valor !== null
                ? valor
                : "";

    }


    /* =====================================================
       OBTENER CHECKBOX
       ===================================================== */

    function obtenerCheckbox(
        id,
        valorPredeterminado
    ) {

        const elemento =
            document.getElementById(id);

        if (!elemento) {
            return valorPredeterminado;
        }

        return elemento.checked;

    }


    /* =====================================================
       ESTABLECER CHECKBOX
       ===================================================== */

    function establecerCheckbox(id, valor) {

        const elemento =
            document.getElementById(id);

        if (!elemento) {
            return;
        }

        elemento.checked =
            Boolean(valor);

    }


    /* =====================================================
       MENSAJES
       ===================================================== */

    function mostrarMensaje(
        texto,
        tipo
    ) {

        if (!mensaje) {

            alert(texto);

            return;

        }


        mensaje.textContent =
            texto;

        mensaje.className =
            "mensaje-configuracion " +
            tipo;

        mensaje.style.display =
            "block";


        setTimeout(function () {

            mensaje.style.display =
                "none";

        }, 3500);

    }


});