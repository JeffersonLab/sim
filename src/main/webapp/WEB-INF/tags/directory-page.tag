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
                        <li>
                            <div class="li-key">
                                <label for="level-select">SQA Level</label>
                            </div>
                            <div class="li-value">
                                <select id="level-select" name="level">
                                    <option value="">&nbsp;</option>
                                    <c:forEach items="${levelList}" var="opt">
                                        <option value="${opt.value}"${param.level eq opt.value ? ' selected="selected"' : ''}>
                                            <c:out value="${opt.value} ${opt}"/></option>
                                    </c:forEach>
                                </select>
                                <div>(the override where there is one)</div>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="attention-select">Attention</label>
                            </div>
                            <div class="li-value">
                                <select id="attention-select" name="attention">
                                    <option value="">&nbsp;</option>
                                    <c:forEach items="${attentionList}" var="opt">
                                        <option value="${opt.value}"${param.attention eq opt.value ? ' selected="selected"' : ''}>
                                            <c:out value="${opt}"/></option>
                                    </c:forEach>
                                </select>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="fragile-select">Risk Factors</label>
                            </div>
                            <div class="li-value">
                                <select id="fragile-select" name="fragile">
                                    <option value="">&nbsp;</option>
                                    <option value="Y"${param.fragile eq 'Y' ? ' selected="selected"' : ''}>Any factor &#8805; 4</option>
                                </select>
                            </div>
                        </li>
                        <li>
                            <div class="li-key">
                                <label for="assessed-select">Assessed</label>
                            </div>
                            <div class="li-value">
                                <select id="assessed-select" name="assessed">
                                    <option value="">&nbsp;</option>
                                    <option value="N"${param.assessed eq 'N' ? ' selected="selected"' : ''}>Not assessed</option>
                                    <option value="Y"${param.assessed eq 'Y' ? ' selected="selected"' : ''}>Assessed</option>
                                </select>
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
            <th>SQA Level</th>
            <th>Attention</th>
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
                data-url="${fn:escapeXml(software.homeUrl)}"
                data-level="${software.controlLevel.value}"
                data-level-override="${software.controlLevelOverride.value}"
                data-level-note="${fn:escapeXml(software.controlLevelNote)}"
                data-fragility="${software.fragility.value}"
                data-attention="${software.attention.value}"
                data-assessed-by="${fn:escapeXml(software.assessedBy)}"
                data-assessed-date="<fmt:formatDate value="${software.assessedDate}" pattern="dd-MMM-yyyy HH:mm"/>"
                data-impact="${fn:escapeXml(software.impact.value)}"
                data-rate="${fn:escapeXml(software.rate.value)}"
                data-difficulty="${fn:escapeXml(software.difficulty.value)}"
                data-complexity="${fn:escapeXml(software.complexity.value)}"
                data-gaps="${fn:escapeXml(software.gaps.value)}"
                data-esotericism="${fn:escapeXml(software.esotericism.value)}">
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
                <td>
                    <a class="risk-dialog-opener${software.assessed ? '' : ' not-assessed'}" href="#"><c:choose>
                        <c:when test="${software.assessed}"><c:out value="${software.effectiveControlLevel.value} ${software.effectiveControlLevel}"/><c:if test="${not empty software.controlLevelOverride}"><span class="override-marker" title="Overridden from ${software.controlLevel.value} ${software.controlLevel}: ${fn:escapeXml(software.controlLevelNote)}">*</span></c:if></c:when>
                        <c:otherwise>Not assessed</c:otherwise>
                    </c:choose></a>
                </td>
                <td>
                    <a class="risk-dialog-opener${software.assessed ? '' : ' not-assessed'}" href="#"><c:choose>
                        <c:when test="${software.assessed}"><c:out value="${software.attention}"/></c:when>
                        <c:otherwise>Not assessed</c:otherwise>
                    </c:choose></a>
                </td>
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
<div class="dialog" id="risk-dialog" title="Risk &amp; SQA Assessment">
    <section>
        <input type="hidden" id="risk-software-id"/>
        <div class="dialog-links">
            <a target="_blank" href="https://pages.jlab.org/acc/iac/docs/sqap/latest/04-software-development-life-cycle/#43-software-control-levels">SQAP &sect;4.3</a>
            |
            <a target="_blank" href="https://acgdocs.acc.jlab.org/en/software-directory-guidance#software-risk-guidance">Guidance</a>
        </div>
        <select id="level-labels" style="display: none;">
            <c:forEach items="${levelList}" var="opt">
                <option value="${opt.value}"><c:out value="${opt.value} ${opt}"/></option>
            </c:forEach>
        </select>
        <select id="attention-labels" style="display: none;">
            <c:forEach items="${attentionList}" var="opt">
                <option value="${opt.value}"><c:out value="${opt}"/></option>
            </c:forEach>
        </select>
        <select id="fragility-labels" style="display: none;">
            <c:forEach items="${fragilityList}" var="opt">
                <option value="${opt.value}"><c:out value="${opt} (${opt.value})"/></option>
            </c:forEach>
        </select>
        <fieldset>
            <legend>SQA Control Level = lookup(Impact, Complexity)</legend>
            <ul class="key-value-list">
                <li>
                    <div class="li-key">
                        <label>Derived Level</label>
                    </div>
                    <div class="li-value">
                        <span id="level-visible"></span>
                    </div>
                </li>
                <li>
                    <div class="li-key">
                        <label for="level-override">Override</label>
                    </div>
                    <div class="li-value">
                        <select id="level-override">
                            <option value="">None</option>
                            <c:forEach items="${levelList}" var="opt">
                                <option value="${opt.value}"><c:out value="${opt.value} ${opt}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                </li>
                <li>
                    <div class="li-key">
                        <label for="level-note">Rationale</label>
                    </div>
                    <div class="li-value">
                        <textarea id="level-note" maxlength="512" placeholder="Required with an override: the documented exception the SQAP allows"></textarea>
                    </div>
                </li>
            </ul>
        </fieldset>
        &nbsp;
        <fieldset>
            <legend>Attention = lookup(Impact, Fragility)</legend>
            <ul class="key-value-list">
                <li>
                    <div class="li-key">
                        <label>Fragility</label>
                    </div>
                    <div class="li-value">
                        <span id="fragility-visible"></span>
                        <div class="limiting-factors">Worst risk factor: <span id="limiting-factors"></span></div>
                    </div>
                </li>
                <li>
                    <div class="li-key">
                        <label>Attention</label>
                    </div>
                    <div class="li-value">
                        <span id="attention-visible"></span>
                    </div>
                </li>
            </ul>
        </fieldset>
        &nbsp;
        <fieldset>
            <legend>Risk Inputs</legend>
            <ul class="key-value-list">
                <li>
                    <div class="li-key">
                        <label for="impact">Ops Impact</label>
                    </div>
                    <div class="li-value">
                        <select id="impact">
                            <c:forEach items="${impactList}" var="opt">
                                <option value="${opt.value}"><c:out value="${opt} (${opt.value})"/></option>
                            </c:forEach>
                        </select>
                    </div>
                </li>
                <li>
                    <div class="li-key">
                        <label for="complexity">Code Complexity</label>
                    </div>
                    <div class="li-value">
                        <select id="complexity">
                            <c:forEach items="${complexityList}" var="opt">
                                <option value="${opt.value}"><c:out value="${opt} (${opt.value})"/></option>
                            </c:forEach>
                        </select>
                    </div>
                </li>
                <li>
                    <div class="li-key">
                        <label for="rate">Past Downtime Rate</label>
                    </div>
                    <div class="li-value">
                        <select id="rate">
                            <c:forEach items="${rateList}" var="opt">
                                <option value="${opt.value}"><c:out value="${opt} (${opt.value})"/></option>
                            </c:forEach>
                        </select>
                    </div>
                </li>
                <li>
                    <div class="li-key">
                        <label for="difficulty">Debug/Test Difficulty</label>
                    </div>
                    <div class="li-value">
                        <select id="difficulty">
                            <c:forEach items="${difficultyList}" var="opt">
                                <option value="${opt.value}"><c:out value="${opt} (${opt.value})"/></option>
                            </c:forEach>
                        </select>
                    </div>
                </li>
                <li>
                    <div class="li-key">
                        <label for="gaps">Documentation Gaps</label>
                    </div>
                    <div class="li-value">
                        <select id="gaps">
                            <c:forEach items="${gapList}" var="opt">
                                <option value="${opt.value}"><c:out value="${opt} (${opt.value})"/></option>
                            </c:forEach>
                        </select>
                    </div>
                </li>
                <li>
                    <div class="li-key">
                        <label for="esotericism">Esotericism</label>
                    </div>
                    <div class="li-value">
                        <select id="esotericism">
                            <c:forEach items="${esotericismList}" var="opt">
                                <option value="${opt.value}"><c:out value="${opt} (${opt.value})"/></option>
                            </c:forEach>
                        </select>
                    </div>
                </li>
            </ul>
        </fieldset>
        <div id="assessment-footer"></div>
        <div class="dialog-button-panel">
            <button id="risk-update-button" type="button">Update</button>
            <button class="dialog-close-button" type="button">OK</button>
        </div>
    </section>
</div>