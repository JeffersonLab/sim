<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@taglib prefix="s" uri="jlab.tags.smoothness"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<c:set var="title" value="Directory"/>
<s:page title="${title}">
    <jsp:attribute name="stylesheets">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/css/directory.css"/>
    </jsp:attribute>
    <jsp:attribute name="scripts">
        <script type="text/javascript" src="${pageContext.request.contextPath}/resources/v${initParam.releaseNumber}/js/directory.js"></script>
    </jsp:attribute>
    <jsp:body>
        <t:directory-page/>
        <s:editable-row-table-dialog>
            <section>
                <form id="row-form">
                    <ul class="key-value-list">
                        <li>
                            <div class="li-key">
                                <label for="row-name">Name</label>
                            </div>
                            <div class="li-value">
                                <input type="text" id="row-name"/>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="row-type">Type</label>
                            </div>
                            <div class="li-value">
                                <select id="row-type" required="required">
                                    <option value="">&nbsp;</option>
                                    <c:forEach items="${typeList}" var="type">
                                        <option value="${type}">
                                            <c:out value="${type}"/></option>
                                    </c:forEach>
                                </select>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="row-description">Description</label>
                            </div>
                            <div class="li-value">
                                <textarea id="row-description"></textarea>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="row-repo">Repo</label>
                            </div>
                            <div class="li-value">
                                <select id="row-repo" required="required">
                                    <option value="">&nbsp;</option>
                                    <c:forEach items="${repoList}" var="repo">
                                        <option value="${repo.repositoryId}">
                                            <c:out value="${repo.name}"/></option>
                                    </c:forEach>
                                </select>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="row-topics">Topics</label>
                            </div>
                            <div class="li-value">
                                <div class="topic-scroll-wrap">
                                <select id="row-topics" multiple="multiple">
                                    <c:forEach items="${topicList}" var="topic">
                                        <option value="${topic.name}">
                                            <c:out value="${topic.name}"/></option>
                                    </c:forEach>
                                </select>
                                </div>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="row-maintainers">Maintainers</label>
                            </div>
                            <div class="li-value">
                                <input type="text" id="row-maintainers" placeholder="CSV of usernames"/>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="row-url">Home URL</label>
                            </div>
                            <div class="li-value">
                                <input type="text" id="row-url"/>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="row-archived">Archived</label>
                            </div>
                            <div class="li-value">
                                <select id="row-archived" required="required">
                                    <option value="N">No</option>
                                    <option value="Y">Yes</option>
                                </select>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="row-note">Note</label>
                            </div>
                            <div class="li-value">
                                <textarea id="row-note"></textarea>
                            </div>
                        </li>
                    </ul>
                </form>
            </section>
        </s:editable-row-table-dialog>
    </jsp:body>         
</s:page>
