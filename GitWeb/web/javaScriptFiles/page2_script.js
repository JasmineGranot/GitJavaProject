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
    var tableBody;
    var htmlContent;
    $.each(repos || [], function(index, repo) {
        console.log("Adding repo #" + index + ": " + repo.name);
        tableBody = document.getElementById('userRepoTable').getElementsByTagName('tbody');
        //appeand it to the #UserRepoTable

        var tr = $(document.createElement('tr'));
        var tdRepoName = $(document.createElement('td')).text(repo.name);
        var tdHead = $(document.createElement('td')).text(repo.activeBranch);
        var tdBranchNum = $(document.createElement('td')).text(repo.numOfBranches);
        var tdLastCommitDate = $(document.createElement('td')).text(repo.lastCommitDate);
        var btn = $(document.createElement('button'));
        var tdFork = $(document.createElement('td'));
        btn.text("Fork");
        btn.appendTo(tdFork);
        tdRepoName.appendTo(tr);
        tdHead.appendTo(tr);
        tdBranchNum.appendTo(tr);
        tdLastCommitDate.appendTo(tr);
        tdFork.appendTo(tr);

        tr.appendTo(tableBody);


        console.log("finished repos");

        $(btn).click(function(){
            alert("hello");
        });
        console.log("try click");

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


