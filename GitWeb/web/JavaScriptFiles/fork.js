function forkRepository(event) {
    var forkRepo = document.getElementById("forkButton");
    $.ajax ({
        url:"../fork"
    });
}

