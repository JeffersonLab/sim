var jlab = jlab || {};
jlab.editableRowTable = jlab.editableRowTable || {};
jlab.editableRowTable.entity = 'Software';
jlab.editableRowTable.dialog.width = 650;
jlab.editableRowTable.dialog.height = 650;
/* The row the Edit dialog is open for; null while adding */
jlab.editingRow = null;
jlab.addRow = function() {
    var name = $("#row-name").val(),
        type = $("#row-type").val(),
        topicArray = $("#row-topics").val(),
        description = $("#row-description").val(),
        note = $("#row-note").val(),
        maintainerUsernameCsv = $("#row-maintainers").val(),
        homeUrl = $("#row-url").val(),
        repositoryId = $("#row-repo").val(),
        archived = $("#row-archived").val(),
        reloading = false;

    $(".dialog-submit-button")
        .height($(".dialog-submit-button").height())
        .width($(".dialog-submit-button").width())
        .empty().append('<div class="button-indicator"></div>');
    $(".dialog-close-button").attr("disabled", "disabled");
    $(".ui-dialog-titlebar button").attr("disabled", "disabled");

    var request = jQuery.ajax({
        url: jlab.contextPath + "/ajax/add-software",
        type: "POST",
        data: {
            repositoryId: repositoryId,
            name: name,
            type: type,
            topicArray: topicArray,
            description: description,
            note: note,
            maintainerUsernameCsv: maintainerUsernameCsv,
            homeUrl: homeUrl,
            archived: archived
        },
        dataType: "json"
    });

    request.done(function(json) {
        if (json.stat === 'ok') {
            reloading = true;
            window.location.reload();
        } else {
            alert(json.error);
        }
    });

    request.fail(function(xhr, textStatus) {
        window.console && console.log('Unable to add software; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to Save: Server unavailable or unresponsive');
    });

    request.always(function() {
        if (!reloading) {
            $(".dialog-submit-button").empty().text("Save");
            $(".dialog-close-button").removeAttr("disabled");
            $(".ui-dialog-titlebar button").removeAttr("disabled");
        }
    });
};
jlab.editRow = function() {
    var softwareId = jlab.editingRow.attr("data-id"),
        name = $("#row-name").val(),
        type = $("#row-type").val(),
        topicArray = $("#row-topics").val(),
        description = $("#row-description").val(),
        note = $("#row-note").val(),
        maintainerUsernameCsv = $("#row-maintainers").val(),
        homeUrl = $("#row-url").val(),
        repositoryId = $("#row-repo").val(),
        archived = $("#row-archived").val(),
        reloading = false;

    $(".dialog-submit-button")
        .height($(".dialog-submit-button").height())
        .width($(".dialog-submit-button").width())
        .empty().append('<div class="button-indicator"></div>');
    $(".dialog-close-button").attr("disabled", "disabled");
    $(".ui-dialog-titlebar button").attr("disabled", "disabled");

    var request = jQuery.ajax({
        url: jlab.contextPath + "/ajax/edit-software",
        type: "POST",
        data: {
            softwareId: softwareId,
            repositoryId: repositoryId,
            name: name,
            type: type,
            topicArray: topicArray,
            description: description,
            note: note,
            maintainerUsernameCsv: maintainerUsernameCsv,
            homeUrl: homeUrl,
            archived: archived
        },
        dataType: "json"
    });

    request.done(function(json) {
        if (json.stat === 'ok') {
            reloading = true;
            window.location.reload();
        } else {
            alert(json.error);
        }
    });

    request.fail(function(xhr, textStatus) {
        window.console && console.log('Unable to edit software; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to Save: Server unavailable or unresponsive');
    });

    request.always(function() {
        if (!reloading) {
            $(".dialog-submit-button").empty().text("Save");
            $(".dialog-close-button").removeAttr("disabled");
            $(".ui-dialog-titlebar button").removeAttr("disabled");

        }
    });
};
jlab.removeRow = function() {
    var id = jlab.editingRow.attr("data-id"),
        reloading = false;

    $("#remove-software-button")
        .height($("#remove-software-button").height())
        .width($("#remove-software-button").width())
        .empty().append('<div class="button-indicator"></div>');
    $("#table-row-dialog button, .ui-dialog-titlebar button").attr("disabled", "disabled");

    var request = jQuery.ajax({
        url: jlab.contextPath + "/ajax/remove-software",
        type: "POST",
        data: {
            softwareId: id
        },
        dataType: "json"
    });

    request.done(function(json) {
        if (json.stat === 'ok') {
            reloading = true;
            window.location.reload();
        } else {
            alert(json.error);
        }
    });

    request.fail(function(xhr, textStatus) {
        window.console && console.log('Unable to remove software; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to Remove: Server unavailable or unresponsive');
    });

    request.always(function() {
        if (!reloading) {
            $("#remove-software-button").empty().text("Remove");
            $("#table-row-dialog button, .ui-dialog-titlebar button").removeAttr("disabled");
        }
    });
};
$(document).on("dialogclose", "#table-row-dialog", function() {
    $("#row-form")[0].reset();

    $("#row-topics").val(null).trigger("change");

    $("#row-name").removeAttr("disabled");
    $("#row-repo").removeAttr("disabled");

    $("#remove-software-button").hide();
    jlab.editingRow = null;
});
jlab.openEditDialog = function($row) {
    var topicCsv = $row.attr("data-topic-csv") || "";

    jlab.editingRow = $row;

    $("#row-name").val($row.attr("data-name"));
    $("#row-type").val($row.attr("data-type"));
    $("#row-description").val($row.attr("data-description"));
    $("#row-note").val($row.attr("data-note"));
    $("#row-maintainers").val($row.attr("data-maintainer-csv"));
    $("#row-repo").val($row.attr("data-repo-id"));
    $("#row-topics").val(topicCsv.split(",")).trigger('change');
    $("#row-url").val($row.attr("data-url"));
    $("#row-archived").val($row.attr("data-archived"));

    $("#row-name").attr("disabled", "disabled");
    $("#row-repo").attr("disabled", "disabled");

    $("#remove-software-button").show();

    $("#table-row-dialog").dialog("option", "title", "Edit " + jlab.editableRowTable.entity).dialog("open");
};
/* A click on an editor's Name, Description or Maintainers cell opens Edit for its row, except on a
 * link or button in the cell, or after dragging to select text. There is no row selection: Remove
 * is in the Edit dialog, since archiving is usually what's wanted */
$(document).on("click", ".editable-row-table td.edit-opener", function(e) {
    if ($(e.target).closest("a, button").length > 0 || window.getSelection().toString() !== "") {
        return;
    }
    jlab.openEditDialog($(this).closest("tr"));
});
$(document).on("table-row-add", function() {
    jlab.addRow();
});
$(document).on("table-row-edit", function() {
    jlab.editRow();
});
$(document).on("click", "#remove-software-button", function() {
    var name = jlab.editingRow.attr("data-name");
    if (confirm('Remove ' + name + ' for good, with its topics, assessment and SQA elements?\n\n'
            + 'To retire software, set Archived to Yes instead.')) {
        jlab.removeRow();
    }
});
$(document).on("click", ".default-clear-panel", function () {
    $("#software-name").val('');
    $("#username").val('');
    $("#repository-select").val('');
    $("#topic-select").val(null).trigger('change');
    $("#type-select").val('');
    $("#archived-select").val('');
    $("#level-select").val('');
    $("#attention-select").val('');
    $("#fragile-select").val('');
    $("#assessed-select").val('');
    $("#gap-select").val('');
    return false;
});
/* The lookup tables of container/oracle/initdb.d/02_ddl.sql, which derives the stored values; these
 * only preview the dialog's current inputs. Rows are Ops Impact 1-5, columns Code Complexity or
 * Fragility 1-5. */
jlab.risk = {
    levelTable: [
        [0, 0, 1, 2, 2],
        [0, 0, 1, 2, 2],
        [1, 1, 2, 3, 3],
        [2, 2, 3, 3, 3],
        [3, 3, 3, 3, 3]
    ],
    attentionTable: [
        [1, 1, 1, 2, 2],
        [1, 1, 2, 2, 3],
        [1, 2, 2, 3, 3],
        [2, 2, 3, 3, 4],
        [2, 3, 3, 4, 4]
    ],
    factors: [
        ["rate", "Past Downtime Rate"],
        ["difficulty", "Debug/Test Difficulty"],
        ["complexity", "Code Complexity"],
        ["gaps", "Documentation Gaps"],
        ["esotericism", "Esotericism"]
    ],
    label: function (selectId, value) {
        return $("#" + selectId + " option[value='" + value + "']").text().trim();
    },
    refresh: function () {
        var impact = parseInt($("#impact").val(), 10),
            complexity = parseInt($("#complexity").val(), 10),
            fragility = 0,
            limiting = [],
            override = $("#level-override").val(),
            level, attention;

        jlab.risk.factors.forEach(function (factor) {
            var value = parseInt($("#" + factor[0]).val(), 10);
            if (value > fragility) {
                fragility = value;
                limiting = [factor[1]];
            } else if (value === fragility) {
                limiting.push(factor[1]);
            }
        });

        level = jlab.risk.levelTable[impact - 1][complexity - 1];
        attention = jlab.risk.attentionTable[impact - 1][fragility - 1];

        var levelText = jlab.risk.label("level-labels", level);
        if (override !== "") {
            levelText = levelText + " (overridden to " + jlab.risk.label("level-labels", override) + ")";
        }
        $("#level-visible").text(levelText);
        $("#fragility-visible").text(jlab.risk.label("fragility-labels", fragility));
        $("#limiting-factors").text(limiting.join(", "));
        $("#attention-visible").text(jlab.risk.label("attention-labels", attention));
    }
};
$(document).on("change", "#risk-dialog select", function () {
    jlab.risk.refresh();
});
$(document).on("change", ".change-submit", function () {
    let formId = $(this).attr("form");
    document.getElementById(formId).submit();
});
$(document).on("click", "#risk-save-button", function() {
    var softwareId = $("#risk-software-id").val(),
        impact = $("#impact").val(),
        rate = $("#rate").val(),
        difficulty = $("#difficulty").val(),
        complexity = $("#complexity").val(),
        gaps = $("#gaps").val(),
        esotericism = $("#esotericism").val(),
        levelOverride = $("#level-override").val(),
        levelNote = $("#level-note").val(),
        $saveButton = $("#risk-save-button"),
        reloading = false;

    if (levelOverride !== "" && levelNote.trim() === "") {
        alert("A control level override needs a rationale");
        return;
    }

    $saveButton
        .height($saveButton.height())
        .width($saveButton.width())
        .empty().append('<div class="button-indicator"></div>');
    $(".dialog-close-button").attr("disabled", "disabled");
    $(".ui-dialog-titlebar button").attr("disabled", "disabled");

    var request = jQuery.ajax({
        url: jlab.contextPath + "/ajax/edit-software-risk",
        type: "POST",
        data: {
            softwareId: softwareId,
            impact: impact,
            rate: rate,
            difficulty: difficulty,
            complexity: complexity,
            gaps: gaps,
            esotericism: esotericism,
            levelOverride: levelOverride,
            levelNote: levelNote
        },
        dataType: "json"
    });

    request.done(function(json) {
        if (json.stat === 'ok') {
            reloading = true;
            window.location.reload();
        } else {
            alert(json.error);
        }
    });

    request.fail(function(xhr, textStatus) {
        window.console && console.log('Unable to edit software; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to Save: Server unavailable or unresponsive');
    });

    request.always(function() {
        if (!reloading) {
            $saveButton.empty().text("Save");
            $(".dialog-close-button").removeAttr("disabled");
            $(".ui-dialog-titlebar button").removeAttr("disabled");

        }
    });
});
$(document).on("click", ".risk-dialog-opener", function () {
    let $tr = $(this).closest("tr");

    $("#risk-software-id").val($tr.attr("data-id"));

    $("#risk-dialog").dialog("option", "title", $tr.attr("data-name") + " - Risk & SQA Assessment");

    $("#impact").val($tr.attr("data-impact"));
    $("#rate").val($tr.attr("data-rate"));
    $("#difficulty").val($tr.attr("data-difficulty"));
    $("#complexity").val($tr.attr("data-complexity"));
    $("#gaps").val($tr.attr("data-gaps"));
    $("#esotericism").val($tr.attr("data-esotericism"));
    $("#level-override").val($tr.attr("data-level-override") || "");
    $("#level-note").val($tr.attr("data-level-note") || "");

    jlab.risk.refresh();

    if ($tr.attr("data-assessed-date")) {
        $("#assessment-footer").text("Assessed by " + ($tr.attr("data-assessed-by") || "an unknown user")
            + " on " + $tr.attr("data-assessed-date"));
    } else {
        $("#assessment-footer").text("Not assessed yet: the inputs are the defaults");
    }

    let editable = $("#risk-dialog").attr("data-editable") === "Y",
        $controls = $("#risk-dialog fieldset select, #level-note");

    if(editable) {
        $controls.removeAttr("disabled");
    } else {
        $controls.attr("disabled", "disabled");
    }

    $("#risk-dialog").dialog("open");
    return false;
});
/* The checklist dialog: the row's data-checklist lists its elements in order, each with its
 * requirement and what was recorded (Directory.java); the dialog has a row for every element */
$(document).on("click", ".elements-dialog-opener", function () {
    let $tr = $(this).closest("tr"),
        checklist = JSON.parse($tr.attr("data-checklist") || "[]"),
        $tbody = $("#elements-table tbody"),
        editable = $("#elements-dialog").attr("data-editable") === "Y",
        level = $tr.attr("data-level-override") || $tr.attr("data-level");

    $("#elements-software-id").val($tr.attr("data-id"));
    $("#elements-dialog").dialog("option", "title", $tr.attr("data-name") + " - SQA Elements");
    $("#elements-level").text(jlab.risk.label("level-labels", level));

    $tbody.find(".element-row").removeClass("on-checklist").hide();

    checklist.forEach(function (item) {
        let $row = $tbody.find(".element-row").filter(function () {
            return $(this).attr("data-element-id") === item.id;
        });

        $row.find(".element-requirement").text(item.requirement === "REQUIRED" ? "Required" : "Recommended");
        $row.find(".element-status").val(item.status || "");
        $row.find(".element-link").val(item.link || "");
        $row.find(".element-note").val(item.note || "");
        $row.find(".element-recorded").text(item.status ? "by " + item.by + " on " + item.date : "");

        /* An element the repository provides for all its software is met without a record: it
         * takes the place of Not recorded, and a status recorded for this software replaces it */
        let $provided = $row.find(".element-provided").empty();
        $row.find(".element-status option[value='']").text(item.provider ? "Met, by " + item.provider : "Not recorded");
        if (item.provider && item.status) {
            $provided.text("Otherwise met by " + item.provider);
        } else if (item.provider) {
            $provided.text(item.providerNote);
            if (/^https?:\/\//.test(item.providerLink || "")) {
                $provided.append(" ").append($("<a target=\"_blank\"></a>").attr("href", item.providerLink).text("Link"));
            }
        }
        $row.toggleClass("recommended", item.requirement !== "REQUIRED");
        /* In the checklist's order: required elements first */
        $row.addClass("on-checklist").appendTo($tbody).show();
    });

    let $controls = $("#elements-table select, #elements-table input");

    if (editable) {
        $controls.removeAttr("disabled");
    } else {
        $controls.attr("disabled", "disabled");
    }

    $("#elements-dialog").dialog("open");
    return false;
});
$(document).on("click", "#elements-save-button", function () {
    var softwareId = $("#elements-software-id").val(),
        elementId = [],
        status = [],
        link = [],
        note = [],
        problem = null,
        $saveButton = $("#elements-save-button"),
        reloading = false;

    $("#elements-table .element-row.on-checklist").each(function () {
        let $row = $(this),
            rowStatus = $row.find(".element-status").val(),
            rowLink = $row.find(".element-link").val().trim(),
            rowNote = $row.find(".element-note").val().trim(),
            name = '"' + $row.attr("data-element-name") + '"';

        if (rowStatus === "NOT_APPLICABLE" && rowNote === "") {
            problem = problem || name + " needs a note saying why it is not applicable";
        } else if (rowStatus === "" && (rowLink !== "" || rowNote !== "")) {
            problem = problem || name + " needs a status to keep its link or note";
        }

        elementId.push($row.attr("data-element-id"));
        status.push(rowStatus);
        link.push(rowLink);
        note.push(rowNote);
    });

    if (problem !== null) {
        alert(problem);
        return;
    }

    $saveButton
        .height($saveButton.height())
        .width($saveButton.width())
        .empty().append('<div class="button-indicator"></div>');
    $(".dialog-close-button").attr("disabled", "disabled");
    $(".ui-dialog-titlebar button").attr("disabled", "disabled");

    var request = jQuery.ajax({
        url: jlab.contextPath + "/ajax/edit-software-elements",
        type: "POST",
        data: {
            softwareId: softwareId,
            elementId: elementId,
            status: status,
            link: link,
            note: note
        },
        dataType: "json"
    });

    request.done(function(json) {
        if (json.stat === 'ok') {
            reloading = true;
            window.location.reload();
        } else {
            alert(json.error);
        }
    });

    request.fail(function(xhr, textStatus) {
        window.console && console.log('Unable to record SQA elements; Text Status: ' + textStatus + ', Ready State: ' + xhr.readyState + ', HTTP Status Code: ' + xhr.status);
        alert('Unable to Save: Server unavailable or unresponsive');
    });

    request.always(function() {
        if (!reloading) {
            $saveButton.empty().text("Save");
            $(".dialog-close-button").removeAttr("disabled");
            $(".ui-dialog-titlebar button").removeAttr("disabled");
        }
    });
});
$(document).on("click", ".topic-more", function () {
    let $button = $(this),
        expanded = $button.closest(".topic-list").toggleClass("expanded").hasClass("expanded");

    $button.attr("aria-expanded", expanded ? "true" : "false")
        .text(expanded ? "less" : $button.attr("data-more-text"));
    return false;
});
$(function(){
    $("#row-topics").select2({
        tags: true
    });

    $("#topic-select").select2();

    $("#risk-dialog").dialog({
        autoOpen: false,
        minWidth: 640,
        width: 640,
        height: "auto",
        modal: true,
        draggable: false
    });

    $("#elements-dialog").dialog({
        autoOpen: false,
        minWidth: 900,
        width: 900,
        height: "auto",
        modal: true,
        draggable: false
    });
});