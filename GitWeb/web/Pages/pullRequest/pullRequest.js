var refreshRate = 3000; //milli seconds
var PR_ACTIONS = "../../prActions";

// =================== Updating online users list ==========================
function getFiles() {
    $.ajax({
        url: PR_ACTIONS,
        data:
            {
                action: "getPRFiles"
            },

        success: function(fileObj) {
            if (fileObj.hasErrors) {
                alert(fileObj.errorMsg);
            } else {
                addFilesToPRPage(fileObj);

            }
        }
    });
}

function addFilesToPRPage(fileObj) {
    var list = $("#changedFiles");
    list.empty();

    if (fileObj.changedFiles.length === 0) {
        $('<li>' + "No Files were changed" + '</li>').appendTo(list);
    } else {

        $.each(fileObj.changedFiles || [], function (index, changedFile) {
            $('<li>' + changedFile + '</li>').appendTo(list);
        });
    }

    list = $("#addedFiles");
    list.empty();
    if (fileObj.newFiles.length === 0) {
        $('<li>' + "No Files were added" + '</li>').appendTo(list);
    } else {
        $.each(fileObj.newFiles || [], function (index, newFile) {
            $('<li>' + newFile + '</li>').appendTo(list);
        });
    }

    list = $("#deletedFiles");
    list.empty();
    if (fileObj.deletedFiles.length === 0) {
        $('<li>' + "No Files were deleted" + '</li>').appendTo(list);
    } else {
        $.each(fileObj.deletedFiles || [], function (index, deletedFile) {
            $('<li>' + deletedFile + '</li>').appendTo(list);
        });
    }
}

function approvePr() {
    $.ajax({
        url: PR_ACTIONS,
        data:
            {
                action: "approvePR"
            },

        success: function(approveObj) {
            if (approveObj.haveError) {
                alert(approveObj.errorMSG);
            } else {
                alert(approveObj.data);
            }
        }
    });
}

function rejectPR() {
    var msg=prompt("Please enter the reason for declining the Pull Request",
        "Your Pull Request sucks, I didn't like it so I rejected it.");

    $.ajax({
        url: PR_ACTIONS,
        data:
            {
                action: "declinePR",
                declineMsg: msg
            },

        success: function(declineObj) {
            if (declineObj.haveError) {
                alert(declineObj.errorMSG);
            } else {
                alert(declineObj.data);
            }
        }
    });
}

function getSrcBranch() {
    $.ajax({
        url: PR_ACTIONS,
        data:
            {
                action: "getSrc"
            },

        success: function(srcObj) {
            $("#branchSource").text(srcObj);
        }
    });
}
function getTargetBranch() {
    $.ajax({
        url: PR_ACTIONS,
        data:
            {
                action: "getTarget"
            },

        success: function(targetObj) {
            $("#branchTarget").text(targetObj);
        }
    });
}
function getMsg() {
    $.ajax({
        url: PR_ACTIONS,
        data:
            {
                action: "getMsg"
            },

        success: function(msgObj) {
            $("#prMessage").text(msgObj);
        }
    });
}

// activate the timer calls after the page is loaded
$(function() {

    getMsg();
    getTargetBranch();
    getSrcBranch();
    getFiles();

});


