var REPO_ACTIONS = "../../actionOnRepo";

// =================== Updating Files in System ====================
var FILE_CHANGING = "../../fileChange";

function showWC(){
    $.ajax({
        url: REPO_ACTIONS,
        data:
            {
                action: "showWC",
            },
        success: function (res) {
            if(res.haveError) {
                alert(res.errorMSG);
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
    var textField = $('#fileData');

    textField.val("");
    filesArea.empty();

    $.each(files || [], function (index, file) {
        var newFile = $(document.createElement('ul', { is : 'expanding-list' }));
        newFile.text(file);
        newFile.attr("selected", "false");
        newFile.css('font-weight', 'auto');
        newFile.css('font-color', 'white');
        newFile.appendTo(filesArea);

        $(newFile).click(function () {
            var text = $('#fileData');
            text.val("");
            text.attr("path", file);
            newFile.attr("selected", "true");
            newFile.css('font-weight', 'bold');
            newFile.css('color', 'red');
            getTextFromFile(file);

        });
    });

}

function updateTextOfFile(textToEnter){
    let textField = $('#fileData');
    var newFileButton = $('#newFile');
    var newFolderButton = $('#newFolder');
    var saveFileButton = $('#saveFile');
    var deleteFileButton = $('#deleteFile');

    textField.val(textToEnter);
    newFileButton.attr('disabled', false);
    newFolderButton.attr('disabled', false);
    saveFileButton.attr('disabled', false);
    deleteFileButton.attr('disabled', false);
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
            if(res.haveError) {
                alert(res.errorMsg);
            }
            else {
                updateTextOfFile(res.data);
            }
        }
    });
}

function addNewFile(){
    var fileName = prompt("Please enter the new file name",
        "new file.txt");
    let textField = $('#fileData');
    let filePath = textField.attr('path');
    $.ajax({
        url: FILE_CHANGING,
        data:
            {
                action: "addNewFile",
                filePath: filePath,
                fileName: fileName
            },
        success: function (res) {
            if(res.haveError) {
                alert(res.errorMSG);
            }
            else {
                alert(res.data);
                showWC();
            }
        }
    });
}

function addNewFolder(){
    var fileName = prompt("Please enter the new folder name");
    let textField = $('#fileData');
    let filePath = textField.attr("path");
    $.ajax({
        url: FILE_CHANGING,
        data:
            {
                action: "addNewFolder",
                filePath: filePath,
                fileName: fileName,
            },
        success: function (res) {
            if(res.haveError) {
                alert(res.errorMSG);
            }
            else {
                alert(res.data);
                showWC();
            }
        }
    });
}

function saveFile(){
    let textField = $('#fileData');
    let text = textField.val();
    let file = textField.attr("path");
    $.ajax({
        url: FILE_CHANGING,
        data:
            {
                action: "saveChangesInFile",
                filePath: file,
                data: text
            },
        success: function (res) {
            if(res.haveError) {
                alert(res.errorMSG);
            }
            else {
                alert (res.data);
                showWC();
            }
        }
    });
}

function deleteFile(){
    let textField = $('#fileData');
    let file = textField.attr("path");
    $.ajax({
        url: FILE_CHANGING,
        data:
            {
                action: "deleteFile",
                filePath: file,
            },
        success: function (res) {
            if(res.haveError) {
                alert(res.errorMSG);
            }
            else {
                alert(res.data);
                textField.attr('path', null);
                showWC();
            }
        }
    });

}


$(function() {
    showWC();
});