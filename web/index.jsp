

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script type="text/javascript" src="http://code.jquery.com/jquery-latest.min.js"></script>
        <script type="text/javascript" src="myScript.js"></script>
        <title>Login</title>
    </head>
    <body>
        <div class="content">
        
            Username: <input type="text" name="name" id="username"><br>
            Password: <input type="password" name="password" id="password"><br>
            <button id="loginButton">Login</button>
            
        </div>
   <!--<p>REGISTER USER</p>
        <form name="createuserform" action="/ProjectV1/API/Users/" method="POST">
            Username: <input type="text" name="name"><br>
            Password: <input type="password" name="password"><br>
            <input type="submit" name="submit" value="Register">
        </form>
        
    <p>LOGIN</p>
        <form name="login" action="/ProjectV1/API/Users/login/" method="POST">
            Username: <input type="text" name="name"><br>
            Password: <input type="password" name="password"><br>
            <input type="submit" name="submit" value="Login">
        </form>
    
    <p>LOGOUT</p>
        <form name="logout" action="/ProjectV1/API/Users/logout" method="POST">
            <br>
            <input type="submit" name="submit" value="LOGOUT">
        </form> 
    
    <p>CREATE GROUP</p>
        <form name="newgroup" action="/ProjectV1/API/Groups/" method="POST">
            Groupname <input type="text" name="groupname"><br>
            group starter ID <input type="text" name="groupstarterID"><br>
            <input type="submit" name="submit" value="Create group">
        </form>  
    
     <p>ADD USER TO GROUP</p>
        <form name="newgroup" action="/ProjectV1/API/Groups/5/users" method="POST">
            Add user <input type="text" name="userID"><br><br>
            <input type="submit" name="submit" value="Add">
        </form>
     <p>MSG TEST</p>
        <form name="newgroup" action="/ProjectV1/API/Groups/5/users/13/messages" method="POST">
            Message <input type="text" name="message"><br>
            <br>
            <input type="submit" name="submit" value="Send">
        </form> -->
    </body>
</html>
