$(document).ready(function () {
    if (!sessionStorage.credentials) {
        console.log("Credentials found redirecting to login");
        window.location.href = "/ProjectV1/";
    }
    var userID = sessionStorage.userID;
    var basicCredentials = sessionStorage.credentials;
    console.log(sessionStorage.credentials);
    console.log("USER ID: " + userID);
    console.log("Session storage: " + sessionStorage.userID);

    var wsUri = "ws://" + location.host + "/ProjectV1/chatendpoint";
    var websocket;
    var wsCheck = true;
    var pollInterval;

    init();

    function init() {
        $("#message,#sendMessage").attr("disabled", "disabled");
        $("#messageSpace").html("<div id='noMessagesFound'>Select a group</div>");

        $.ajax({
            type: "GET",
            url: "/ProjectV1/API/Users/" + userID + "/",
            headers: {
                "Authorization": basicCredentials
            },
            dataType: 'xml'
            , success: function (myInformation) {
                var userName = $(myInformation).find("username");
                $(".usernamespan").html(userName);
            }
        });
        //$("#userlist").html(" ");

        $.ajax({
            type: "GET",
            url: "/ProjectV1/API/Users/" + userID + "/groups/",
            headers: {
                "Authorization": basicCredentials
            },
            dataType: 'xml'
            , success: function (groupInformation) {
                $("#grouplist").append("<div id='groupWrapper'>");


                if ($(groupInformation).find("group").length > 0) {
                    $(groupInformation).find("group").each(function () {
                        var groupName = $(this).find("groupName").text();
                        var groupID = $(this).find("groupID").text();
                        $("#groupWrapper").append("<div class='group' id='group_" + groupID + "'><h3>" + groupName + "</h3><span class='moreGroupInfo'>▼</span></div>");

                        $("#group_" + groupID).append("<div class='groupInformation' id='group_" + groupID + "_info'><hr>\n\
                        <div class='leaveThisGroupButton userInfoButton' id='leaveGroup_" + groupID + "'><img src='pictures/leave.png' alt='Leave group' title='Leave group' height='20' width='20'></div>\n\
                        </div>");

                        var thisGroupAdmins = [];
                        //checking if user is admin of this group
                        $.ajax({
                            type: "GET",
                            url: "/ProjectV1/API/Groups/" + groupID + "/admins/",
                            dataType: 'xml',
                            headers: {
                                "Authorization": basicCredentials
                            },
                            success: function (admins) {
                                $('user', admins).each(function () {
                                    thisGroupAdmins.push($(this).find("userID").text());
                                });

                                if ($.inArray(userID, thisGroupAdmins) > -1) {
                                    $("#group_" + groupID + "_info").append("<div class='deleteThisGroupButton userInfoButton' id='deleteGroup_" + groupID + "'><img src='pictures/deletegroup.png' alt='Delete group' title='Delete group' height='20' width='20'></div>");
                                }
                            }
                        });
                    });

                    $("#grouplist").append("</div>");
                } else {
                    $("#groupWrapper").html("<div id='noGroupsFound'>No groups found.</div>");
                }
            }
        });

        $.ajax({
            type: "GET",
            url: "/ProjectV1/API/Users/",
            dataType: 'xml'
            , success: function (userlist) {
                $("#userlist").append("<div id='usersWrapper'>");
                $('user', userlist).each(function () {
                    var thisuserID = $(this).find("userID").text();
                    var thisuserName = $(this).find("username").text();
                    var thisfirstName = $(this).find("firstname").text();
                    var thislastName = $(this).find("lastname").text();
                    var thisEmail = $(this).find("email").text();
                    var thisDepartment = $(this).find("department").text();
                    var thisTitle = $(this).find("title").text();
                    
                    var userInfoButton = "<span class='loadMoreUserInfo'>▼</span>";
                    if (parseInt(thisuserID) === parseInt(userID)) {
                        thisuserName = "Me";
                        userInfoButton = "";
                    }
                    $("#usersWrapper").append("<div class='usercontainer' id='user_" + thisuserID + "'><h3>" + thisuserName+"</h3>"+userInfoButton+"<br><br></div>");

                    $("#user_" + thisuserID).append("<div class='userinformation' id='user_" + thisuserID + "_info'>\n\
                    <hr><p>" + thisfirstName + "</p>\n\
                    <p>" + thislastName + "</p>\n\
                    <p>" + thisDepartment + "</p>\n\
                    <p>" + thisTitle + "</p>\n\
                    <p>" + thisEmail + "</p><hr>\n\
                    <div class='startChatWithUser userInfoButton' id='startWithUser_" + thisuserID + "'><img src='pictures/newchat.png' alt='New chat' title='New chat' height='20' width='20'></div>\n\
                    </div>");
                });
                $("#userlist").append("</div>");
            }
        });

        $("#placeholder").attr("id", userID);
    }
    
    $("#message").on('input', function () {

        while ($("#message").val().length > 500) {
            $("#message").val($("#message").val().slice(0, -1));
        }

        var charsRemaining = 500 - $("#message").val().length;
        $("#charactersRemaining").html(charsRemaining);

    });

    //Send message by pressing enter
    $('#message').keypress(function (e) {
        if (e.which == 13)  // the enter key code
        {
            $('#sendMessage').click();
            return false;
        }
    });

    //Sending a message
    $("#sendMessage").click(function () {
        var message = $("#message").val();
        $("#message").val("");
        $("#message").height(30);
        if (message != "") {
            var userID = $(".username").attr("id");
            var currentgroupID = $(".usergroup").attr("id");

            $.ajax({
                type: "POST",
                url: "/ProjectV1/API/Groups/" + currentgroupID + "/users/" + userID + "/messages/",
                headers: {
                    "Authorization": basicCredentials
                },
                data: {message: message}
                , success: function (messageCreation) {
                    $("#charactersRemaining").html(500);
                    if (wsCheck === true) {
                        sendMsg(currentgroupID);
                    }
                }
            });
        }
    });


    //Scrolls the messagespace to the bottom
    function scrollToBot() {
        var height = 0;
        $('.message').each(function (i, value) {
            height += parseInt($(this).height()) + 2;
        });
        height += '';
        $('#messageSpace').scrollTop(height);

    }



    /***********************
     ****LOADING A GROUP****
     ************************/
    $(document).on('click', '.group h3', function () {
        //Closing existing websockets
        if (websocket) {
            websocket.close();
        }

        var groupID = $(this).parent().closest('div').attr("id");
        var groupIDSplit = groupID.split("_");
        $(".usergroup").attr("id", groupIDSplit[1]);

        $("#message,#sendMessage").removeAttr("disabled");
        $("#charactersRemaining").html(500);

        $("#messageSpace").html(" ");
        $("#userlist").html(" ");
        $("#message").height(30);
        $("#message").val("");
        $(".groupbuttons").html("");

        $(".group").css("background-color", "#6394cf");
        $("#group_" + groupIDSplit[1]).css("background-color", "#366db0");

        $("#currentGroupName").html("<h3>" + $(this).text() + "</h3>");
        $(".userlist_container h2").html("Participants");

        //Loading admins and then users
        var adminIDArray = [];
        var isAdmin = false;
        $.ajax({
            type: "GET",
            url: "/ProjectV1/API/Groups/" + groupIDSplit[1] + "/admins/",
            dataType: 'xml',
            headers: {
                "Authorization": basicCredentials
            },
            success: function (admins) {
                $('user', admins).each(function () {
                    adminIDArray.push($(this).find("userID").text());
                });

                if ($.inArray(userID, adminIDArray) > -1) {
                    isAdmin = true;
                    $(".groupbuttons").append("<hr>");
                    $(".groupbuttons").append("<span id='addusertogroup'><img src='pictures/adduserb.png' alt='Account' title='Account' height='20' width='20'>Add user</span>");
                    $(".groupbuttons").append("<span id='removeuserfromgroup'><img src='pictures/removeuserb.png' alt='Account' title='Account' height='20' width='20'>Remove user</span>");
                    $(".groupbuttons").append("<span id='promoteuser'><img src='pictures/promoteuserb.png' alt='Account' title='Account' height='20' width='20'>Promote to admin</span>");
                    $(".groupbuttons").append("<span id='modifygroup'><img src='pictures/editgroupb.png' alt='Account' title='Account' height='20' width='20'>Modify group info</span>");
                    $(".groupbuttons").append("<span id='deletegroup'><img src='pictures/deletegroupb.png' alt='Account' title='Account' height='20' width='20'>Delete group</span>");

                }
                //Loading users
                $.ajax({
                    type: "GET",
                    url: "/ProjectV1/API/Groups/" + groupIDSplit[1] + "/users/",
                    dataType: 'xml',
                    headers: {
                        "Authorization": basicCredentials
                    }
                    , success: function (users) {
                        $('user', users).each(function () {

                            var thisuserID = $(this).find("userID").text();
                            var thisuserName = $(this).find("username").text();
                            var thisfirstName = $(this).find("firstname").text();
                            var thislastName = $(this).find("lastname").text();
                            var thisEmail = $(this).find("email").text();
                            var thisDepartment = $(this).find("department").text();
                            var thisTitle = $(this).find("title").text();
                            
                            var userInfoButton = "<span class='loadMoreUserInfo'>▼</span>";
                            if (parseInt(thisuserID) === parseInt(userID)) {
                                thisuserName = "Me";
                                userInfoButton = "";
                            }
                            $("#userlist").append("<div class='usercontainer' id='user_" + thisuserID + "'><h3>" + thisuserName+"</h3>"+userInfoButton+"<br><br></div>");
                            $("#user_" + thisuserID).append("<div class='userinformation' id='user_" + thisuserID + "_info'>\n\
                            <hr><p>" + thisfirstName + "</p>\n\
                            <p>" + thislastName + "</p>\n\
                            <p>" + thisDepartment + "</p>\n\
                            <p>" + thisTitle + "</p>\n\
                            <p>" + thisEmail + "</p><hr>\n\
                            <div class='startChatWithUser userInfoButton' id='startWithUser_" + thisuserID + "'><img src='pictures/newchat.png' alt='New chat' title='New chat' height='20' width='20'></div>\n\
                            </div>");
                            if ($.inArray(thisuserID, adminIDArray) > -1) {
                                $("#user_" + thisuserID + " h3").after("<div class='adminIcon'><img src='pictures/admin.png' alt='Admin' title='Admin' height='15' width='15'></div>");
                            }
                            if (isAdmin === true) {
                                $("#user_" + thisuserID + "_info").append("<div class='userInfoRemoveButton userInfoButton' id='uInfoRemoveUser_" + thisuserID + "'><img src='pictures/removeuser.png' alt='Remove user' title='Remove user' height='20' width='20'></div>");
                                if ($.inArray(thisuserID, adminIDArray) === -1) {
                                    $("#user_" + thisuserID + "_info").append("<div class='userInfoPromoteButton userInfoButton' id='uInfoPromoteUser_" + thisuserID + "'><img src='pictures/promoteuser.png' alt='Promote user' title='Promote user' height='20' width='20'></div>");
                                }

                            }
                        });
                    }
                });
            }
        });
        //Loading messages to the message screen
        $.ajax({
            type: "GET",
            url: "/ProjectV1/API/Groups/" + groupIDSplit[1] + "/messages/",
            dataType: 'xml',
            headers: {
                "Authorization": basicCredentials
            },
            success: function (messages) {
                if ($(messages).find("messageroot").length > 0) {
                    $('messageroot', messages).each(function () {

                        var senderID = $(this).find("userID").text();
                        var message = $(this).find("message").text();
                        var messageID = $(this).find("messageID").text();
                        var timestamp = formatTimestamp($(this).find("timestamp").text());
                        var senderName = $(this).find("username").text();

                        var messageclasses = "message";

                        if (senderID === userID) {
                            messageclasses = "message mymessage"
                            senderName = "Me";
                        }
                        $("#messageSpace").append("<div class='" + messageclasses + "' id='message_" + messageID + "'><p>" + escapeHtml(message) + "</p><p>" + senderName + " <span class='timestamp'>" + timestamp + "</span></p></div>");
                    });
                } else {
                    $("#messageSpace").html("<div id='noMessagesFound'>No messages yet</div>");
                }
                scrollToBot();

                websocket = new WebSocket(wsUri);

                websocket.onopen = function (event) {
                    //onOpen(event);
                };
                websocket.onmessage = function (event) {
                    onMessage(event);
                };
                websocket.onerror = function (event) {
                    wsCheck = false;
                    fallbackPoll(groupIDSplit[1]);
                };
                websocket.onclose = function (event) {
                    console.log('Closing');
                };
            }
        });

        $(".groupbuttons").append("<span id='leavegroup'><img src='pictures/leaveb.png' alt='Account' title='Account' height='20' width='20'>Leave group</span>");



    });
    function fallbackPoll(groupID) {
        console.log("WebSocket failed, using polling.");

        //Closing existing polling
        if (pollInterval) {
            console.log("Closed poll " + pollInterval);
            clearInterval(pollInterval);
        }
        pollInterval = setInterval(function () {
            checkMessages(groupID);
        }, 500);
        console.log("Started new poll on group " + groupID + " with poll " + pollInterval);
    }

    function onMessage(event) {
        console.log("New message in group " + event.data);
        loadMessages(event.data);
    }
    function sendMsg(msg) {
        websocket.send(msg);
    }

    function checkMessages(groupID) {

        $.ajax({
            type: "GET",
            url: "/ProjectV1/API/Groups/" + groupID + "/messages/latest/",
            headers: {
                "Authorization": basicCredentials
            },
            success: function (latestmessageID) {
                var messageAttrID = $("#messageSpace div:last").attr("id");
                var latestMessage;

                if (!messageAttrID) {
                    latestMessage = "0";
                } else {
                    var messageIDSplit = messageAttrID.split("_");
                    latestMessage = messageIDSplit[1];
                }

                if (parseInt(latestmessageID) > parseInt(latestMessage)) {
                    loadMessages(groupID);
                }
            }
        });
    }

    function loadMessages(groupID) {
        var currentGroup = $(".usergroup").attr("id");
        $("#noMessagesFound").remove();
        if (parseInt(groupID) === parseInt(currentGroup)) {
            var messageAttrID = $("#messageSpace div:last").attr("id");
            var latestMessage;

            if (!messageAttrID) {
                latestMessage = "0";
            } else {
                var messageIDSplit = messageAttrID.split("_");
                latestMessage = messageIDSplit[1];
            }

            $.ajax({
                type: "GET",
                url: "/ProjectV1/API/Groups/" + groupID + "/messages/",
                dataType: 'xml',
                headers: {
                    "Authorization": basicCredentials
                },
                success: function (messages) {
                    $('messageroot', messages).each(function () {
                        var messageID = $(this).find("messageID").text();
                        if (parseInt(messageID) > parseInt(latestMessage)) {
                            var timestamp = formatTimestamp($(this).find("timestamp").text());
                            var senderName = $(this).find("username").text();
                            var senderID = $(this).find("userID").text();
                            var message = $(this).find("message").text();
                            //Getting the username
                            var senderName = $(this).find("username").text();
                            //Getting the username
                            var messageclasses = "message";

                            if (senderID === userID) {
                                messageclasses = "message mymessage"
                                senderName = "Me";
                            }
                            /* var newMessage = $("<div class='" + messageclasses + "' id='message_" + messageID + "'><p>" + message + "</p><p>" + senderName + " <span class='timestamp'>" + timestamp + "</span></p></div>").hide();
                             $("#messageSpace").append(newMessage);
                             newMessage.show("slow"); */
                            $("#messageSpace").append("<div class='" + messageclasses + "' id='message_" + messageID + "'><p>" + escapeHtml(message) + "</p><p>" + escapeHtml(senderName) + " <span class='timestamp'>" + timestamp + "</span></p></div>");
                        }
                    });
                    scrollToBot();
                }
            });
        }
    }
    $(document).on('click', '.loadMoreUserInfo', function () {
        var clickedUserID = $(this).parent().closest('div').attr("id").split("_")[1];
        if (clickedUserID !== userID) {
            if (!$("#user_" + clickedUserID + "_info").is(":visible")) {
                $(".loadMoreUserInfo").html("▼");
                $(".userinformation").hide("fast");
                $(this).html("▲");
                $("#user_" + clickedUserID + "_info").toggle("fast");
            } else {
                $(this).html("▼");
                $("#user_" + clickedUserID + "_info").hide("fast");
            }
        }
    });

    $(document).on('click', '.moreGroupInfo', function () {
        var clickedGroupID = $(this).parent().closest('div').attr("id").split("_")[1];
        if (!$("#group_" + clickedGroupID + "_info").is(":visible")) {
            $(".moreGroupInfo").html("▼");
            $(".groupInformation").hide("fast");
            $(this).html("▲");
            $("#group_" + clickedGroupID + "_info").toggle("fast");
        } else {
            $(this).html("▼");
            $("#group_" + clickedGroupID + "_info").hide("fast");
        }
    });

    function formatTimestamp(timestamp) {
        var splitTimestamp = timestamp.split(" ");
        var date = splitTimestamp[0];
        var time = splitTimestamp[1];

        var splitDate = date.split("-");
        var splitTime = time.split(":");

        return splitDate[2] + "." + splitDate[1] + " " + splitTime[0] + ":" + splitTime[1];

    }


    function escapeHtml(string) {
        var entityMap = {
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': '&quot;',
            "'": '&#39;',
            "/": '&#x2F;'
        };
        return String(string).replace(/[&<>"'\/]/g, function (s) {
            return entityMap[s];
        });
    }

    $("#logout").click(function () {
        sessionStorage.removeItem('userID');
        sessionStorage.removeItem('credentials');
        window.location.href = "/ProjectV1/";
    });
    $("#home").click(function () {
        window.location.href = "/ProjectV1/chatscreen.html";
    });
    $("#sidebars_on").click(function () {
        $("#sidebars").toggle();

        if ($('#sidebars').css('display') == 'block') {
            $("#sidebars_on").html("<img src='pictures/hide.png' alt='Hide sidebars' title='Hide sidebars' height='30' width='30'>");
        } else {
            $("#sidebars_on").html("<img src='pictures/show.png' alt='Show sidebars' title='Show sidebars' height='30' width='30'>");
        }
    });

    $(document).on('click', ".leaveThisGroupButton", function () {
        leaveGroup($(this).attr("id").split("_")[1]);
    });

    $(document).on('click', "#leavegroup", function () {
        leaveGroup($(".usergroup").attr("id"));
    });
    function leaveGroup(leaveGroupID) {
        var confirmation = confirm("Are you sure you want to leave this group?");
        if (confirmation === true) {

            $.ajax({
                type: "DELETE",
                url: "/ProjectV1/API/Groups/" + leaveGroupID + "/leave/",
                headers: {
                    "Authorization": basicCredentials
                },
                success: function (messages) {
                    $("#group_" + leaveGroupID).hide("slow", function () {
                        $("#group_" + leaveGroupID).remove();
                    });

                    if (leaveGroupID === $(".usergroup").attr("id")) {
                        $("#messageSpace").html("<div id='noMessagesFound'><h2>Group left.</h2><h2>Returning to home</h2></div>");
                        $("#userlist").html("");
                        setTimeout(function () {
                            window.location.href = "/ProjectV1/chatscreen.html";
                        }, 1500);
                    }
                    if ($("#groupWrapper:not(:empty)").length <= 0) {
                        $("#groupWrapper").html("<div id='noGroupsFound'>No groups found.</div>");
                    }

                }
            });
        }
    }

    $(document).on('click', "#addusertogroup", function () {
        $(".dropdown-content").hide();
        $(".addUsersList").html("");
        $.ajax({
            type: "GET",
            url: "/ProjectV1/API/Users/",
            dataType: 'xml'
            , success: function (userlist) {
                $('user', userlist).each(function () {
                    var thisuserID = $(this).find("userID").text();
                    var thisuserName = $(this).find("username").text();

                    if (parseInt(thisuserID) === parseInt(userID)) {
                        thisuserName = "Me";
                    }

                    if ($("#user_" + thisuserID).length === 0) {
                        $(".addUsersList").append("<div class='addUserContainer' id='addUser_" + thisuserID + "'><h3>" + thisuserName + " <span class='addThisUserButton'>+</span></h3></div>");
                    }
                });
            }
        });

        $("#addUsersToGroup").show();
    });

    $(document).on('click', ".addThisUserButton", function () {
        var userContainerID = $(this).parent().closest('div').attr("id");
        var userContainerIDSplit = userContainerID.split("_");
        var currentGroupID = $(".usergroup").attr("id");

        $("#addUser_" + userContainerIDSplit[1]).css({"position": "relative", "height": "28px", "width": "175px"});

        setTimeout(function () {
            $("#addUser_" + userContainerIDSplit[1]).css({"height": "24px", "width": "170px"});
        }, 200);
        //TODO
        setTimeout(function () {
            $.ajax({
                type: "POST",
                url: "/ProjectV1/API/Groups/" + currentGroupID + "/users/",
                data: {userID: userContainerIDSplit[1]},
                headers: {
                    "Authorization": basicCredentials
                }, success: function () {
                    $("#addUser_" + userContainerIDSplit[1]).remove();

                    var adminIDArray = [];
                    var isAdmin = false;
                    $.ajax({
                        type: "GET",
                        url: "/ProjectV1/API/Groups/" + currentGroupID + "/admins/",
                        dataType: 'xml',
                        headers: {
                            "Authorization": basicCredentials
                        },
                        success: function (admins) {
                            $('user', admins).each(function () {
                                adminIDArray.push($(this).find("userID").text());
                            });

                            if ($.inArray(userID, adminIDArray) > -1) {
                                isAdmin = true;
                            }
                            //Loading a user
                            $.ajax({
                                type: "GET",
                                url: "/ProjectV1/API/Users/" + userContainerIDSplit[1] + "/"
                                , success: function (user) {
                                    var thisuserID = $(user).find("userID").text();
                                    var thisuserName = $(user).find("username").text();
                                    var thisfirstName = $(user).find("firstname").text();
                                    var thislastName = $(user).find("lastname").text();
                                    var thisEmail = $(user).find("email").text();
                                    var thisDepartment = $(user).find("department").text();
                                    var thisTitle = $(this).find("title").text();
                                    
                                    var userInfoButton = "<span class='loadMoreUserInfo'>▼</span>";
                                    if (parseInt(thisuserID) === parseInt(userID)) {
                                        thisuserName = "Me";
                                        userInfoButton = "";
                                    }
                                    $("#userlist").append("<div class='usercontainer' id='user_" + thisuserID + "'><h3>" + thisuserName+"</h3>"+userInfoButton+"<br><br></div>");
                                    $("#user_" + thisuserID).append("<div class='userinformation' id='user_" + thisuserID + "_info'>\n\
                                    <hr><p>" + thisfirstName + "</p>\n\
                                    <p>" + thislastName + "</p>\n\
                                    <p>" + thisDepartment + "</p>\n\
                                    <p>" + thisTitle + "</p>\n\
                                    <p>" + thisEmail + "</p><hr>\n\
                                    <div class='startChatWithUser userInfoButton' id='startWithUser_" + thisuserID + "'><img src='pictures/newchat.png' alt='New chat' title='New chat' height='20' width='20'></div>\n\
                                    </div>");
                                    
                                    if ($.inArray(thisuserID, adminIDArray) > -1) {
                                        $("#user_" + thisuserID + " h3").append("<span style='float: right;'>A</span>");
                                    }
                                    if (isAdmin === true) {
                                        $("#user_" + thisuserID + "_info").append("<div class='userInfoRemoveButton userInfoButton' id='uInfoRemoveUser_" + thisuserID + "'><img src='pictures/removeuser.png' alt='Remove user' title='Remove user' height='20' width='20'></div>");
                                        if ($.inArray(thisuserID, adminIDArray) === -1) {
                                            $("#user_" + thisuserID + "_info").append("<div class='userInfoPromoteButton userInfoButton' id='uInfoPromoteUser_" + thisuserID + "'><img src='pictures/promoteuser.png' alt='Promote user' title='Promote user' height='20' width='20'></div>");
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }, 300);
    });


    $(document).on('click', "#removeuserfromgroup", function () {
        $(".dropdown-content").hide();
        $(".removeUsersList").html("");
        var currentGroupID = $(".usergroup").attr("id");

        $.ajax({
            type: "GET",
            url: "/ProjectV1/API/Groups/" + currentGroupID + "/users/",
            dataType: 'xml',
            headers: {
                "Authorization": basicCredentials
            }
            , success: function (groupuserlist) {
                $('user', groupuserlist).each(function () {
                    var thisuserID = $(this).find("userID").text();
                    var thisuserName = $(this).find("username").text();
                    if (parseInt(thisuserID) !== parseInt(userID)) {
                        $(".removeUsersList").append("<div class='removeUserContainer' id='removeUser_" + thisuserID + "'><h3>" + thisuserName + " <span class='removeThisUserButton'>-</span></h3></div>");
                    }
                });
            }
        });

        $("#removeUsersFromGroup").show();
    });

    $(document).on('click', ".removeThisUserButton", function () {
        var confirmation = confirm("Are you sure you want to remove this user from the group?");
        if (confirmation === true) {
            var userContainerID = $(this).parent().closest('div').attr("id");
            var userContainerIDSplit = userContainerID.split("_");
            var currentGroupID = $(".usergroup").attr("id");

            $("#removeUser_" + userContainerIDSplit[1]).css({"position": "relative", "height": "28px", "width": "175px"});

            setTimeout(function () {
                $("#removeUser_" + userContainerIDSplit[1]).css({"height": "24px", "width": "170px"});
            }, 200);

            $.ajax({
                type: "DELETE",
                url: "/ProjectV1/API/Groups/" + currentGroupID + "/users/" + userContainerIDSplit[1] + "/",
                headers: {
                    "Authorization": basicCredentials
                }, success: function () {
                    $("#removeUser_" + userContainerIDSplit[1]).remove();

                    $("#user_" + userContainerIDSplit[1]).remove();
                }
            });
        }
    });

    $(document).on('click', ".userInfoRemoveButton ", function () {
        $("#removeuserfromgroup").click();
        $("#removeUsersFromGroup").hide();

        var clickedUserID = $(this).attr("id").split("_")[1];
        setTimeout(function () {
            $("#removeUser_" + clickedUserID + " span").click();
        }, 100);

    });

    $(document).on('click', "#modifygroup", function () {
        $(".dropdown-content").hide();
        $("#groupUpdateErrors").html("");
        var currentGroupID = $(".usergroup").attr("id");
        $.ajax({
            type: "GET",
            url: "/ProjectV1/API/Groups/" + currentGroupID + "/",
            dataType: 'xml',
            success: function (groupinfo) {
                var groupname = $(groupinfo).find("groupName").text();
                $("#updateGroupName").val(groupname);
            }
        });

        $("#changeGroupInfo").show();
    });

    $(document).on('click', "#saveGroupInfo", function () {
        $("#groupUpdateErrors").html("");
        var newGroupname = $("#updateGroupName").val();
        var currentGroupID = $(".usergroup").attr("id");

        var newNameCheck = true;
        if (newGroupname.length > 16) {
            newNameCheck = false;
            $("#groupUpdateErrors").html("Group name is too long.");
        } else if (newGroupname.length <= 0) {
            newNameCheck = false;
            $("#groupUpdateErrors").html("Group name is too short.");
        }

        if (newNameCheck === true) {
            $.ajax({
                type: "PUT",
                url: "/ProjectV1/API/Groups/" + currentGroupID + "/",
                headers: {
                    "Authorization": basicCredentials
                }
                , data: {newname: newGroupname}
                , success: function () {
                    $("#groupUpdateErrors").html("Group name updated!");
                    $("#group_" + currentGroupID).html("<h3>" + newGroupname + "</h3>");
                    $("#currentGroupName").html("<h3>" + newGroupname + "</h3>");
                    console.log(currentGroupID);
                    setTimeout(function () {
                        $("#changeGroupInfo").hide();
                    }, 750);
                }
            });
        }

    });

    $(document).on('click', ".deleteThisGroupButton", function () {
        var thisGroupID = $(this).attr("id").split("_")[1];
        deleteGroup(thisGroupID);
    });

    $(document).on('click', "#deletegroup", function () {
        var currentGroupID = $(".usergroup").attr("id");
        deleteGroup(currentGroupID);
    });

    function deleteGroup(deletedGroupID) {
        var confirmation = confirm("Are you sure you want to delete this group?");
        if (confirmation === true) {

            $.ajax({
                type: "DELETE",
                url: "/ProjectV1/API/Groups/" + deletedGroupID + "/",
                headers: {
                    "Authorization": basicCredentials
                }
                , success: function () {
                    if (deletedGroupID === $(".usergroup").attr("id")) {
                        setTimeout(function () {
                            window.location.href = "/ProjectV1/chatscreen.html";
                        }, 750);
                    } else {
                        $("#group_" + deletedGroupID).hide("slow", function () {
                            $("#group_" + deletedGroupID).remove();
                        });

                        if ($("#groupWrapper:not(:empty)").length <= 0) {
                            $("#groupWrapper").html("<div id='noGroupsFound'>No groups found.</div>");
                        }
                    }
                }, beforeSend: function () {
                    if (deletedGroupID === $(".usergroup").attr("id")) {
                        $("#messageSpace").html("<div id='noMessagesFound'><h3>Group deleted.</h3><h3>Returning to home</h3></div>");
                    }

                }
            });
        }
    }
    $(document).on('click', '#promoteuser', function () {
        $(".dropdown-content").hide();
        $("#promoteUsersToAdmin").show();
        $(".promoteUsersList").html("");
        var currentGroupID = $(".usergroup").attr("id");

        var thisGroupAdmins = [];
        //getting group admins
        $.ajax({
            type: "GET",
            url: "/ProjectV1/API/Groups/" + currentGroupID + "/admins/",
            dataType: 'xml',
            headers: {
                "Authorization": basicCredentials
            },
            success: function (admins) {
                $('user', admins).each(function () {
                    thisGroupAdmins.push($(this).find("userID").text());
                });

                $.ajax({
                    type: "GET",
                    url: "/ProjectV1/API/Groups/" + currentGroupID + "/users/",
                    dataType: 'xml',
                    headers: {
                        "Authorization": basicCredentials
                    }
                    , success: function (groupuserlist) {
                        $('user', groupuserlist).each(function () {
                            var thisuserID = $(this).find("userID").text();
                            var thisuserName = $(this).find("username").text();
                            if (parseInt(thisuserID) !== parseInt(userID) && $.inArray(thisuserID, thisGroupAdmins) === -1) {
                                $(".promoteUsersList").append("<div class='removeUserContainer' id='promoteUser_" + thisuserID + "'><h3>" + thisuserName + " <span class='promoteThisUserButton'>^</span></h3></div>");
                            }
                        });
                    }
                });
            }
        });
    });

    $(document).on('click', '.promoteThisUserButton', function () {
        var clickedUserID = $(this).parent().closest('div').attr("id").split("_")[1];
        promoteUser(clickedUserID);
    });

    $(document).on('click', '.userInfoPromoteButton', function () {
        var clickedUserID = $(this).attr("id").split("_")[1];
        promoteUser(clickedUserID);
    });

    function promoteUser(clickedUserID) {
        var confirmation = confirm("Are you sure you want to promote this user?");
        if (confirmation === true) {
            var currentGroupID = $(".usergroup").attr("id");

            $.ajax({
                type: "POST",
                url: "/ProjectV1/API/Groups/" + currentGroupID + "/admins/",
                data: {userID: clickedUserID},
                headers: {
                    "Authorization": basicCredentials
                }
                , success: function () {
                    $("#promoteUser_" + clickedUserID).hide("slow").remove();
                    $("#uInfoPromoteUser_" + clickedUserID).remove();
                    $("#user_"+clickedUserID+" h3").after("<div class='adminIcon'><img src='pictures/admin.png' alt='Admin' title='Admin' height='15' width='15'></div>");
                }
            });
        }
    }

    $(document).on('click', '#changeMyInfo', function () {
        $(".dropdown-content").hide();
        $("#myInformation").show();
        $("#myInformationWrapper").html("");
        $("#myInformationPasswordWrapper").html("");
        $("#editMyInfo").show();
        $("#myInformationPasswordWrapper").hide();
        $("#myInformationWrapper").show();
        $("#showbasicInfo").css("background-color", "#283571");
        $("#showbasicInfo").css("border-color", "#283571");
        $.ajax({
            type: "GET",
            url: "/ProjectV1/API/Users/" + userID + "/",
            headers: {
                "Authorization": basicCredentials
            },
            dataType: 'xml'
            , success: function (userDetails) {
                var thisuserID = $(userDetails).find("userID").text();
                var thisuserName = $(userDetails).find("username").text();
                var thisfirstName = $(userDetails).find("firstname").text();
                var thislastName = $(userDetails).find("lastname").text();
                var thisEmail = $(userDetails).find("email").text();
                $("#myInformationWrapper").append("Username <input type='text' disabled value='" + thisuserName + "' class='myInfoInput' id='myInfoUname'>\n\
                First name <input disabled type='text' value='" + thisfirstName + "' class='myInfoInput' id='myInfoFname'>\n\
                Last name <input disabled type='text' value='" + thislastName + "' class='myInfoInput' id='myInfoLname'>\n\
                Email <input disabled type='text' value='" + thisEmail + "' class='myInfoInput' id='myInfoEmail'>");

                $("#myInformationPasswordWrapper").append("New password <input type='password'  id='myInfoNewPassword'>\n\
                New password again <input type='password'  id='myInfoNewPasswordAgain'>\n\
                Current password <input type='password' id='myInfoCurrentPassword'>");
            }
        });
    });

    //Toggling password / basic user information forms
    $("#showbasicInfo").click(function () {
        $("#showbasicInfo").css("background-color", "#283571");
        $("#showbasicInfo").css("border-color", "#283571");
        $("#changePassword").css("background-color", "#6394cf");
        $("#changePassword").css("border-color", "#6394cf");

        $("#myInformationPasswordWrapper").hide();
        $("#myInformationWrapper").show();

        $("#cancelEditMyInfo").click();
        $("#saveNewPassword").hide();
    });
    $("#changePassword").click(function () {
        $("#changePassword").css("background-color", "#283571");
        $("#changePassword").css("border-color", "#283571");
        $("#showbasicInfo").css("background-color", "#6394cf");
        $("#showbasicInfo").css("border-color", "#6394cf");

        $("#myInformationPasswordWrapper").show();
        $("#myInformationWrapper").hide();

        $(".myinfobutton").hide();
        $("#saveNewPassword").show();
    });

    $("#saveNewPassword").click(function () {
        var newPassword = $("#myInfoNewPassword").val();
        var newPasswordAgain = $("#myInfoNewPasswordAgain").val();
        var currentPassword = $("#myInfoCurrentPassword").val();
        var newPasswordCheck = true;

        if (!newPassword) {
            newPasswordCheck = false;
        }
        if (newPassword.length > 16) {
            newPasswordCheck = false;
        }
        if (!newPasswordAgain) {
            newPasswordCheck = false;
        }
        if (newPassword !== newPasswordAgain) {
            newPasswordCheck = false;
        }
        if (!currentPassword) {
            newPasswordCheck = false;
        }

        if (newPasswordCheck === true) {
            $.ajax({
                type: "PUT",
                url: "/ProjectV1/API/Users/" + userID + "/password/",
                headers: {
                    "Authorization": "Basic " + btoa($(".usernamespan").text() + ":" + currentPassword)
                },
                data: {password: newPassword}
                , statusCode: {
                    200: function (response) {

                        sessionStorage.setItem("credentials", "Basic " + btoa($(".usernamespan").text() + ":" + newPassword));
                        basicCredentials = sessionStorage.credentials;

                        console.log("Password updated");

                        $("#myInformationPasswordWrapper").html("<p>Password changed!</p>");
                        setTimeout(function () {
                            $("#saveNewPassword").hide();
                            $("#showbasicInfo").click();
                            $("#changeMyInfo").click();
                        }, 750);

                    },
                    400: function (response) {
                        console.log("Invalid password");
                    }
                }
            });
        }
    });

    $("#editMyInfo").click(function () {
        $("#myInformationWrapper").append("<div id='pwdConfirmationDiv'>Confirm by entering password <input type='password' class='myInfoInput' id='confirmationPassword'></div>");
        $(".myInfoInput").prop("disabled", false);
        $(this).hide();
        $("#saveMyInfo").css("display", "inline-block");
        $("#cancelEditMyInfo").css("display", "inline-block");
    });

    $("#cancelEditMyInfo").click(function () {
        $.ajax({
            type: "GET",
            url: "/ProjectV1/API/Users/" + userID + "/",
            headers: {
                "Authorization": basicCredentials
            },
            dataType: 'xml'
            , success: function (userDetails) {
                $('#myInfoUname').val($(userDetails).find("username").text());
                $('#myInfoFname').val($(userDetails).find("firstname").text());
                $('#myInfoLname').val($(userDetails).find("lastname").text());
                $('#myInfoEmail').val($(userDetails).find("email").text());
            }
        });
        $("#pwdConfirmationDiv").remove();
        $(".myInfoInput").prop("disabled", true);
        $(this).hide();
        $("#saveMyInfo").hide();
        $("#editMyInfo").css("display", "inline-block");
    });

    $("#saveMyInfo").click(function () {
        var newUsername = $('#myInfoUname').val();
        var newFname = $('#myInfoFname').val();
        var newLname = $('#myInfoLname').val();
        var newEmail = $('#myInfoEmail').val();
        
        var validInformation = true;
        if (!newUsername) {
            
            validInformation = false;
        }
        if (newUsername.length > 16) {
            validInformation = false;
        }

        if (!newFname) {
            validInformation = false;
        }
        if (newFname.length > 16) {
            validInformation = false;
        }
        if (!newLname) {
            validInformation = false;
        }
        if (newLname.length > 16) {
            validInformation = false;
        }
        if (!newEmail) {
            validInformation = false;
        }
        if (newEmail.length > 30) {
            validInformation = false;
        }
        if (!$("#confirmationPassword").val()) {
            $("#confirmationPassword").focus();
            validInformation = false;
        }
        
        if (validInformation === true) {
            $.ajax({
                type: "PUT",
                url: "/ProjectV1/API/Users/" + userID + "/",
                headers: {
                    "Authorization": "Basic " + btoa($(".usernamespan").text() + ":" + $("#confirmationPassword").val())
                },
                data: {username: newUsername, firstname: newFname, lastname: newLname, email: newEmail}
                , statusCode: {
                    200: function (response) {

                        sessionStorage.setItem("credentials", "Basic " + btoa(newUsername + ":" + $("#confirmationPassword").val()));
                        basicCredentials = sessionStorage.credentials;
                        $(".usernamespan").html(newUsername);
                        console.log("User values updated");
                        $("#myInformationWrapper").html("<p>User values updated!</p>");
                        setTimeout(function () {
                            $("#saveMyInfo").hide();
                            $("#cancelEditMyInfo").hide();
                            $("#changeMyInfo").click();
                            $("#showbasicInfo").click();
                        }, 750);

                    },
                    400: function (response) {
                        console.log("Invalid values");
                    }
                }
            });
        }


    });

    $(".dropMenu").click(function () {
        $(".dropdown-content").toggle();
        $("#myInformation").hide();
        $("#promoteUsersToAdmin").hide();
        $("#createGroup").hide();
        $("#addUsersToGroup").hide();
        $("#removeUsersFromGroup").hide();
        $("#changeGroupInfo").hide();
        $("#saveNewPassword").hide();
        $("#changePassword").css("background-color", "#6394cf");
        $("#changePassword").css("border-color", "#6394cf");
    });

    $(document).mousedown(function (e) {
        var container = $(".dropdown");
        if (!container.is(e.target) && container.has(e.target).length === 0) {
            $("#promoteUsersToAdmin").hide();
            $("#myInformation").hide();
            $("#createGroup").hide();
            $("#addUsersToGroup").hide();
            $("#removeUsersFromGroup").hide();
            $("#changeGroupInfo").hide();
            $(".dropdown-content").hide();
            $("#saveNewPassword").hide();
            $("#changePassword").css("background-color", "#6394cf");
            $("#changePassword").css("border-color", "#6394cf");
        }
    });
    
    $("#showSearch").click(function(){
        $("#searchInput").val("");
        $("#searchBox").slideToggle("fast");
    });
    
    $("#searchInput").on('input', function () {
        var searchterm = $(this).val().toLowerCase();
        if(searchterm){
            $(".usercontainer").each(function(){
                var info = [];
                info.push($(this).find("h3").text().toLowerCase());
                
                $(this).find("p").each(function(){
                    info.push($(this).text().toLowerCase());
                });
                
                //Checking if searchterm is contained in user info
                var stringInInfo = false;
                
                for (var i = 0; i < info.length ; i++) {
                    if(info[i].indexOf(searchterm) > -1){
                        stringInInfo = true;
                    }
                }
                
                if(stringInInfo === true){
                    $(this).show(200);
                }else{
                    $(this).hide(200);
                }
            });
        }else{
             $(".usercontainer").show();
        }
    });
    
    
    
    
    $('#grouplist').bind('scroll', chk_scroll);
    function chk_scroll(e)
    {
        var elem = $(e.currentTarget);
        if (elem[0].scrollHeight - elem.scrollTop() == elem.outerHeight())
        {
            $("#botNotifier").hide();
        } else {
            $("#botNotifier").show();
        }
        if (elem.scrollTop() == 0)
        {
            $("#topNotifier").hide();
        } else {
            $("#topNotifier").show();
        }
    }
});


