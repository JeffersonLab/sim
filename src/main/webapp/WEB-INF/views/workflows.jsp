<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib prefix="s" uri="jlab.tags.smoothness"%>
<c:set var="title" value="Workflows"/>
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
                        <th>SQA Elements Provided</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="workflow" items="${workflowList}">
                        <tr>
                            <td>
                                <span class="workflow-name"><c:out value="${workflow.name}"/></span>
                                (<a href="directory?workflowId=${workflow.workflowId}">Software</a>)
                            </td>
                            <td><c:out value="${workflow.description}"/></td>
                            <%-- Met on the checklist of all the software released with the workflow, unless recorded otherwise --%>
                            <td class="provided-elements">
                                <c:forEach var="provision" items="${provided[workflow.workflowId]}">
                                    <div class="provided-element">
                                        <c:out value="${elementsById[provision.elementId].name}"/>
                                        <div class="provided-note"><c:out value="${provision.note}"/><c:if test="${not empty provision.linkUrl}"> <a href="${fn:escapeXml(provision.linkUrl)}">Link</a></c:if></div>
                                    </div>
                                </c:forEach>
                            </td>
                        </tr>
                    </c:forEach>
                    <tr>
                        <td>
                            <span class="workflow-name">None</span>
                            (<a href="directory?workflowId=NONE">Software</a>)
                        </td>
                        <td>Software whose workflow nobody has set</td>
                        <td class="provided-elements"></td>
                    </tr>
                </tbody>
            </table>
        </section>
    </jsp:body>
</s:page>
