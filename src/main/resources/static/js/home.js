function abrirCrearUsuario(){

    document.getElementById("modalUsuario")
        .style.display = "flex";

    document.getElementById("tituloModal")
        .innerText = "Crear Usuario";

    document.getElementById("formUsuario")
        .action = "/agregar";

    limpiarFormulario();
}

function editarUsuario(button){

    document.getElementById("modalUsuario")
        .style.display = "flex";

    document.getElementById("tituloModal")
        .innerText = "Editar Usuario";

    document.getElementById("formUsuario")
        .action = "/modificar";

    let id =
        button.getAttribute("data-id");

    let username =
        button.getAttribute("data-username");

    document.getElementById("id")
        .value = id;

    document.getElementById("username")
        .value = username;

    document.getElementById("password")
        .value = "";

    document.getElementById("confirmPassword")
        .value = "";
}

function cerrarModal(){

    document.getElementById("modalUsuario")
        .style.display = "none";
}

function limpiarFormulario(){

    document.getElementById("id")
        .value = "";

    document.getElementById("username")
        .value = "";

    document.getElementById("password")
        .value = "";

    document.getElementById("confirmPassword")
        .value = "";
}

function validarPasswords(){

    let p1 =
        document.getElementById("password").value;

    let p2 =
        document.getElementById("confirmPassword").value;

    if(p1 !== p2){

        alert("Las contraseñas no coinciden");

        return false;
    }

    return true;
}

window.onclick = function(event){

    let modal =
        document.getElementById("modalUsuario");

    if(event.target === modal){

        cerrarModal();
    }
}