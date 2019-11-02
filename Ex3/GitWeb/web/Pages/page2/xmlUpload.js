function LoadFile(event) {
    var file = event.target.files[0];
    var reader = new FileReader();

    reader.onload = function (){
        var content = reader.result;
        console.log(content);

        $.ajax({
            url: "../../LoadXml",
            data:
                {
                    file: content
                },
            type: 'POST',
            success: function(finalMsg) {
                updateRepoAfterLoadFile();
                alert(finalMsg);
            }
        });
    };

    reader.readAsText(file);
}

function updateRepoAfterLoadFile() {
    ajaxCurrentUserRepo();
}