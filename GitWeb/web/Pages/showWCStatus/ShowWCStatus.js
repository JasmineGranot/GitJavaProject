var REPO_ACTIONS = "../../actionOnRepo";


function showWC(){
    $.ajax({
        url: REPO_ACTIONS,
        data:
            {
                action: "showWC",
            },
        success: function (res) {
            if(res.harError) {
                alert(res.errorMsg);
            }
            else {
                showWCStatus(res.res);
            }
        }
    });
}

function showWCStatus(files) {
    var filesArea = $("#workingCopy");

    filesArea.empty();

    $.each(files || [], function (index, file) {
        var newFile = $(document.createElement('ul', { is : 'expanding-list' }));


        newFile.text(file);
        newFile.appendTo(filesArea);

        $(newFile).click(function () {
            alert(file);
        });
    });
}

$(function() {
    showWC();
});