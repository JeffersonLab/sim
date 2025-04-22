<%@tag description="App Script Tag" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/sim.js"></script>
<script>
    $(function() {
        <c:if test="${not empty settings.get('FRONTEND_SERVER_URL_OVERRIDE')}">
        <c:set var="absHostUrl" value="${settings.get('FRONTEND_SERVER_URL_OVERRIDE')}"/>
        <c:url value="/sso" var="loginUrl">
        <c:param name="returnUrl" value="${absHostUrl.concat(domainRelativeReturnUrl)}"/>
        </c:url>
        $("#login-link").attr("href", '${loginUrl}');
        </c:if>

        if (window.self !== window.top) {
            // The page is in an iframe
            $("#login-link").hide();

            $(".ext-link").attr('target', '_parent');
        }
    });
</script>