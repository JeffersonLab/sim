<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<c:set var="title" value="Sync"/>
<s:page title="${title}">
    <jsp:attribute name="stylesheets">
        <style>
            td {
                word-break: break-word;
            }
            td:nth-child(1) {
                width: 150px;
            }
            td:nth-child(2) {
                width: 70px;
            }
        </style>
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>        
    <jsp:body>
        <div class="banner-breadbox">
            <ul>
                <li>
                    <a href="${pageContext.request.contextPath}/repositories">Repositories</a>
                </li>
                <li>
                    <c:out value="${repository.name}"/>
                </li>
            </ul>
        </div>
        <section>
            <h2><c:out value="${title}"/></h2>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Type</th>
                        <th>Description</th>
                        <th>Maintainers</th>
                        <th>Home URL</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="software" items="${diff.addList}">
                        <tr class="add-list">
                            <td><c:out value="${software.name}"/></td>
                            <td><c:out value="${software.type}"/></td>
                            <td><c:out value="${software.description}"/></td>
                            <td><c:out value="${software.maintainerUsernameCsv}"/></td>
                            <td><c:out value="${software.homeUrl}"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </section>
    </jsp:body>         
</s:page>
