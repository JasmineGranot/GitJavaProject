// var REFRESH_DATA = buildUrlWithContextPath("refreshData");
var refreshRate = 2000; //milli seconds

// =================== Updating online users list ==========================
function ajaxUsersList() {
    $.ajax({
        url: "..",
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

// =================== Updating current user's repo list ======================

function ajaxCurrentUserRepo() {
    console.log("got here");

    $.ajax({
        url: "../refreshData",
        data:
            {
                action: "getUserRepoList"
            },

        success: function(repos) {
            refreshUserReposList(repos);
        }
    });
}

// function to update the repo list
function refreshUserReposList(repos) {
    //clear all current repos
    $("#UserRepoTable").clear();

    // rebuild the table of repos: scan all users and add them to the list of users
    $.each(repos || [], function(index, repo) {
        console.log("Adding repo #" + index + ": " + repo.name);
        //appeand it to the #UserRepoTable
        var line = $("#UserRepoTable").insertRow(0);
        var x = line.insertCell(0);
        x.innerHTML=repo.name;
        x = line.insertCell(1);
        x.innerHTML=repo.activeBranch;
        x = line.insertCell(2);
        x.innerHTML=repo.numOfBranches;
        x = line.insertCell(3);
        x.innerHTML=repo.lastCommitDate;
    });
}

// ========================= On loading ================================


// activate the timer calls after the page is loaded
$(function() {

    //These lists is refreshed automatically every second
    setInterval(ajaxCurrentUserRepo, refreshRate);
    // setInterval(ajaxUsersList, refreshRate);
    // setInterval(ajaxUsersNotificationsList, refreshRate);


    // //The chat content is refreshed only once (using a timeout) but
    // //on each call it triggers another execution of itself later (1 second later)
    // triggerAjaxChatContent();
});

