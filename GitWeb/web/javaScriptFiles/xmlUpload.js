function loadRepositoryFromXml() {
    var c = document.getElementById("loadRepo");
    $.ajax({
        url: "../loadXml",
        data:
            {action: "loadXML"}
    });
    success: loadFile();
}
function LoadGameClicked(event) {
    var file = event.target.files[0];
    var reader = new FileReader();

    reader.onload = function (){
        var content = reader.result;
        loadRepositoryFromXml();
    };

    reader.readAsText(file);
}
// function loadFile(json) {
//
// }