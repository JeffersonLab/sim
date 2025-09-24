<%@tag description="Primary Navigation Tag" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib prefix="s" uri="jlab.tags.smoothness"%>
<ul>
    <li${'/directory' eq currentPath ? ' class="current-primary"' : ''}>
        <a href="${pageContext.request.contextPath}/directory">Directory</a>
    </li>
    <li${fn:startsWith(currentPath, '/repositories') ? ' class="current-primary"' : ''}>
        <a href="${pageContext.request.contextPath}/repositories">Repositories</a>
    </li>
    <c:if test="${pageContext.request.isUserInRole('sim-admin')}">
        <li${fn:startsWith(currentPath, '/setup') ? ' class="current-primary"' : ''}>
            <a href="${pageContext.request.contextPath}/setup/settings">Setup</a>
        </li>
    </c:if>
    <li${'/help' eq currentPath ? ' class="current-primary"' : ''}>
        <a href="${pageContext.request.contextPath}/help">Help</a>
    </li>
</ul>