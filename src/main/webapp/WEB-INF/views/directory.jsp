<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="s" uri="http://jlab.org/jsp/smoothness"%>
<c:set var="title" value="Directory"/>
<s:page title="${title}">
    <jsp:attribute name="stylesheets">
    </jsp:attribute>
    <jsp:attribute name="scripts">
    </jsp:attribute>        
    <jsp:body>
        <section>
            <s:filter-flyout-widget clearButton="true" ribbon="true">
                <form class="filter-form" method="get" action="directory">
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
                            </ul>
                        </fieldset>
                    </div>
                    <input type="hidden" class="offset-input" name="offset" value="0"/>
                    <input class="filter-form-submit-button" type="submit" value="Apply"/>
                </form>
            </s:filter-flyout-widget>
            <h2 class="page-header-title"><c:out value="${title}"/></h2>
            <div class="message-box"><c:out value="${selectionMessage}"/></div>
            <table class="data-table">
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
                        <tr>
                            <td><c:out value="${software.name}"/></td>
                            <td><c:out value="${software.type}"/></td>
                            <td><c:out value="${software.description}"/></td>
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
        </section>
    </jsp:body>         
</s:page>
