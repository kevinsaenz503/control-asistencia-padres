// =====================================================
// RELOJ
// =====================================================

function actualizarReloj() {

    const ahora = new Date();

    let horas = String(
        ahora.getHours()
    ).padStart(2, "0");

    let minutos = String(
        ahora.getMinutes()
    ).padStart(2, "0");

    let segundos = String(
        ahora.getSeconds()
    ).padStart(2, "0");


    const reloj =
        document.getElementById("reloj");


    if (reloj) {

        reloj.textContent =
            `${horas}:${minutos}:${segundos}`;

    }

}


// =====================================================
// FECHA
// =====================================================

function actualizarFecha() {

    const ahora = new Date();

    const opciones = {

        weekday: "long",

        day: "numeric",

        month: "long",

        year: "numeric"

    };


    let fecha =
        ahora.toLocaleDateString(
            "es-SV",
            opciones
        );


    fecha =
        fecha.charAt(0).toUpperCase()
        + fecha.slice(1);


    const elemento =
        document.getElementById("fecha");


    if (elemento) {

        elemento.textContent = fecha;

    }

}


// =====================================================
// INICIAR
// =====================================================

actualizarReloj();

actualizarFecha();


setInterval(
    actualizarReloj,
    1000
);