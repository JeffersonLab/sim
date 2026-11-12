var jlab = jlab || {};
jlab.editableRowTable = jlab.editableRowTable || {};
jlab.editableRowTable.entity = 'Software';
jlab.editableRowTable.dialog.width = 650;
jlab.editableRowTable.dialog.height = 650;
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
    var softwareId = $(".editable-row-table tr.selected-row").attr("data-id"),
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
    var id = $(".editable-row-table tr.selected-row").attr("data-id"),
        reloading = false;

    $("#remove-row-button")
        .height($("#remove-row-button").height())
        .width($("#remove-row-button").width())
        .empty().append('<div class="button-indicator"></div>');

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
            $("#remove-row-button").empty().text("Remove");
        }
    });
};
$(document).on("dialogclose", "#table-row-dialog", function() {
    $("#row-form")[0].reset();

    $("#row-topics").val(null).trigger("change");

    $("#row-name").removeAttr("disabled");
    $("#row-repo").removeAttr("disabled");
});
$(document).on("click", "#open-edit-row-dialog-button", function() {
    var $selectedRow = $(".editable-row-table tr.selected-row"),
        topicCsv = $selectedRow.attr("data-topic-csv") || "";
    $("#row-name").val($selectedRow.attr("data-name"));
    $("#row-type").val($selectedRow.attr("data-type"));
    $("#row-description").val($selectedRow.attr("data-description"));
    $("#row-note").val($selectedRow.attr("data-note"));
    $("#row-maintainers").val($selectedRow.attr("data-maintainer-csv"));
    $("#row-repo").val($selectedRow.attr("data-repo-id"));
    $("#row-topics").val(topicCsv.split(",")).trigger('change');
    $("#row-url").val($selectedRow.attr("data-url"));
    $("#row-archived").val($selectedRow.attr("data-archived"));

    $("#row-name").attr("disabled", "disabled");
    $("#row-repo").attr("disabled", "disabled");
});
$(document).on("table-row-add", function() {
    jlab.addRow();
});
$(document).on("table-row-edit", function() {
    jlab.editRow();
});
$(document).on("click", "#remove-row-button", function() {
    var name = $(".editable-row-table tr.selected-row").attr("data-name");
    if (confirm('Are you sure you want to remove ' + name + '?')) {
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
$(document).on("click", "#risk-update-button", function() {
    var softwareId = $("#risk-software-id").val(),
        impact = $("#impact").val(),
        rate = $("#rate").val(),
        difficulty = $("#difficulty").val(),
        complexity = $("#complexity").val(),
        gaps = $("#gaps").val(),
        esotericism = $("#esotericism").val(),
        levelOverride = $("#level-override").val(),
        levelNote = $("#level-note").val(),
        $updateButton = $("#risk-update-button"),
        reloading = false;

    if (levelOverride !== "" && levelNote.trim() === "") {
        alert("A control level override needs a rationale");
        return;
    }

    $updateButton
        .height($updateButton.height())
        .width($updateButton.width())
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
            $updateButton.empty().text("Update");
            $updateButton.removeAttr("disabled");
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

    let editable = $("#logout-form").length,
        $controls = $("#risk-update-button, #risk-dialog fieldset select, #level-note");

    if(editable) {
        $controls.removeAttr("disabled");
    } else {
        $controls.attr("disabled", "disabled");
    }

    $("#risk-dialog").dialog("open");
    return false;
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
});