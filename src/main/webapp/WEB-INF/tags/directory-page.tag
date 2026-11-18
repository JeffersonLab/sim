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
                                <label for="workflow-select">Workflow</label>
                            </div>
                            <div class="li-value">
                                <%-- Compare as strings: EL can't turn NONE into a number to compare with an id --%>
                                <select id="workflow-select" name="workflowId">
                                    <option value="">&nbsp;</option>
                                    <c:forEach items="${workflowList}" var="workflow">
                                        <option value="${workflow.workflowId}"${param.workflowId eq ('' += workflow.workflowId) ? ' selected="selected"' : ''}>
                                            <c:out value="${workflow.name}"/></option>
                                    </c:forEach>
                                    <option value="NONE"${param.workflowId eq 'NONE' ? ' selected="selected"' : ''}>None</option>
                                </select>
                                <div>(how the software is released)</div>
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
                                <%-- Compare as strings: EL turns a blank level= into 0 when compared with a number, selecting 0 Minimal --%>
                                <select id="level-select" name="level">
                                    <option value="">&nbsp;</option>
                                    <c:forEach items="${levelList}" var="opt">
                                        <option value="${opt.value}"${param.level eq ('' += opt.value) ? ' selected="selected"' : ''}>
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
                                        <option value="${opt.value}"${param.attention eq ('' += opt.value) ? ' selected="selected"' : ''}>
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
                        <li>
                            <div class="li-key">
                                <label for="gap-select">Gap in SQA Element</label>
                            </div>
                            <div class="li-value">
                                <select id="gap-select" name="gap">
                                    <option value="">&nbsp;</option>
                                    <option value="ANY"${param.gap eq 'ANY' ? ' selected="selected"' : ''}>Any element</option>
                                    <c:forEach items="${elementList}" var="element">
                                        <option value="${fn:escapeXml(element.elementId)}" title="${fn:escapeXml(element.name)}"${param.gap eq element.elementId ? ' selected="selected"' : ''}>
                                            <c:out value="${element.shortName}"/></option>
                                    </c:forEach>
                                </select>
                                <div>(recorded as a gap, at the software's level)</div>
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
        <%-- Add only: a click on a row opens Edit, which also offers Remove (directory.js) --%>
        <s:editable-row-table-controls excludeEdit="true" excludeDelete="true">
            <%-- The hover highlight and tooltips show only on hover, and never on a touch screen --%>
            <span class="row-edit-hint">Click a row to edit it</span>
        </s:editable-row-table-controls>
    </c:if>
    <c:set var="editOpener" value="${readonly ? '' : ' edit-opener'}"/>
    <table class="data-table ${readonly ? '' : 'editable-row-table'}">
        <thead>
        <tr>
            <th>Name</th>
            <th>Description</th>
            <th>Maintainers</th>
            <th>Risk &amp; SQA</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="software" items="${softwareList}">
            <%-- For editors, a click on the Name, Description or Maintainers cell opens Edit for its row (directory.js) --%>
            <tr data-id="${software.softwareId}"
                data-archived="${software.archived ? 'Y' : 'N'}"
                data-name="${fn:escapeXml(software.name)}"
                data-type="${fn:escapeXml(software.type)}"
                data-topic-csv="${fn:escapeXml(software.topicCsv)}"
                data-description="${fn:escapeXml(software.description)}"
                data-note="${fn:escapeXml(software.note)}"
                data-repo-id="${software.repository.repositoryId}"
                data-workflow-id="${software.workflow.workflowId}"
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
                data-esotericism="${fn:escapeXml(software.esotericism.value)}"
                data-checklist="${fn:escapeXml(checklistJson[software.softwareId])}">
                <td class="name${editOpener}"<c:if test="${not readonly}"> title="Click to edit"</c:if>>
                    <c:choose>
                        <c:when test="${not empty software.homeUrl}">
                            <a ${'Y' eq param.loose ? 'target="_parent"' : ''} href="${software.homeUrl}" title="Open home page"><c:out value="${software.name}"/></a>
                        </c:when>
                        <c:otherwise>
                            <c:out value="${software.name}"/>
                        </c:otherwise>
                    </c:choose>
                    <c:if test="${software.archived}">
                        <span class="archived-symbol">🕸</span>
                    </c:if>
                    <div class="name-meta"><span class="type"><c:out value="${software.type}"/></span> · <span class="repo"><c:out value="${software.repository.name}"/></span></div>
                    <%-- How it is released, where somebody or its sync has said --%>
                    <c:if test="${not empty software.workflow}">
                        <div class="name-meta workflow" title="${fn:escapeXml(software.workflow.description)}"><c:out value="${software.workflow.name}"/> workflow</div>
                    </c:if>
                </td>
                <td class="description${editOpener}"<c:if test="${not readonly}"> title="Click to edit"</c:if>>
                    <div><c:out value="${software.description}"/></div>
                    <div class="note-div ${empty software.note ? '' : 'non-empty-note'}"><c:out value="${software.note}"/></div>
                    <%-- Topics are an aside: show the first three, and the rest behind "+N more",
                         unless only one would be hidden, which would save no room --%>
                    <c:set var="topicCount" value="${fn:length(software.softwareTopicList)}"/>
                    <c:set var="shownTopics" value="${topicCount ge 5 ? 3 : topicCount}"/>
                    <ul class="topic-list">
                        <c:forEach items="${software.softwareTopicList}" var="softwareTopic" varStatus="topicStatus">
                            <li class="topic${topicStatus.index ge shownTopics ? ' extra-topic' : ''}"><c:out value="${softwareTopic.topic.name}"/></li>
                        </c:forEach>
                        <c:if test="${topicCount gt shownTopics}">
                            <li><button type="button" class="topic-more" aria-expanded="false" data-more-text="+${topicCount - shownTopics} more">+${topicCount - shownTopics} more</button></li>
                        </c:if>
                    </ul>
                </td>
                <td class="maintainers${editOpener}"<c:if test="${not readonly}"> title="Click to edit"</c:if>><c:forEach items="${software.maintainerUsernameList}" var="username"><div><c:out value="${username}"/></div></c:forEach></td>
                <%-- One assessment gives all three, and one dialog edits it --%>
                <td class="risk">
                    <a class="risk-dialog-opener${software.assessed ? '' : ' not-assessed'}" href="#"><c:choose>
                        <c:when test="${software.assessed}">
                            <span class="risk-key">Ops Impact</span>
                            <span class="ops-impact"><c:out value="${software.impact.value} ${software.impact}"/></span>
                            <span class="risk-key">SQA Level</span>
                            <span class="sqa-level"><c:out value="${software.effectiveControlLevel.value} ${software.effectiveControlLevel}"/><c:if test="${not empty software.controlLevelOverride}"><span class="override-marker" title="Overridden from ${software.controlLevel.value} ${software.controlLevel}: ${fn:escapeXml(software.controlLevelNote)}">*</span></c:if></span>
                            <span class="risk-key">Attention</span>
                            <span class="attention"><c:out value="${software.attention}"/></span>
                        </c:when>
                        <c:otherwise>Not assessed</c:otherwise>
                    </c:choose></a>
                    <%-- The checklist follows the control level, so only assessed software has one --%>
                    <c:set var="checklist" value="${checklists[software.softwareId]}"/>
                    <c:if test="${not empty checklist}">
                        <a class="elements-dialog-opener${checklist.recorded ? '' : ' not-recorded'}" href="#" title="Required SQA elements met, of those that apply"><span class="risk-key">SQA Elements</span>
                            <span class="sqa-elements"><c:choose>
                                <c:when test="${checklist.recorded}"><c:out value="${checklist.summary}"/><c:if test="${checklist.gapCount gt 0}"> <span class="gap-count">(<c:out value="${checklist.gapCount}"/> ${checklist.gapCount eq 1 ? 'gap' : 'gaps'})</span></c:if></c:when>
                                <c:otherwise>Not recorded</c:otherwise>
                            </c:choose></span></a>
                    </c:if>
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
<%-- Editable for the same people as the table: directory.js enables the inputs from this --%>
<div class="dialog" id="risk-dialog" title="Risk &amp; SQA Assessment" data-editable="${readonly ? 'N' : 'Y'}">
    <section>
        <input type="hidden" id="risk-software-id"/>
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
            <div class="dialog-links">
                <a target="_blank" href="${fn:escapeXml(sqapControlLevelsUrl)}">SQAP &sect;4.3</a>
                |
                <a target="_blank" href="https://jeffersonlab.github.io/sim/v${initParam.releaseNumber}/risk-assessment/#rating-the-risk-inputs">Guidance</a>
            </div>
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
        <fieldset class="risk-inputs">
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
        <%-- Save and Cancel, as in the software dialog, so closing isn't mistaken for saving --%>
        <div class="dialog-button-panel">
            <c:choose>
                <c:when test="${readonly}">
                    <button class="dialog-close-button" type="button">Close</button>
                </c:when>
                <c:otherwise>
                    <button id="risk-save-button" type="button">Save</button>
                    <button class="dialog-close-button" type="button">Cancel</button>
                </c:otherwise>
            </c:choose>
        </div>
    </section>
</div>
<%-- A product's checklist of SQA elements. Every element is here once; directory.js shows those of
     the row's checklist, in its order, and fills in what was recorded --%>
<div class="dialog" id="elements-dialog" title="SQA Elements" data-editable="${readonly ? 'N' : 'Y'}">
    <section>
        <input type="hidden" id="elements-software-id"/>
        <div class="dialog-links">
            <a target="_blank" href="${fn:escapeXml(sqapStagesUrl)}">SQAP &sect;4.4</a>
        </div>
        <p id="elements-intro">The SQA elements the SQAP asks of a product at control level <span id="elements-level"></span>: <span id="elements-counts"></span>. An element nobody has recorded is not a gap. Hover over an element for the SQAP's wording.</p>
        <%-- Not a data-table: the Directory is the page's only one, which its scripts and tests rely on.
             The Full level's checklist is taller than most windows, so the table scrolls, not the dialog --%>
        <div id="elements-scroll">
        <table id="elements-table">
            <colgroup>
                <col class="element-col"/>
                <col class="status-col"/>
                <col class="link-col"/>
                <col class="note-col"/>
            </colgroup>
            <thead>
            <tr>
                <th>Element</th>
                <th>Status</th>
                <th>Link</th>
                <th>Note</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${elementList}" var="element">
                <tr class="element-row" data-element-id="${fn:escapeXml(element.elementId)}" data-element-name="${fn:escapeXml(element.shortName)}">
                    <td class="element-name">
                        <div><span class="element-short" title="${fn:escapeXml(element.name)}"><c:out value="${element.shortName}"/></span></div>
                        <div class="element-meta"><c:out value="${element.phase}"/> · <a target="_blank" href="${fn:escapeXml(element.sectionUrl)}">&sect;<c:out value="${element.sectionNumber}"/></a></div>
                    </td>
                    <td>
                        <select class="element-status" aria-label="Status of ${fn:escapeXml(element.shortName)}">
                            <option value="">Not recorded</option>
                            <c:forEach items="${elementStatusList}" var="opt">
                                <option value="${opt}"><c:out value="${opt.label}"/></option>
                            </c:forEach>
                        </select>
                        <div class="element-recorded"></div>
                        <div class="element-provided"></div>
                    </td>
                    <td>
                        <div class="element-link-cell">
                            <input type="url" class="element-link" maxlength="512" placeholder="https://&hellip;" aria-label="Link for ${fn:escapeXml(element.shortName)}"/>
                            <a class="element-link-open" target="_blank" rel="noopener noreferrer" title="Open the link in a new window" hidden>Open</a>
                        </div>
                        <div class="element-evidence"><c:out value="${element.evidence}"/></div>
                    </td>
                    <td><input type="text" class="element-note" maxlength="512" aria-label="Note for ${fn:escapeXml(element.shortName)}"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        </div>
        <div id="elements-footer">Link: the evidence of an element that is met, which the SQAP names in the line under it, or the issue that tracks a gap. Not applicable needs a note saying why. An element met by a workflow or a repository is one it provides for all its software; a status recorded here replaces it. SIM sets Ratings recorded and Two maintainers from the assessment and the Maintainers column.<c:if test="${not empty sqapVersion}"> Elements of SQAP revision <c:out value="${sqapVersion}"/>.</c:if></div>
        <div class="dialog-button-panel">
            <c:choose>
                <c:when test="${readonly}">
                    <button class="dialog-close-button" type="button">Close</button>
                </c:when>
                <c:otherwise>
                    <button id="elements-save-button" type="button">Save</button>
                    <button class="dialog-close-button" type="button">Cancel</button>
                </c:otherwise>
            </c:choose>
        </div>
    </section>
</div>
