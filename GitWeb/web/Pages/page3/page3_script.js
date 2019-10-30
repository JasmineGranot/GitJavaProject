var refreshRate = 3000; //milli seconds
var REFRESH_DATA = "../../refreshData";
var REPO_ACTIONS = "../../actionOnRepo";
var DELIMETER = "; ";

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
    $("#notifications").empty();

    $.each(notifications || [], function (index, msg) {
        var notificationSection = $(document.getElementById("notifications"));
        var label = $(document.createElement('li'));
        var down = $(document.createElement('br'));

        label.text(msg.msg);
        label.attr('id', msg.msg);
        label.appendTo($(notificationSection));
        down.appendTo($(notificationSection));

        $(label).click(function () {
            alert(msg.msg);
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
            var listArea = $('#commitSection');
            listArea.empty();
        }
    });
}

function checkoutBranch(){
    var branchName = $("#comboBranches option:selected").text();

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
                updateHeadBranchName();

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
    var branchName = $("#comboBranches option:selected").text();
    $.ajax({
        url: REPO_ACTIONS,
        data:
            {
                action: "deleteBranch",
                branchName: branchName
            },
        type: 'POST',
        success: function() {
            refreshBranches();
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

                listArea.append("<tr><th>Commit Sha1</th>" +
                    "<th>Commit Message</th>" +
                    "<th>Commit Date</th>" +
                    "<th>Commit Writer</th>" +
                    "<th>Branches Who Points To Commit</th></tr>");

                $.each(commits.res || [], function (index, commitData) {
                    var list = "";
                    $.each(commitData.branches || [], function (index, branch) {
                        list +=  branch + '<br>';
                    });

                    listArea.append("<tr><td>" + commitData.commitSha1 + "</td>" +
                        "<td>" + commitData.commitMsg + "</td>" +
                        "<td>" + commitData.commitDate + "</td>" +
                        "<td>" + commitData.commitWriter + "</td>" +
                        "<td>" + list +"</td></tr>");
                });
            }
        }
    });

}

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

function showWCStatus() {

    $("#showWCStatus").click(function () {
        window.open("../showWCStatus/ShowWCStatus.html", "Show Status");
    });
}

function createPullRequest(){
    var srcBranch=prompt("Please enter the source branch:");
    var targetBranch=prompt("Please enter the target branch:");
    var msg=prompt("Please enter a message for repository owner:",
        "I created a new pull request. Please check it out :)");

    if(srcBranch == null && targetBranch == null){
        alert("Please insert 2 branches!")
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

    var output = '';
    $.each(branches || [], function(index, branch) {
        output += '<option>' + branch + '</option>';
    });

    $('#comboBranches').append(output);
}

function addPRToSession(pr) {
    var prString = pr.owner + DELIMETER + pr.prMsg + DELIMETER + pr.status + DELIMETER + pr.baseToMergeInto +
        DELIMETER + pr.targetToMergeFrom + DELIMETER + pr.repoManagerMsg;
    $.ajax({
        url: REPO_ACTIONS,
        data:
            {
                action: "setPullRequestInSession",
                prObj: prString
            },
        type: 'POST'
    });

}

function ajaxUsersPullRequests() {
    $.ajax({
        url: REFRESH_DATA,
        data:
            {
                action: "getPullRequestsForUser"
            },
        success: function(pullRequests) {
            triggerGetPRs();
            updatePullRequestsForUser(pullRequests);
        }
    });
}

function updatePullRequestsForUser(pullRequests) {
    $("#openPrs").empty();

    $.each(pullRequests || [], function (index, pr) {
        var prSection = $(document.getElementById("openPrs"));
        var row = $(document.createElement('li'));
        var down = $(document.createElement('br'));

        row.text(pr.prMsg);
        row.appendTo($(prSection));
        down.appendTo($(prSection));

        addPRToSession(pr);

        $(row).click(function () {
            window.open("../pullRequest/pullRequestPage.html", "Pull Request");
        });
    });
}




// ========================= On loading ================================
function triggerGetMsg(){
    setTimeout(ajaxUsersNotificationsList, refreshRate);
}
function triggerGetPRs(){
    setTimeout(ajaxUsersPullRequests, refreshRate);
}

// activate the timer calls after the page is loaded
$(function() {
    setInterval(ajaxUsersList, refreshRate);
    ajaxCurrentUser();
    updateHeadBranchName();
    updateRepoName();
    ajaxUsersNotificationsList();
    triggerGetMsg();
    refreshBranches();
    triggerGetPRs();
    ajaxUsersPullRequests();
});


