<%@taglib prefix="sql" uri="jakarta.tags.sql"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib prefix="s" uri="jlab.tags.smoothness"%>
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
                            <button ${diff.hasChanges() && (pageContext.request.isUserInRole('sim-admin') or pageContext.request.isUserInRole('acg')) ? '' : 'disabled="disabled"'}
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
                            data-archived="${fn:escapeXml(software.archived)}"
                            data-type="${fn:escapeXml(software.type)}"
                            data-topic-csv="${fn:escapeXml(software.stringTopicCsv)}"
                            data-description="${fn:escapeXml(software.description)}"
                            data-maintainer="${fn:escapeXml(software.maintainerUsernameCsv)}"
                            data-url="${fn:escapeXml(software.homeUrl)}">
                            <td><c:out value="${software.name}"/></td>
                            <td><c:out value="${software.type}"/></td>
                            <td>
                                <div><c:out value="${software.description}"/></div>
                                <ul class="topic-list">
                                    <c:forEach items="${software.stringTopicList}" var="stringTopic">
                                    <li class="topic"><c:out value="${stringTopic}"/></li>
                                    </c:forEach>
                                </ul>
                            </td>
                            <td><c:out value="${software.maintainerUsernameCsv}"/></td>
                            <td>
                                <c:out value="${software.homeUrl}"/>
                                <div>Archived: ${software.archived}</div>
                            </td>
                            <td>
                                <button class="add" type="button"${(pageContext.request.isUserInRole('sim-admin') or pageContext.request.isUserInRole('acg')) ? '' : ' disabled="disabled"'}>Add</button>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:forEach var="software" items="${diff.updateList}">
                        <c:set value="${not empty remoteMap[software.name].homeUrl}"
                               var="homeUrlSync"/>
                        <c:set value="${not empty remoteMap[software.name].stringTopicCsv}"
                               var="topicSync"/>
                        <c:set value="${not empty remoteMap[software.name].archived}"
                               var="archivedSync"/>
                        <tr data-id = "${software.softwareId}"
                            data-name="${fn:escapeXml(software.name)}"
                            data-archived="${fn:escapeXml(archivedSync ? remoteMap[software.name].archived : software.archived)}"
                            data-type="${fn:escapeXml(software.type)}"
                            data-topic-csv="${fn:escapeXml(topicSync ? remoteMap[software.name].stringTopicCsv : software.topicCsv)}"
                            data-description="${fn:escapeXml(remoteMap[software.name].description)}"
                            data-maintainer="${fn:escapeXml(software.maintainerUsernameCsv)}"
                            data-url="${fn:escapeXml(homeUrlSync ? remoteMap[software.name].homeUrl : software.homeUrl)}">
                            <td><c:out value="${software.name}"/></td>
                            <td><c:out value="${software.type}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${software.description eq remoteMap[software.name].description}">
                                        <c:out value="${software.description}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <div>
                                            <div class="remote"><c:out
                                                    value="${remoteMap[software.name].description}"/></div>
                                            <span class="local"><c:out value="${software.description}"/></span>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                                <c:choose>
                                    <c:when test="${not topicSync || software.topicCsv eq remoteMap[software.name].stringTopicCsv}">
                                        <ul class="topic-list">
                                            <c:forEach items="${software.stringTopicList}" var="stringTopic">
                                                <li class="topic"><c:out value="${stringTopic}"/></li>
                                            </c:forEach>
                                        </ul>
                                    </c:when>
                                    <c:otherwise>
                                        <div>
                                            <div class="remote">
                                                <ul class="topic-list">
                                                    <c:forEach items="${remoteMap[software.name].stringTopicList}" var="stringTopic">
                                                        <li class="topic"><c:out value="${stringTopic}"/></li>
                                                    </c:forEach>
                                                </ul>
                                            </div>
                                            <div class="local">
                                <ul class="topic-list">
                                    <c:forEach items="${software.softwareTopicList}" var="softwareTopic">
                                        <li class="topic"><c:out value="${softwareTopic.topic.name}"/></li>
                                    </c:forEach>
                                </ul>
                                            </div>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td><c:out value="${software.maintainerUsernameCsv}"/></td>
                            <td>
                                <c:choose>
                                    <c:when test="${not homeUrlSync || software.homeUrl eq remoteMap[software.name].homeUrl}">
                                        <c:out value="${software.homeUrl}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <div>
                                            <div class="remote"><c:out
                                                    value="${remoteMap[software.name].homeUrl}"/></div>
                                            <span class="local"><c:out value="${software.homeUrl}"/></span>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                                <div>Archived:
                                <c:choose>
                                    <c:when test="${not archivedSync || software.archived eq remoteMap[software.name].archived}">
                                        <c:out value="${software.archived}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <div>
                                            <div class="remote"><c:out
                                                    value="${remoteMap[software.name].archived}"/></div>
                                            <span class="local"><c:out value="${software.archived}"/></span>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                                </div>
                            </td>
                            <td>
                                <button class="update" type="button"${(pageContext.request.isUserInRole('sim-admin') or pageContext.request.isUserInRole('acg')) ? '' : ' disabled="disabled"'}>Update</button>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:forEach var="software" items="${diff.removeList}">
                        <tr class="remove-row" data-id="${software.softwareId}">
                            <td><c:out value="${software.name}"/></td>
                            <td><c:out value="${software.type}"/></td>
                            <td>
                                <div><c:out value="${software.description}"/></div>
                                <ul class="topic-list">
                                    <c:forEach items="${software.softwareTopicList}" var="softwareTopic">
                                        <li class="topic"><c:out value="${softwareTopic.topic.name}"/></li>
                                    </c:forEach>
                                </ul>
                            </td>
                            <td><c:out value="${software.maintainerUsernameCsv}"/></td>
                            <td>
                                <c:out value="${software.homeUrl}"/>
                                <div>Archived: ${software.archived}</div>
                            </td>
                            <td>
                                <button class="remove" type="button"${(pageContext.request.isUserInRole('sim-admin') or pageContext.request.isUserInRole('acg')) ? '' : ' disabled="disabled"'}>Remove</button>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:forEach var="software" items="${diff.matchList}">
                        <tr>
                            <td><c:out value="${software.name}"/></td>
                            <td><c:out value="${software.type}"/></td>
                            <td>
                                <div><c:out value="${software.description}"/></div>
                                <ul class="topic-list">
                                    <c:forEach items="${software.softwareTopicList}" var="softwareTopic">
                                        <li class="topic"><c:out value="${softwareTopic.topic.name}"/></li>
                                    </c:forEach>
                                </ul>
                            </td>
                            <td><c:out value="${software.maintainerUsernameCsv}"/></td>
                            <td>
                                <c:out value="${software.homeUrl}"/>
                                <div>Archived: ${software.archived}</div>
                            </td>
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
