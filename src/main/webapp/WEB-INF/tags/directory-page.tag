<%@tag description="Directory Page" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib prefix="s" uri="jlab.tags.smoothness"%>
<section>
    <s:filter-flyout-widget clearButton="true" ribbon="${'Y' eq param.loose ? 'false' : 'true'}">
        <form id="directory-form" class="filter-form" method="get" action="directory">
            <div class="filter-form-panel">
                <fieldset>
                    <legend>Filter</legend>
                    <ul class="key-value-list">
                        <li>
                            <div class="li-key">
                                <label for="software-name">Software Name</label>
                            </div>
                            <div class="li-value">
                                <input id="software-name"
                                       name="softwareName" value="${fn:escapeXml(param.softwareName)}"/>
                                <div>(use * as wildcard)</div>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="username">Maintainer Username</label>
                            </div>
                            <div class="li-value">
                                <input id="username"
                                       name="username" value="${fn:escapeXml(param.username)}"/>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="repository-select">Repository</label>
                            </div>
                            <div class="li-value">
                                <select id="repository-select" name="repositoryId">
                                    <option value="">&nbsp;</option>
                                    <c:forEach items="${repoList}" var="repo">
                                        <option value="${repo.repositoryId}"${param.repositoryId eq repo.repositoryId ? ' selected="selected"' : ''}>
                                            <c:out value="${repo.name}"/></option>
                                    </c:forEach>
                                </select>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="type-select">Type</label>
                            </div>
                            <div class="li-value">
                                <select id="type-select" name="type">
                                    <option value="">&nbsp;</option>
                                    <c:forEach items="${typeList}" var="type">
                                        <option value="${type}"${param.type eq type ? ' selected="selected"' : ''}>
                                            <c:out value="${type}"/></option>
                                    </c:forEach>
                                </select>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="topic-select">Topics</label>
                            </div>
                            <div class="li-value">
                                <select id="topic-select" name="topic" multiple="multiple">
                                    <c:forEach items="${topicList}" var="topic">
                                        <option value="${topic.name}"${s:inArray(paramValues.topic, topic.name) ? ' selected="selected"' : ''}>
                                            <c:out value="${topic.name}"/></option>
                                    </c:forEach>
                                </select>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="archived-select">Include Archived</label>
                            </div>
                            <div class="li-value">
                                <select id="archived-select" name="archived">
                                    <option value="">&nbsp;</option>
                                    <c:forEach items="${includeList}" var="archived">
                                        <option value="${archived}"${param.archived eq archived ? ' selected="selected"' : ''}>
                                            <c:out value="${archived}"/></option>
                                    </c:forEach>
                                </select>
                                <div>(excluded by default)</div>
                            </div>
                        </li>
                    </ul>
                </fieldset>
            </div>
            <input type="hidden" name="loose" value="${fn:escapeXml(param.loose)}"/>
            <input type="hidden" class="offset-input" name="offset" value="0"/>
            <input class="filter-form-submit-button" type="submit" value="Apply"/>
        </form>
    </s:filter-flyout-widget>
    <h2 class="page-header-title"><c:out value="${title}"/></h2>
    <div class="message-box"><c:out value="${selectionMessage}"/></div>
    <c:set var="readonly" value="${'Y' eq param.loose or !(pageContext.request.isUserInRole('acg') or pageContext.request.isUserInRole('sim-admin'))}"/>
    <c:if test="${not readonly}">
        <s:editable-row-table-controls>
        </s:editable-row-table-controls>
    </c:if>
    <table class="data-table ${readonly ? '' : 'uniselect-table editable-row-table'}">
        <thead>
        <tr>
            <th>Name</th>
            <th>Type</th>
            <th>Description</th>
            <th>Repo</th>
            <th>Maintainers</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="software" items="${softwareList}">
            <tr data-id="${software.softwareId}"
                data-archived="${software.archived ? 'Y' : 'N'}"
                data-name="${fn:escapeXml(software.name)}"
                data-type="${fn:escapeXml(software.type)}"
                data-topic-csv="${fn:escapeXml(software.topicCsv)}"
                data-description="${fn:escapeXml(software.description)}"
                data-note="${fn:escapeXml(software.note)}"
                data-repo-id="${software.repository.repositoryId}"
                data-maintainer-csv="${fn:escapeXml(software.maintainerUsernameCsv)}"
                data-url="${fn:escapeXml(software.homeUrl)}">
                <td>
                    <c:choose>
                        <c:when test="${not empty software.homeUrl}">
                            <a ${'Y' eq param.loose ? 'target="_parent"' : ''} href="${software.homeUrl}"><c:out value="${software.name}"/></a>
                        </c:when>
                        <c:otherwise>
                            <c:out value="${software.name}"/>
                        </c:otherwise>
                    </c:choose>
                    <c:if test="${software.archived}">
                        <span class="archived-symbol">🕸</span>
                    </c:if>
                </td>
                <td><c:out value="${software.type}"/></td>
                <td>
                    <div><c:out value="${software.description}"/></div>
                    <div class="note-div ${empty software.note ? '' : 'non-empty-note'}"><c:out value="${software.note}"/></div>
                    <ul class="topic-list">
                        <c:forEach items="${software.softwareTopicList}" var="softwareTopic">
                            <li class="topic"><c:out value="${softwareTopic.topic.name}"/></li>
                        </c:forEach>
                    </ul>
                </td>
                <td><c:out value="${software.repository.name}"/></td>
                <td><c:out value="${software.maintainerUsernameCsv}"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <button class="previous-button" type="button" data-offset="${paginator.previousOffset}"
            value="Previous"${paginator.previous ? '' : ' disabled="disabled"'}>Previous
    </button>
    <button class="next-button" type="button" data-offset="${paginator.nextOffset}"
            value="Next"${paginator.next ? '' : ' disabled="disabled"'}>Next
    </button>
    <div class="max-select">
        <label for="max-select">Max Per Page</label>
        <select id="max-select" name="max" form="directory-form" class="change-submit">
            <option value="10"${param.max eq 10 ? ' selected="selected"' : ''}>10</option>
            <option value="100"${param.max eq 100 ? ' selected="selected"' : ''}>100</option>
        </select>
    </div>
</section>