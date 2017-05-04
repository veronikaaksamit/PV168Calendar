<%--
  Created by IntelliJ IDEA.
  User: Dadka
  Date: 21.04.2017
  Time: 0:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<body>

<table border="1">
    <thead>
    <tr>
        <th>jmeno</th>
        <th>email</th>
    </tr>
    </thead>
    <c:forEach items="${users}" var="user">
        <tr>
            <td><c:out value="${user.fullName}"/></td>
            <td><c:out value="${user.email}"/></td>
            <td><form method="post" action="${pageContext.request.contextPath}/users/delete?id=${user.id}"
                      style="margin-bottom: 0;"><input type="submit" value="Smazat"></form></td>
        </tr>
    </c:forEach>
</table>

<h2>Zadejte usera</h2>
<c:if test="${not empty chyba}">
    <div style="border: solid 1px red; background-color: yellow; padding: 10px">
        <c:out value="${chyba}"/>
    </div>
</c:if>
<form action="${pageContext.request.contextPath}/users/add" method="post">
    <table>
        <tr>
            <th>jmeno:</th>
            <td><input type="text" name="fullName" value="<c:out value='${param.fullName}'/>"/></td>
        </tr>
        <tr>
            <th>email:</th>
            <td><input type="text" name="email" value="<c:out value='${param.email}'/>"/></td>
        </tr>
    </table>
    <input type="Submit" value="Zadat" />
</form>

</body>
</html>
