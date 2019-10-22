var REFRESH_DATA = buildUrlWithContextPath("refreshData");
var refreshRate = 5000; //milli seconds

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
    triggerGetRepos();
}

// function to update the repo list

function refreshUserReposList(repos) {
    //clear all current repos

    console.log("empting repos");
    $('#tableBody').empty();

    // rebuild the table of repos: scan all users and add them to the list of users
    console.log("adding repos");
    var newRow;
    var htmlContent;
    $.each(repos || [], function(index, repo) {
        console.log("Adding repo #" + index + ": " + repo.name);
        newRow = document.getElementById('userRepoTable').getElementsByTagName('tbody')[0].insertRow();
        htmlContent = "";
        //appeand it to the #UserRepoTable
        htmlContent += ("<td>" + repo.name + "</td>");
        htmlContent += ("<td>" + repo.activeBranch + "</td>");
        htmlContent += ("<td>" + repo.numOfBranches + "</td>");
        htmlContent += ("<td>" + repo.lastCommitDate + "</td>");
        // x.innerHTML=repo.name;
        // x = line.insertCell(1);
        // x.innerHTML=repo.activeBranch;
        // x = line.insertCell(2);
        // x.innerHTML=repo.numOfBranches;
        // x = line.insertCell(3);
        // x.innerHTML=repo.lastCommitDate;

        console.log("finished repos");

        newRow.innerHTML = htmlContent;
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

