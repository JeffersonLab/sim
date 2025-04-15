<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<c:set var="title" value="Sync"/>
<s:page title="${title}">
    <jsp:attribute name="stylesheets">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/sync.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/sync.js"></script>
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
            <table id="sync-table" data-repo-id="${repository.repositoryId}" class="data-table">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Type</th>
                        <th>Description</th>
                        <th>Maintainers</th>
                        <th>Home URL</th>
                        <th>
                            <button ${diff.hasChanges() ? '' : 'disabled="disabled"'}
                                    id="apply-all-button" type="button">Apply All
                            </button>
                        </th>
                    </tr>
                </thead>
                <tbody>
                <c:choose>
                <c:when test="${error ne null}">
                    Error: <c:out value="${error}"/>
                </c:when>
                <c:when test="${fn:length(remoteList) > 0}">
                <div>Found ${fn:length(remoteList)} remote records, ${fn:length(localList)} local
                    records</div>
                <div> (${diff.matchCount}
                    Matched, ${diff.addCount} Add, ${diff.removeCount} Remove, ${diff.updateCount}
                    Update)
                </div>
                    <c:forEach var="software" items="${diff.addList}">
                        <tr class="add-row"
                            data-name="${fn:escapeXml(software.name)}"
                            data-type="${fn:escapeXml(software.type)}"
                            data-description="${fn:escapeXml(software.description)}"
                            data-maintainer="${fn:escapeXml(software.maintainerUsernameCsv)}"
                            data-url="${fn:escapeXml(software.homeUrl)}">
                            <td><c:out value="${software.name}"/></td>
                            <td><c:out value="${software.type}"/></td>
                            <td><c:out value="${software.description}"/></td>
                            <td><c:out value="${software.maintainerUsernameCsv}"/></td>
                            <td><c:out value="${software.homeUrl}"/></td>
                            <td>
                                <button class="add" type="button">Add</button>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:forEach var="software" items="${diff.updateList}">
                        <tr>
                            <td><c:out value="${software.name}"/></td>
                            <td><c:out value="${software.type}"/></td>
                            <td><c:out value="${software.description}"/></td>
                            <td><c:out value="${software.maintainerUsernameCsv}"/></td>
                            <td><c:out value="${software.homeUrl}"/></td>
                            <td>
                                <button class="update" type="button">Update</button>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:forEach var="software" items="${diff.removeList}">
                        <tr class="remove-row">
                            <td><c:out value="${software.name}"/></td>
                            <td><c:out value="${software.type}"/></td>
                            <td><c:out value="${software.description}"/></td>
                            <td><c:out value="${software.maintainerUsernameCsv}"/></td>
                            <td><c:out value="${software.homeUrl}"/></td>
                            <td>
                                <button class="remove" type="button">Remove</button>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:forEach var="software" items="${diff.matchList}">
                        <tr>
                            <td><c:out value="${software.name}"/></td>
                            <td><c:out value="${software.type}"/></td>
                            <td><c:out value="${software.description}"/></td>
                            <td><c:out value="${software.maintainerUsernameCsv}"/></td>
                            <td><c:out value="${software.homeUrl}"/></td>
                            <td></td>
                        </tr>
                    </c:forEach>
                </c:when>
                    <c:otherwise>No Results Found</c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </section>
    </jsp:body>         
</s:page>
