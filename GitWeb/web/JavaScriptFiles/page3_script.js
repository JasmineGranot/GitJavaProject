var REFRESH_DATA = "../refreshData";
var refreshRate = 3000; //milli seconds

// =================== Updating online users list ==========================
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
        url: "..",
        data:
            {
                action: "getNotificationForUser"
            },
        success: function(notifications) {
            refreshMessagesList(notifications);
        }
    });
}

function refreshMessagesList(notifications) {
    //clear all current messages
    $("#userNotifications").empty();

    // rebuild the list of users: scan all users and add them to the list of users
    $.each(notifications || [], function(index, msg) {
        console.log("Adding msg #" + index + ": " + msg);
        //create a new <option> tag with a value in it and
        //appeand it to the #userslist (div with id=userslist) element
        $('<li>' + msg + '</li>').appendTo($("#userNotifications"));
    });
}

// =================== Updating RepoData ====================
function updateHeadBranch(){
    $.ajax({
        url: REPO_ACTIONS,
        data:
            {
                action: "getHead",
            },

        success: function(headBranch) {
            updateHeadBranch();
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

        success: function() {
            updateHeadBranch();
        }
    });
    updateHeadBranch()
}
function pull(){
    $.ajax({
        url: REPO_ACTIONS,
        data:
            {
                action: "pull",
            },

        success: function() {
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

        success: function() {
        }
    });
}
function addBranch(){
    var name=prompt("Please enter new branch name","Aviad The King");
    if (name != null) {
        $.ajax({
            url: REPO_ACTIONS,
            data:
                {
                    action: "addBranch",
                    branchName: name
                },

            success: function () {
                refreshBranches()
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

        success: function() {
            updateHeadBranch();
        }
    });
    refreshBranches()
}
function showCommits(){}
function showWC(){}
function createPullRequest(){}
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

function triggerGetRepos(){
    setTimeout(ajaxCurrentUserRepo, refreshRate);
}

// activate the timer calls after the page is loaded
$(function() {

    //These lists is refreshed automatically every second
    // setTimeout(ajaxCurrentUserRepo, refreshRate);
    setInterval(ajaxUsersList, refreshRate);
    triggerGetRepos();
    // setInterval(ajaxUsersNotificationsList, refreshRate);


    // //The chat content is refreshed only once (using a timeout) but
    // //on each call it triggers another execution of itself later (1 second later)
    // triggerAjaxChatContent();
});


