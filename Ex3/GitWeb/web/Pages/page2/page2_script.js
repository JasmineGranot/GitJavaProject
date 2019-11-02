var REFRESH_DATA = "../../refreshData";
var PAGE3 = "../../page3";
var REPO_ACTIONS = "../../actionOnRepo";

var refreshRate = 3000; //milli seconds

// ================== Fork action ==========================

function forkRepository(repo) {
    var forkRepo = repo.id;
    var forkOwner = repo.getAttribute("owner");
    console.log(forkOwner);
    $.ajax ({
        url:"../../fork",
        data:
            {
                repositoryName: forkRepo,
                otherUSer: forkOwner
            },
        success: function(finalMsg) {
            ajaxCurrentUserRepo();
            alert(finalMsg);
        }
    });
}

// =================== Updating current user ==========================

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
           $(curUser).click(function(){
               ajaxCurrentUserRepo();
           })


        }
    });
}

function logoutUser(){
    $.ajax({
        url: "../../logout",
    });
}
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

    // rebuild the list of users: scan all users and add them to the list of users
    $.each(users || [], function(index, username) {
        var userRow = $(document.createElement('li'));
        userRow.text(username);
        userRow.attr('id', username);
        $(userRow).click(function () {
            ajaxOtherUserRepo(this.id);
        });
        userRow.appendTo($("#userList"));
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

// =================== Updating user's repo list ======================
function ajaxOtherUserRepo(username) {
    var otherUserName = username;
    $.ajax({
        url: REFRESH_DATA,
        data:
            {
                action: "getOtherUserRepoList",
                otherUserName: otherUserName
            },

        success: function(repos) {
            refreshOtherUserReposList(repos);
        }
    });
}

function ajaxCurrentUserRepo() {
    console.log("got here");

    $.ajax({
        url: REFRESH_DATA,
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

        var btn = $(document.createElement('a'));
        var tdRepo = $(document.createElement('td'));
        btn.attr('id', repo.name);
        btn.attr('owner', repo.repoOwner);
        btn.text("Go To Repository");
        btn.attr('class', "button");
        btn.attr('href', "../page3/Page3.html");
        btn.attr('role', "button");

        btn.appendTo(tdRepo);

        tdRepoName.appendTo(tr);
        tdHead.appendTo(tr);
        tdBranchNum.appendTo(tr);
        tdLastCommitDate.appendTo(tr);
        tdRepo.appendTo(tr);

        tr.appendTo(tableBody);


        console.log("finished repos");

        $(btn).click(function(){

            updateRepo(repo.name);
            console.log("defined session's repo");
        });

    });
}

function refreshOtherUserReposList(repos) {
    //clear all current repos

    $('#tableBody').empty();

    // rebuild the table of repos: scan all users and add them to the list of users
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
        btn.attr('id', repo.name);
        btn.attr('owner', repo.repoOwner);
        btn.text("Fork");
        btn.attr('class', "button");

        btn.appendTo(tdFork);
        tdRepoName.appendTo(tr);
        tdHead.appendTo(tr);
        tdBranchNum.appendTo(tr);
        tdLastCommitDate.appendTo(tr);
        tdFork.appendTo(tr);

        tr.appendTo(tableBody);


        console.log("finished repos");

        $(btn).click(function(){
            forkRepository(this)
        });
        console.log("try click");

    });
}

function updateRepo(repoName) {
        $.ajax({
        url: PAGE3,
        data:
            {
                action: "setRepo",
                repoName: repoName
            },

    });
}


// ========================= On loading ================================

function triggerGetMsg(){
    setTimeout(ajaxUsersNotificationsList, refreshRate);
}

// activate the timer calls after the page is loaded
$(function() {
    ajaxCurrentUserRepo();
    ajaxCurrentUser();
    //These lists is refreshed automatically every second
    // setTimeout(ajaxCurrentUserRepo, refreshRate);
    setInterval(ajaxUsersList, refreshRate);
    triggerGetMsg();
    // setInterval(ajaxUsersNotificationsList, refreshRate);


    // //The chat content is refreshed only once (using a timeout) but
    // //on each call it triggers another execution of itself later (1 second later)
    // triggerAjaxChatContent();
});


