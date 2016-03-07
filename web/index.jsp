<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>

        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1" /> 
        <script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
        <script type="text/javascript" src="myScript.js"></script>
        <link rel="stylesheet" type="text/css" href="chatCss.css">

        <title>Login</title>
    </head>
    <body>
        <div id="index_content">
            <div id="index_header"> 
                <div style = "background-color: #366db0"  class="index_buttons" id="login_form">Login</div>   <div  class="index_buttons" id="create_form">Sign up</div> 
            </div>


            <div id="inputToggle" >
                <p>Login to an existing user: </p>
                <input type="text" name="name" id="username" placeholder = "Username"><br>
                <input type="password" name="password" id="password" placeholder = "Password"><br>

                <button style="font-size: 20px ; margin-top: 5px;"id="loginButton">Login</button>
            </div>

            <div id="inputToggle2"  style="display: none">
                <p>Create a new user: </p>
                
                <input type="text" name="name" id="newUsername" placeholder = "Username"><div class="registrationArrow" id="usernameArrow">◀</div><div class="registrationError" id="usernameError"></div>
                    <input type="password" name="password" id="newPassword" placeholder = "Password"><div class="registrationArrow" id="passwordArrow">◀</div><div class="registrationError" id="passwordError"></div>
                    <input type="password" name="passwordagain" id="newPasswordAgain" placeholder = "Password again"><div class="registrationArrow" id="newpassArrow">◀</div><div class="registrationError" id="passwordAgainError"></div>
                    <input type="text" name="fname" id="firstname" placeholder = "First name"><div class="registrationArrow" id="fnameArrow">◀</div><div class="registrationError" id="firstnameError"></div>
                    <input type="text" name="lname" id="lastname" placeholder = "Last name"><div class="registrationArrow" id="lnameArrow">◀</div><div class="registrationError" id="lastnameError"></div>
                    <input type="text" name="department" id="department" placeholder = "Department"><div class="registrationArrow" id="depArrow">◀</div><div class="registrationError" id="depError"></div>
                    <input type="text" name="title" id="title" placeholder = "Work title"><div class="registrationArrow" id="titleArrow">◀</div><div class="registrationError" id="titleError"></div>
                    <input type="email" name="email" id="email" placeholder = "Email"><div class="registrationArrow" id="emailArrow">◀</div><div class="registrationError" id="emailError"></div><br>
                  <button style="font-size: 20px ; margin-top: 5px;"id="createButton">Create</button>
                  
            </div>



        </div>
        
    </body>
</html>
