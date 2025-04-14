<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<c:set var="title" value="Repositories"/>
<s:page title="${title}">
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>        
    <jsp:body>
        <section>
            <h2><c:out value="${title}"/></h2>
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Description</th>
                        <th>Sync</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="repo" items="${repoList}">
                        <tr>
                            <td><a href="${repo.homeUrl}"><c:out value="${repo.name}"/></a></td>
                            <td><c:out value="${repo.description}"/></td>
                            <td>
                                <!-- Use onclick to avoid https://bugs.webkit.org/show_bug.cgi?id=30103 -->
                                <c:url value="/repositories/sync/${repo.repositoryId}"
                                       var="url">
                                </c:url>
                                <form method="get"
                                      action="${pageContext.request.contextPath}/repositories/sync/${repo.repositoryId}">
                                    <button class="single-char-button" type="button"
                                            onclick="window.location.href = '${url}';  return false;">
                                        &rarr;
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </section>
    </jsp:body>         
</s:page>
