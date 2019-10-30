// =================== Updating Files in System ====================
var FILE_CHANGING;

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
                $('#fileData').empty();

            }
            else {
                showWCStatus(res.res);
            }
        }
    });
}

function showWCStatus(files) {
    var filesArea = $('#workingCopy');

    filesArea.empty();

    $.each(files || [], function (index, file) {
        var newFile = $(document.createElement('ul', { is : 'expanding-list' }));


        newFile.text(file);
        newFile.appendTo(filesArea);

        $(newFile).click(function () {
            var text = $('#fileData');
            text.attr("path", file);
            updateTextOfFile(file);

        });
    });
}

function updateTextOfFile(filePath){
    let textToEnter = getTextFromFile(filePath);
    let textField = $('#fileData');
    textField.empty();
    textField.append(textToEnter);
}

function getTextFromFile(filePath){
    $.ajax({
        url: FILE_CHANGING,
        data:
            {
                action: "getFileContent",
                filePath: filePath
            },
        success: function (res) {
            if(res.harError) {
                alert(res.errorMsg);
            }
            else {
                return (res.res);
            }
        }
    });
}

function addNewFile(){
    var filePath = prompt("Please enter the new path (relative to your repository)",
        "new file.txt");
    $.ajax({
        url: FILE_CHANGING,
        data:
            {
                action: "addNewFile",
                filePath: filePath
            },
        success: function (res) {
            if(res.harError) {
                alert(res.errorMsg);
            }
            else {
                showWC();
            }
        }
    });
}

function addNewFolder(){
    var filePath = prompt("Please enter the new folder path (relative to your repository)",
        "folder 1");
    $.ajax({
        url: FILE_CHANGING,
        data:
            {
                action: "addNewFolder",
                filePath: filePath
            },
        success: function (res) {
            if(res.harError) {
                alert(res.errorMsg);
            }
            else {
                alert(res.data);
            }
        }
    });
}

function saveFile(){
    let textField = $('#fileData');
    let text = textField.text;
    let file = textField.getAttribute("path");
    $.ajax({
        url: FILE_CHANGING,
        data:
            {
                action: "saveChangesInFile",
                filePath: file,
                data: text
            },
        success: function (res) {
            if(res.harError) {
                alert(res.errorMsg);
            }
            else {
                alert (res.data);
            }
        }
    });
}

function deleteFile(){
    let textField = $('#fileData');
    let file = textField.getAttribute("path");
    $.ajax({
        url: FILE_CHANGING,
        data:
            {
                action: "deleteFile",
                filePath: file,
            },
        success: function (res) {
            if(res.harError) {
                alert(res.errorMsg);
            }
            else {
                alert(res.data);
                textField.empty();
                textField.attr('path', null);
            }
        }
    });

}


$(function() {
    showWC();
});
