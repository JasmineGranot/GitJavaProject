var refreshRate = 3000; //milli seconds
var REFRESH_DATA = "../../refreshData";
var REPO_ACTIONS = "../../actionOnRepo";

// =================== Updating online users list ==========================
function ajaxCurrentUser() {
    $.ajax({
        url: REFRESH_DATA,
        data:
            {
                action: "getCurrentUser"
            },

        success: function(username) {
            var curUser =  $("#userName");
            $(curUser).text(username);

        }
    });
}

function ajaxUsersList() {
    $.ajax({
        url: REFRESH_DATA,
        data:
            {
                action: "getUsersList"
            },

        success: function(users) {
            refreshUsersList(users);
        }
    });
}

function refreshUsersList(users) {
    //clear all current users
    $("#userList").empty();
    console.log("Adding users ");

    // rebuild the list of users: scan all users and add them to the list of users
    $.each(users || [], function(index, username) {
        console.log("Adding user #" + index + ": " + username);
        //create a new <option> tag with a value in it and
        //appeand it to the #userslist (div with id=userslist) element
        $('<li>' + username + '</li>').appendTo($("#userList"));
    });
}

// =================== Updating user's Notifications (assuming list) ====================

function ajaxUsersNotificationsList() {
    $.ajax({
        url: REFRESH_DATA,
        data:
            {
                action: "getNotificationForUser"
            },
        success: function(notifications) {
            refreshMessagesList(notifications);
            triggerGetMsg();
        }
    });
}

function refreshMessagesList(notifications) {
    //clear all current messages
    $("#notifications").empty();

    // rebuild the list of users: scan all users and add them to the list of users
    $.each(notifications || [], function (index, msg) {
        var notificationSection = $(document.getElementById("notifications"));
        var label = $(document.createElement('li'));

        label.text(msg.msg);
        label.attr('id', msg.msg);
        label.appendTo($(notificationSection));

        $(label).click(function () {
            ajaxDeleteNotification(msg, label);
        });
    });
}

function ajaxDeleteNotification(msg, label) {
    $.ajax({
        url: REPO_ACTIONS,
        data:
            {
                action: "deleteNotification",
                message: msg.msg
            },
        type: 'POST',
        success: function (users) {
            $(label).remove();
        }

    });
}

// function ajaxUsersNotificationsList() {
//     $.ajax({
//         url: "..",
//         data:
//             {
//                 action: "getNotificationForUser"
//             },
//         success: function(notifications) {
//             refreshMessagesList(notifications);
//         }
//     });
// }
//
// function refreshMessagesList(notifications) {
//     //clear all current messages
//     $("#userNotifications").empty();
//
//     // rebuild the list of users: scan all users and add them to the list of users
//     $.each(notifications || [], function(index, msg) {
//         console.log("Adding msg #" + index + ": " + msg);
//         //create a new <option> tag with a value in it and
//         //appeand it to the #userslist (div with id=userslist) element
//         $('<li>' + msg + '</li>').appendTo($("#userNotifications"));
//     });
// }

// =================== Updating RepoData ====================
function updateRepoName(){
    $.ajax({
        url: REPO_ACTIONS,
        data:
            {
                action: "getRepo",
            },
        success: function (repoName) {
            $("#repoName").text(repoName);
        }
    });
}

function updateHeadBranchName(){
    $.ajax({
        url: REPO_ACTIONS,
        data:
            {
                action: "getHead",
            },
        success: function (headNameObj) {
            if (headNameObj.haveError) {
                alert(headNameObj.errorMSG);
            } else {
                $("#currentBranchName").text(headNameObj.data);
            }
        }
    });
}

function checkoutBranch(){
    var branchName = $("#comboBranches").val().text();

    $.ajax({
        url: REPO_ACTIONS,
        data:
            {
                action: "checkout",
                branchName: branchName
            },
        type: 'POST',
        success: function(checkoutResObj) {
            if (checkoutResObj.haveError){
                alert (checkoutResObj.errorMSG);
            }
            else{
                alert(checkoutResObj.data);
                updateHeadBranch();

            }
        }
    });
}

function pull(){
    $.ajax({
        url: REPO_ACTIONS,
        data:
            {
                action: "pull",
            },
        type: 'POST',
        success: function(pullObj) {
            if (pullObj.haveError) {
                alert(pullObj.errorMSG);
            } else {
                alert(pullObj.data);
            }
        }
    });
}

function push(){
    $.ajax({
        url: REPO_ACTIONS,
        data:
            {
                action: "push",
            },
        type: 'POST',
        success: function() {
        }
    });
}

function addBranch(){
    var name=prompt("Please enter new branch name","Aviad The King");
    var commit=prompt("Please enter commit sha1");

    if (name != null) {
        $.ajax({
            url: REPO_ACTIONS,
            data:
                {
                    action: "addBranch",
                    branchName: name,
                    commitSha1: commit
                },
            type: 'POST',
            success: function (branchObj) {

                if (branchObj.haveError) {
                    alert(branchObj.errorMSG);
                } else {
                    refreshBranches();
                    alert(branchObj.data);
                }
            }
        });
    }

}

function deleteBranch(){
    var branchName = $("#comboBranches").val().text();
    $.ajax({
        url: REPO_ACTIONS,
        data:
            {
                action: "deleteBranch",
                branchName: branchName
            },
        type: 'POST',
        success: function() {
            updateHeadBranch();
        }
    });
    refreshBranches()
}

function commit(){
    var msg=prompt("Please enter a massage for the new commit:");

    $.ajax({
        url: REPO_ACTIONS,
        data:
            {
                action: "getCommits",
                commitMsg: msg,
            },
        type: 'POST',
        success: function(newCommitResult) {
            if (newCommitResult.haveError) {
                alert(newCommitResult.errorMSG);
            } else {
                alert(newCommitResult.data);
            }
        }
    });
}

function showCommits(){
    $.ajax({
        url: REPO_ACTIONS,
        data:
            {
                action: "getCommits",
            },
        success: function(commits) {
            if(commits.hasError){
                alert(commits.errorMsg);
            }
            else {
                var listArea = $('#commitSection');
                listArea.empty();

                listArea.append("<tr><th>Root Sha1</th>" +
                    "<th>Last Commit</th>" +
                    "<th>Commit Message</th>" +
                    "<th>Commit Date</th>" +
                    "<th>Commit Writer</th></tr>");

                $.each(commits.res || [], function (index, commitData) {
                    listArea.append("<tr><td>" + commitData.commitSha1 + "</td>" +
                        "<td>" + commitData.commitsLastCommit + "</td>" +
                        "<td>" + commitData.commitMsg + "</td>" +
                        "<td>" + commitData.commitDate + "</td>" +
                        "<td>" + commitData.commitWriter + "</td></tr>");

                    // console.log("Adding commits");
                    // var element = $(document.createElement('li'));
                    // element.text(commitString);
                    // element.appendTo(listArea);
                    // element.text('\n');
                    // element.appendTo(listArea);
                });
            }
        }
    });

}

function showWC(){}

function createPullRequest(){
    var srcBranch=prompt("Please enter the source branch:");
    var targetBranch=prompt("Please enter the target branch:");
    var msg=prompt("Please enter a message for repository owner:",
        "I created a new pull request. Please check it out :)");

    if(srcBranch == null && targetBranch == null){
        alert("Cannot take 2 branches!")
    }
    else {
        $.ajax({
            url: REPO_ACTIONS,
            data:
                {
                    action: "createPullRequest",
                    branchName: srcBranch,
                    branchTarget: targetBranch,
                    prMsg: msg
                },
            type: 'POST',
            success: function (prObj) {
                if (prObj.haveError) {
                    alert(prObj.errorMSG);
                } else {
                    alert(prObj.data);
                }
            }
        });
    }
}

function refreshBranches(){
    $.ajax({
        url: REPO_ACTIONS,
        data:
            {
                action: "getBranches",
            },

        success: function(branches) {
            addBranchesToList(branches);
        }
    });
}

function addBranchesToList(branches) {
    $("#comboBranches").empty();
    console.log("Adding branches ");

    // rebuild the list of users: scan all users and add them to the list of users
    $.each(branches || [], function(index, branch) {
        console.log("Adding branch #" + index + ": " + branch.name);
        //create a new <option> tag with a value in it and
        //appeand it to the #userslist (div with id=userslist) element
        $('#comboBranches').append( new Option(branch.name) )
    });
}



// ========================= On loading ================================
function triggerGetMsg(){
    setTimeout(ajaxUsersNotificationsList, refreshRate);
}
// function triggerGetRepos(){
//     setTimeout(ajaxCurrentUserRepo, refreshRate);
// }

// activate the timer calls after the page is loaded
$(function() {

    //These lists is refreshed automatically every second
    // setTimeout(ajaxCurrentUserRepo, refreshRate);
    setInterval(ajaxUsersList, refreshRate);
    ajaxCurrentUser();
    updateHeadBranchName();
    updateRepoName();
    ajaxUsersNotificationsList();
    triggerGetMsg();

    // triggerGetRepos();
    // setInterval(ajaxUsersNotificationsList, refreshRate);


    // //The chat content is refreshed only once (using a timeout) but
    // //on each call it triggers another execution of itself later (1 second later)
    // triggerAjaxChatContent();
});


