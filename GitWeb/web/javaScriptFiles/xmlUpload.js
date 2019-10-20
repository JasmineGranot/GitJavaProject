function LoadGameClicked(event) {
    console.log("found this point");
    LoadGame(event);
}

function LoadGame(event) {
    var file = event.target.files[0];
    var reader = new FileReader();

    reader.onload = function (blob){
        var content = reader.readAsArrayBuffer(blob);
        $.ajax({
            url: "../loadXml",
            data:
                {file: content}
        });
    };

    reader.readAsText(file);
}
// function loadFile(json) {
//
// }